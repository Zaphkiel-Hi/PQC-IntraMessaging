package org.niklasunrau.pqcmessenger.presentation.main.viewmodel

import org.niklasunrau.pqcmessenger.domain.model.Chat
import org.niklasunrau.pqcmessenger.domain.model.User
import org.niklasunrau.pqcmessenger.domain.util.Route
import org.niklasunrau.pqcmessenger.presentation.util.UiText

data class MainUIState(
    val currentRoute: Route = Route.Chats,
    val currentUser: User = User(),

    val chats: List<Chat> = listOf(),

    val newChatUsername: String = "",
    val newChatError: UiText = UiText.DynamicString(""),

)