package org.niklasunrau.pqcmessenger.presentation.main.viewmodel

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
import org.niklasunrau.pqcmessenger.domain.crypto.Algorithms
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricPublicKey
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricSecretKey
import org.niklasunrau.pqcmessenger.domain.crypto.aes.AES
import org.niklasunrau.pqcmessenger.domain.crypto.toBitArray
import org.niklasunrau.pqcmessenger.domain.crypto.toSecretKey
import org.niklasunrau.pqcmessenger.domain.model.Chat
import org.niklasunrau.pqcmessenger.domain.model.Message
import org.niklasunrau.pqcmessenger.domain.model.User
import org.niklasunrau.pqcmessenger.domain.model.local.LocalMessage
import org.niklasunrau.pqcmessenger.domain.repository.AuthRepository
import org.niklasunrau.pqcmessenger.domain.repository.ChatRepository
import org.niklasunrau.pqcmessenger.domain.repository.DBRepository
import org.niklasunrau.pqcmessenger.domain.repository.UserRepository
import org.niklasunrau.pqcmessenger.domain.util.ChatType
import org.niklasunrau.pqcmessenger.domain.util.Json.json
import org.niklasunrau.pqcmessenger.presentation.util.UiText
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val dbRepository: DBRepository,
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

            // Get encrypted secret keys
            val encryptedMap = _uiState.value.loggedInUser.encryptedSecretKeys
            val secretKeys = mutableMapOf<Algorithms.Type, AsymmetricSecretKey>()

            // Decrypt each secret key
            for ((name, cipher) in encryptedMap) {

                // Decrypt with AES by streching password (PBKDF2)
                val decrypted = AES.decrypt(cipher, password)

                // Undo JSON encoding
                val secretKey = json.decodeFromString<AsymmetricSecretKey>(decrypted)

                // Get enum value by name and store decrypted secret key
                val type = Algorithms.Type.valueOf(name)
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

    fun signOut(){
        authRepository.signOut()
    }


    fun onCurrentAlgChange(alg: Algorithms.Type) {
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

        // Get selected algorithm
        val algorithmType = state.currentAlg
        val algorithm = Algorithms.map[algorithmType]!!

        // Get every users public key for the selected alg.
        for (userId in state.idToChat[chatId]!!.users) {
            val user =
                if (userId != state.loggedInUser.id) state.idToUser[userId]!! else state.loggedInUser
            val publicKeyRaw = user.publicKeys[algorithmType.name]!!
            val publicKey = json.decodeFromString<AsymmetricPublicKey>(publicKeyRaw)

            // Encrypt aes key with asymmetric algorithm
            val encryptedSymmetricKey =
                algorithm.encrypt(symmetricKeyArray, publicKey).joinToString("")
            encryptedKeys[userId] = encryptedSymmetricKey
        }
        val currentTime = System.currentTimeMillis()
        val message = Message(
            fromId = state.loggedInUser.id,
            encryptedText = encryptedMessage,
            encryptedKeys = encryptedKeys,
            algorithm = state.currentAlg.name,
            timestamp = currentTime
        )
        viewModelScope.launch {
            val id = chatRepository.sendMessage(chatId, message)
            dbRepository.saveMessage(
                LocalMessage(
                    id, chatId, state.loggedInUser.id, text, currentTime
                )
            )
        }
        _uiState.update { it.copy(currentText = "") }
    }

    fun initializeChat(chatId: String) {
        viewModelScope.launch {
            val messageCollection = chatRepository.getMessagesCollection(chatId)
            val localMessages = viewModelScope.async { dbRepository.loadMessages(chatId) }.await()
            _uiState.update { it.copy(currentChatMessages = localMessages) }

            val listener = messageCollection.addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    val newMessages = mutableListOf<LocalMessage>()
                    for (change in snapshot.documentChanges) {
                        val message = change.document.toObject<Message>()
                        if (!_uiState.value.currentChatMessages.any { it.messageId == message.id }) {
                            val decodedMessage = message.decodeToLocalMessage(chatId)
                            newMessages.add(decodedMessage)
                            viewModelScope.launch {
                                dbRepository.saveMessage(decodedMessage)
                            }
                        }
                    }
                    val allMessages =
                        (_uiState.value.currentChatMessages + newMessages).sortedBy { it.timestamp }
                    _uiState.update { it.copy(currentChatMessages = allMessages) }

                }
            }
            _uiState.update { mainUIState ->
                mainUIState.copy(currentChatListener = listener)
            }
        }
    }

    private fun Message.decodeToLocalMessage(chatId: String): LocalMessage {
        val algType = Algorithms.Type.valueOf(algorithm)
        val asymmetricAlgorithm = Algorithms.map[algType]!!

        // Get secret key and decrypt AES key
        val secretKey = _uiState.value.loggedInUserSecretKeys[algType]!!
        val symmetricKey = asymmetricAlgorithm.decrypt(
            encryptedKeys[_uiState.value.loggedInUser.id]!!.toBitArray(), secretKey
        ).toSecretKey()

        // Use AES key to decrypt message
        val decodedText = AES.decrypt(encryptedText, symmetricKey)

        return LocalMessage(
            id, chatId, fromId, decodedText, timestamp
        )
    }

    fun closeChat() {
        _uiState.value.currentChatListener!!.remove()
        _uiState.update {
            it.copy(
                currentText = "", currentChatListener = null, currentChatMessages = listOf()
            )
        }
    }

    fun selectImage() {

    }


}