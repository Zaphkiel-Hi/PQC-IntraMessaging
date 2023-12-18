package org.niklasunrau.pqcmessenger.domain.repository

import org.niklasunrau.pqcmessenger.domain.model.Chat

interface ChatRepository {
    suspend fun getUserChats(uid: String): List<Chat>
    suspend fun startNewChat(chat: Chat): String
}