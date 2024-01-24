package org.niklasunrau.pqcmessenger.domain.repository

import com.google.firebase.firestore.CollectionReference
import org.niklasunrau.pqcmessenger.domain.model.Chat
import org.niklasunrau.pqcmessenger.domain.model.Message

interface ChatRepository {
    suspend fun getUserChats(uid: String): List<Chat>
    suspend fun startNewChat(chat: Chat): String

    suspend fun getMessagesCollection(chatId: String): CollectionReference
    suspend fun sendMessage(chatId: String, message: Message): String
}