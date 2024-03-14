package org.niklasunrau.pqcmessenger

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.niklasunrau.pqcmessenger.domain.repository.AuthRepository
import org.niklasunrau.pqcmessenger.presentation.util.NavigationItem
import org.niklasunrau.pqcmessenger.presentation.util.Screen
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun logoutPotentialUser() {
        if (authRepository.isUserSignedIn) authRepository.signOut()
    }

    val navigationItemsList = listOf(
        NavigationItem(
            titleId = R.string.home, icon = Icons.Filled.Home, screen = Screen.Main.Chats
        ),
        NavigationItem(
            titleId = R.string.profile, icon = Icons.Filled.Person, screen = Screen.Main.Profile
        ),
        NavigationItem(
            titleId = R.string.settings, icon = Icons.Filled.Settings, screen = Screen.Main.Settings
        ),
        NavigationItem(
            titleId = R.string.contact, icon = Icons.Filled.ContactPage, screen = Screen.Main.Contact
        ),
    )

}