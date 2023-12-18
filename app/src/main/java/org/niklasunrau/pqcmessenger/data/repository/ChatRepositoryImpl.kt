package org.niklasunrau.pqcmessenger.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.niklasunrau.pqcmessenger.domain.model.Chat
import org.niklasunrau.pqcmessenger.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatRepository {

    override suspend fun getUserChats(uid: String): List<Chat> {
        return firestore.collection(CHAT_COLLECTION).whereArrayContains(USERS_FIELD, uid).get().await().toObjects(Chat::class.java)
    }

    override suspend fun startNewChat(chat: Chat) {
        firestore.collection(CHAT_COLLECTION).document().set(chat)
    }

    companion object {
        private const val USERS_FIELD = "users"
        private const val CHAT_COLLECTION = "chats"
    }
}