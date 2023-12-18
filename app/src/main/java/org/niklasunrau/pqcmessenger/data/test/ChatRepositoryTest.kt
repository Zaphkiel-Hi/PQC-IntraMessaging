package org.niklasunrau.pqcmessenger.data.test

import org.niklasunrau.pqcmessenger.domain.model.Chat
import org.niklasunrau.pqcmessenger.domain.repository.ChatRepository

class ChatRepositoryTest : ChatRepository {
    override suspend fun getUserChats(uid: String): List<Chat> {

        TODO("Not yet implemented")
    }

    override suspend fun startNewChat(chat: Chat): String {
        TODO("Not yet implemented")
    }
}