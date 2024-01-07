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
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricSecretKey
import org.niklasunrau.pqcmessenger.domain.crypto.aes.AES
import org.niklasunrau.pqcmessenger.domain.crypto.mceliece.McElieceSecretKey
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
            _uiState.update { it.copy(currentUser = currentUser) }


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


            initKeys(savedStateHandle["password"])

        }
    }

    private suspend fun initKeys(password: String?) {
        withContext(Dispatchers.Default) {
            if (password.isNullOrEmpty()) return@withContext
            if (_uiState.value.currentUserSecretKeys.isNotEmpty()) return@withContext

            val encryptedMap = _uiState.value.currentUser.encryptedSecretKeys
            val secretKeys = mutableMapOf<Algorithm.Type, AsymmetricSecretKey>()
            for ((name, cipher) in encryptedMap) {
                val type = Algorithm.Type.valueOf(name)
                val decrypted = AES.decrypt(cipher, password)
                val secretKey = json.decodeFromString<AsymmetricSecretKey>(decrypted) as McElieceSecretKey
                secretKeys[type] = secretKey
            }
            _uiState.update { it.copy(currentUserSecretKeys = secretKeys) }
        }
    }

    fun getOtherUserId(chat: Chat) =
        if (chat.users[0] != _uiState.value.currentUser.id) chat.users[0] else chat.users[1]

    private suspend fun saveOtherUser(chat: Chat): User {
        val otherUserId = getOtherUserId(chat)
        val otherUser = userRepository.getUserById(otherUserId)!!
        saveUserWithId(otherUser)
        return otherUser
    }

    private fun saveUserWithId(user: User) {
        _uiState.value.idToUser[user.id] = user
        _uiState.update {
            it.copy(idToUser = _uiState.value.idToUser)
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

            _uiState.value.idToChat.forEach { chat ->
                if (newChatUsername in chat.value.users) {
                    _uiState.update { it.copy(newChatError = UiText.StringResource(R.string.chat_already_exists)) }
                    currentCoroutineContext().cancel()
                }
            }

            if (newChatUsername == _uiState.value.currentUser.username) {
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
                var newChat = Chat(listOf(_uiState.value.currentUser.id, user.id))
                val newId = chatRepository.startNewChat(newChat)
                newChat = newChat.copy(id = newId)

                _uiState.value.idToChat[newChat.id] = newChat
                _uiState.update {
                    it.copy(idToChat = _uiState.value.idToChat)
                }
                emit(true)
            }
        }
    }

    fun onSendMessage(chatId: String, text: String) {
        if (text.isBlank()) return
        val currentTime = System.currentTimeMillis()
        val message = Message(_uiState.value.currentUser.id, text, currentTime)
        viewModelScope.launch {
            chatRepository.sendMessage(chatId, message)
        }
        _uiState.update { it.copy(currentText = "") }
    }

    fun initializeChat(chatId: String) {
        viewModelScope.launch {
            _uiState.update { mainUIState ->
                mainUIState.copy(currentChatListener = chatRepository.getMessagesCollection(chatId)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) return@addSnapshotListener
                        if (snapshot != null) {
                            _uiState.update { mainUIState ->
                                mainUIState.copy(currentChatMessages = snapshot.documents.mapNotNull {
                                    it.toObject<Message>()
                                }.sortedBy { it.timestamp })
                            }
                        }
                    })
            }
        }
    }

    fun closeChat(chatId: String) {
        val currentMessages = _uiState.value.currentChatMessages
        val lastMessage = if (currentMessages.isNotEmpty()) currentMessages.last().text else ""
        viewModelScope.launch {
            chatRepository.updateLastMessage(
                chatId, lastMessage
            )
        }
        val chats = _uiState.value.idToChat
        val newChat = chats[chatId]!!.copy(lastMessage = lastMessage)
        chats.replace(chatId, newChat)
        _uiState.update {
            it.copy(
                idToChat = chats, currentText = "", currentChatListener = null, currentChatMessages = listOf()
            )
        }


    }


}