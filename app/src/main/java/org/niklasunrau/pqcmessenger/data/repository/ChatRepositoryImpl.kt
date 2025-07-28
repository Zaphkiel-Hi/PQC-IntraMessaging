package org.niklasunrau.pqcmessenger.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.niklasunrau.pqcmessenger.domain.model.Chat
import org.niklasunrau.pqcmessenger.domain.model.Message
import org.niklasunrau.pqcmessenger.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatRepository {

    override suspend fun getUserChats(uid: String): List<Chat> {
        //Log.d("API", "getUserChats Call")
        return firestore.collection(CHAT_COLLECTION).whereArrayContains(USERS_FIELD, uid).get()
            .await().toObjects(Chat::class.java)
    }

    override suspend fun startNewChat(chat: Chat): String {
        //Log.d("API", "startNewChat Call")
        val newDoc = firestore.collection(CHAT_COLLECTION).document()
        newDoc.set(chat.copy(id = newDoc.id))
        return newDoc.id
    }

    override suspend fun getMessagesCollection(chatId: String): CollectionReference {
        //Log.d("API", "getMessagesCollection Call")
        return firestore.collection(CHAT_COLLECTION).document(chatId).collection(MESSAGES_COLLECTION)
    }

    override suspend fun sendMessage(chatId: String, message: Message): String {
        //Log.d("API", "sendMessage Call")
        val newDoc = firestore.collection(CHAT_COLLECTION).document(chatId).collection(MESSAGES_COLLECTION).document()
        newDoc.set(message.copy(id = newDoc.id))
        return newDoc.id
    }

    companion object {
        private const val USERS_FIELD = "users"
        private const val CHAT_COLLECTION = "chats"
        private const val MESSAGES_COLLECTION = "messages"
    }
}