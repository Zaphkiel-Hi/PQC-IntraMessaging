package org.niklasunrau.pqcmessenger.presentation.main.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.niklasunrau.pqcmessenger.R
import org.niklasunrau.pqcmessenger.domain.model.Chat
import org.niklasunrau.pqcmessenger.domain.model.User
import org.niklasunrau.pqcmessenger.domain.repository.AuthRepository
import org.niklasunrau.pqcmessenger.domain.repository.ChatRepository
import org.niklasunrau.pqcmessenger.domain.repository.UserRepository
import org.niklasunrau.pqcmessenger.domain.util.Route
import org.niklasunrau.pqcmessenger.presentation.util.NavigationItem
import org.niklasunrau.pqcmessenger.presentation.util.UiText
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository

) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUIState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val chats = chatRepository.getUserChats(authRepository.currentUserId)
            val user = userRepository.getUserById(authRepository.currentUserId)
            _uiState.update { it.copy(chats = chats, currentUser = user!!) }
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

    fun updateCurrentRoute(newRoute: Route) {
        _uiState.update { it.copy(currentRoute = newRoute) }
    }

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(newChatUsername = username, newChatError = UiText.DynamicString("")) }

    }

    fun getUser(chat: Chat): Flow<User> {
        val users = chat.users
        val otherUserId = if (users[0] != authRepository.currentUserId) users[0] else users[1]
        return flow {
            val user = userRepository.getUserById(otherUserId)!!
            emit(user)
        }
    }


    fun singOut(
        onNavigateToStart: () -> Unit
    ) {
        viewModelScope.launch {
            authRepository.signOut()
        }
        onNavigateToStart()
    }

    fun startNewSingleChat(newChatUsername: String): Flow<Boolean> {
        return flow {

            _uiState.value.chats.forEach { chat ->
                if (newChatUsername in chat.users) {
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
                val newChat = Chat(listOf(authRepository.currentUserId, user.id))
                _uiState.update { it.copy(chats = _uiState.value.chats + newChat) }
                chatRepository.startNewChat(newChat)
                emit(true)
            }
        }
    }
}