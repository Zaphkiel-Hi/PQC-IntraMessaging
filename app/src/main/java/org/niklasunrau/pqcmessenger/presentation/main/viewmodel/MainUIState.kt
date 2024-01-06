package org.niklasunrau.pqcmessenger.presentation.main.viewmodel

import com.google.firebase.firestore.ListenerRegistration
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricSecretKey
import org.niklasunrau.pqcmessenger.domain.model.Chat
import org.niklasunrau.pqcmessenger.domain.model.Message
import org.niklasunrau.pqcmessenger.domain.model.User
import org.niklasunrau.pqcmessenger.domain.util.Algorithm
import org.niklasunrau.pqcmessenger.domain.util.Route
import org.niklasunrau.pqcmessenger.presentation.util.UiText

data class MainUIState(
    val currentRoute: Route = Route.Chats,
    val currentUser: User = User(),
    val currentUserSecretKeys: Map<Algorithm.Type, AsymmetricSecretKey> = mapOf(),

    val idToChat: MutableMap<String, Chat> = mutableMapOf(),
    val idToUser: MutableMap<String, User> = mutableMapOf(),

    val newChatUsername: String = "",
    val newChatError: UiText = UiText.DynamicString(""),

    val currentText: String = "",
    val currentChatMessages: List<Message> = listOf(),
    val currentChatListener: ListenerRegistration? = null


)