package org.niklasunrau.pqcmessenger.presentation.main.viewmodel

import org.niklasunrau.pqcmessenger.domain.util.Route

data class MainUIState(
    val currentRoute: Route = Route.Chats
)