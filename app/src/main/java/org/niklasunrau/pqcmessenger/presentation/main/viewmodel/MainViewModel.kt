package org.niklasunrau.pqcmessenger.presentation.main.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.niklasunrau.pqcmessenger.domain.repository.AuthRepository
import org.niklasunrau.pqcmessenger.domain.repository.UserRepository
import org.niklasunrau.pqcmessenger.domain.util.Route
import org.niklasunrau.pqcmessenger.presentation.util.NavigationItem
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository

) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUIState())
    val uiState = _uiState.asStateFlow()

    val navigationItemsList = listOf(
        NavigationItem(
            title = "Home",
            icon = Icons.Filled.Home,
            route = Route.Chats
        ),
        NavigationItem(
            title = "Profile",
            icon = Icons.Filled.Person,
            route = Route.Profile
        ),
        NavigationItem(
            title = "Settings",
            icon = Icons.Filled.Settings,
            route = Route.Settings
        ),
        NavigationItem(
            title = "Contact",
            icon = Icons.Filled.ContactPage,
            route = Route.Contact
        ),
    )

    fun updateCurrentRoute(newRoute: Route){
        _uiState.update { it.copy(currentRoute = newRoute) }
    }


    fun singOut(
        onNavigateToStart: () -> Unit
    ) {
        viewModelScope.launch {
            authRepository.signOut()
        }
        onNavigateToStart()
    }
}