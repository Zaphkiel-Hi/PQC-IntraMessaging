package org.niklasunrau.pqcmessenger.domain.model

import org.niklasunrau.pqcmessenger.domain.util.ChatType

data class Chat(
    val users: List<String>,
    val id: String = "",
    val name: String = "",
    val icon: String = "",
    val type: ChatType = ChatType.SINGLE,
    val lastMessage: String = "",
){
    constructor() : this(listOf())
}