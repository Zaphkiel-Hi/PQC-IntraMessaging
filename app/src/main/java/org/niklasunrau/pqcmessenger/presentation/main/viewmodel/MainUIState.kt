package org.niklasunrau.pqcmessenger.presentation.main.viewmodel

import com.google.firebase.firestore.ListenerRegistration
import org.niklasunrau.pqcmessenger.domain.crypto.Algorithms
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricSecretKey
import org.niklasunrau.pqcmessenger.domain.model.Chat
import org.niklasunrau.pqcmessenger.domain.model.User
import org.niklasunrau.pqcmessenger.domain.model.local.LocalMessage
import org.niklasunrau.pqcmessenger.domain.util.Route
import org.niklasunrau.pqcmessenger.presentation.util.UiText

data class MainUIState(
    val isLoading: Boolean = false,

    val currentRoute: Route = Route.Chats,
    val loggedInUser: User = User(),
    val loggedInUserSecretKeys: Map<Algorithms.Type, AsymmetricSecretKey> = mapOf(),

    val idToChat: Map<String, Chat> = mapOf(),
    val idToUser: Map<String, User> = mapOf(),

    val newChatUsername: String = "",
    val newChatError: UiText = UiText.DynamicString(""),

    val currentText: String = "",
    val currentAlg: Algorithms.Type = Algorithms.Type.MCELIECE,
    val currentChatMessages: List<LocalMessage> = listOf(),
    val currentChatListener: ListenerRegistration? = null


)