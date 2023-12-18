package org.niklasunrau.pqcmessenger.domain.model

import org.niklasunrau.pqcmessenger.domain.util.ChatType

data class Chat(
    val users: List<String>,
    val name: String? = null,
    val icon: String? = null,
    val type: ChatType = ChatType.SINGLE,
    val recentMessage: String = "",
)