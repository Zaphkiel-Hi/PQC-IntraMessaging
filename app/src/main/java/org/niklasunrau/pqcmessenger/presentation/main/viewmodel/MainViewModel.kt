package org.niklasunrau.pqcmessenger.presentation.main.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.niklasunrau.pqcmessenger.R
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricPublicKey
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricSecretKey
import org.niklasunrau.pqcmessenger.domain.crypto.aes.AES
import org.niklasunrau.pqcmessenger.domain.crypto.toBitArray
import org.niklasunrau.pqcmessenger.domain.crypto.toSecretKey
import org.niklasunrau.pqcmessenger.domain.model.Chat
import org.niklasunrau.pqcmessenger.domain.model.Message
import org.niklasunrau.pqcmessenger.domain.model.User
import org.niklasunrau.pqcmessenger.domain.repository.AuthRepository
import org.niklasunrau.pqcmessenger.domain.repository.ChatRepository
import org.niklasunrau.pqcmessenger.domain.repository.UserRepository
import org.niklasunrau.pqcmessenger.domain.util.Algorithm
import org.niklasunrau.pqcmessenger.domain.util.ChatType
import org.niklasunrau.pqcmessenger.domain.util.Json.json
import org.niklasunrau.pqcmessenger.domain.util.Route
import org.niklasunrau.pqcmessenger.presentation.util.DecryptedMessage
import org.niklasunrau.pqcmessenger.presentation.util.NavigationItem
import org.niklasunrau.pqcmessenger.presentation.util.UiText
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUIState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val currentUser = userRepository.getUserById(authRepository.currentUserId)!!
            _uiState.update { it.copy(loggedInUser = currentUser) }


            initKeys(savedStateHandle["password"])

            val chats = chatRepository.getUserChats(currentUser.id)
            val idToChat = mutableMapOf<String, Chat>()
            for (chat in chats) {
                if (chat.type == ChatType.SINGLE) {
                    val otherUser = saveOtherUser(chat)
                    val newChat = chat.copy(name = otherUser.username, icon = otherUser.image)
                    idToChat[newChat.id] = newChat
                } else {
                    TODO("groups exist")
                }

            }
            _uiState.update { it.copy(idToChat = idToChat) }


        }
    }

    private suspend fun initKeys(password: String?) {
        withContext(Dispatchers.Default) {
            _uiState.update { it.copy(isLoading = true) }
            if (password.isNullOrEmpty()) return@withContext
            if (_uiState.value.loggedInUserSecretKeys.isNotEmpty()) return@withContext

            val encryptedMap = _uiState.value.loggedInUser.encryptedSecretKeys
            val secretKeys = mutableMapOf<Algorithm.Type, AsymmetricSecretKey>()
            for ((name, cipher) in encryptedMap) {
                val type = Algorithm.Type.valueOf(name)
                val decrypted = AES.decrypt(cipher, password)
                val secretKey = json.decodeFromString<AsymmetricSecretKey>(decrypted)
                secretKeys[type] = secretKey
            }
            _uiState.update { it.copy(loggedInUserSecretKeys = secretKeys, isLoading = false) }
        }
    }

    fun getOtherUserId(chat: Chat) =
        if (chat.users[0] != _uiState.value.loggedInUser.id) chat.users[0] else chat.users[1]

    private suspend fun saveOtherUser(chat: Chat): User {
        val otherUserId = getOtherUserId(chat)
        val otherUser = userRepository.getUserById(otherUserId)!!
        saveUserWithId(otherUser)
        return otherUser
    }

    private fun saveUserWithId(user: User) {
        _uiState.update {
            it.copy(idToUser = _uiState.value.idToUser + Pair(user.id, user))
        }
    }

    val navigationItemsList = listOf(
        NavigationItem(
            title = "Home", icon = Icons.Filled.Home, route = Route.Chats
        ),
        NavigationItem(
            title = "Profile", icon = Icons.Filled.Person, route = Route.Profile
        ),
        NavigationItem(
            title = "Settings", icon = Icons.Filled.Settings, route = Route.Settings
        ),
        NavigationItem(
            title = "Contact", icon = Icons.Filled.ContactPage, route = Route.Contact
        ),
    )

    fun onCurrentRouteChange(newRoute: Route) {
        _uiState.update { it.copy(currentRoute = newRoute) }
    }


    fun onCurrentAlgChange(alg: Algorithm.Type) {
        _uiState.update { it.copy(currentAlg = alg) }
    }

    fun onCurrentTextChange(newText: String) {
        _uiState.update { it.copy(currentText = newText) }

    }

    fun onUsernameChange(username: String) {
        _uiState.update {
            it.copy(
                newChatUsername = username, newChatError = UiText.DynamicString("")
            )
        }

    }

    fun singOut() {
        authRepository.signOut()
    }

    fun startNewSingleChat(newChatUsername: String): Flow<Boolean> {
        return flow {

            _uiState.value.idToChat.forEach { (_, chat) ->
                if (chat.type == ChatType.SINGLE && newChatUsername in chat.users) {
                    _uiState.update { it.copy(newChatError = UiText.StringResource(R.string.chat_already_exists)) }
                    currentCoroutineContext().cancel()
                }
            }

            if (newChatUsername == _uiState.value.loggedInUser.username) {
                _uiState.update { it.copy(newChatError = UiText.StringResource(R.string.cannot_add_yourself)) }
                currentCoroutineContext().cancel()
            }

            val user = viewModelScope.async {
                userRepository.getUserByUsername(newChatUsername)
            }.await()

            if (user == null) {
                _uiState.update { it.copy(newChatError = UiText.StringResource(R.string.user_not_found)) }
            } else {
                saveUserWithId(user)
                var newChat = Chat(listOf(_uiState.value.loggedInUser.id, user.id))
                val newId = chatRepository.startNewChat(newChat)
                newChat = newChat.copy(id = newId, name = newChatUsername)
                _uiState.update {
                    it.copy(idToChat = _uiState.value.idToChat + Pair(newChat.id, newChat))
                }
                emit(true)
            }
        }
    }

    fun onSendSingleMessage(chatId: String, text: String) {
        if (text.isBlank()) return

        val state = _uiState.value

        // Generate AES Key and encrypt message
        val symmetricKey = AES.generateSymmetricKey()
        val symmetricKeyArray = symmetricKey.toBitArray()
        val encryptedMessage = AES.encrypt(text, symmetricKey)

        val encryptedKeys = mutableMapOf<String, String>()
        val algorithmType = state.currentAlg
        val algorithm = Algorithm.map[algorithmType]!!

        // Get every users public key and respective algorithm
        for (userId in state.idToChat[chatId]!!.users) {
            val user = if (userId != state.loggedInUser.id) state.idToUser[userId]!! else state.loggedInUser
            val publicKeyRaw = user.publicKeys[algorithmType.name]!!
            val publicKey = json.decodeFromString<AsymmetricPublicKey>(publicKeyRaw)

            // Encrypt aes key with asymmetric algorithm
            val encryptedSymmetricKey = algorithm.encrypt(symmetricKeyArray, publicKey).joinToString("")

            encryptedKeys[userId] = encryptedSymmetricKey
        }
        val currentTime = System.currentTimeMillis()
        val message = Message(
            state.loggedInUser.id, encryptedMessage, encryptedKeys, state.currentAlg.name, currentTime
        )
        viewModelScope.launch {
            chatRepository.sendMessage(chatId, message)
        }
        _uiState.update { it.copy(currentText = "") }
    }

    fun initializeChat(chatId: String) {
        viewModelScope.launch {
            val messageCollection = chatRepository.getMessagesCollection(chatId)
            val listener = messageCollection.addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    val newMessages = mutableListOf<DecryptedMessage>()
                    for (change in snapshot.documentChanges) {
                        val message = change.document.toObject<Message>()
                        val algType = Algorithm.Type.valueOf(message.algorithm)
                        val secretKey = _uiState.value.loggedInUserSecretKeys[algType]!!
                        val asymmetricAlgorithm = Algorithm.map[algType]!!
                        val symmetricKey = asymmetricAlgorithm.decrypt(
                            message.encryptedKeys[_uiState.value.loggedInUser.id]!!.toBitArray(), secretKey
                        ).toSecretKey()
                        val decodedText = AES.decrypt(message.encryptedText, symmetricKey)
                        newMessages.add(DecryptedMessage(message.fromId, decodedText, message.timestamp))
                    }
                    val allMessages = (_uiState.value.currentChatMessages + newMessages).sortedBy { it.timestamp }
                    _uiState.update { it.copy(currentChatMessages = allMessages) }
                }
            }

            _uiState.update { mainUIState ->
                mainUIState.copy(currentChatListener = listener)
            }
        }
    }

    fun closeChat(chatId: String) {
        _uiState.value.currentChatListener!!.remove()
        _uiState.update {
            it.copy(currentText = "", currentChatListener = null, currentChatMessages = listOf()
            )
        }


    }


}