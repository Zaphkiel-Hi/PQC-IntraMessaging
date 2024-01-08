package org.niklasunrau.pqcmessenger.data.test

import com.google.firebase.firestore.CollectionReference
import org.niklasunrau.pqcmessenger.domain.model.Chat
import org.niklasunrau.pqcmessenger.domain.model.Message
import org.niklasunrau.pqcmessenger.domain.repository.ChatRepository

class ChatRepositoryTest : ChatRepository {
    override suspend fun getUserChats(uid: String): List<Chat> {

        TODO("Not yet implemented")
    }

    override suspend fun startNewChat(chat: Chat): String {
        TODO("Not yet implemented")
    }

    override suspend fun getMessagesCollection(chatId: String): CollectionReference {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(chatId: String, message: Message) {
        TODO("Not yet implemented")
    }
}