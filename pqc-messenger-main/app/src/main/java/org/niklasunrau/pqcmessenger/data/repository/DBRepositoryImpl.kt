package org.niklasunrau.pqcmessenger.data.repository

import org.niklasunrau.pqcmessenger.data.local.ChatDatabase
import org.niklasunrau.pqcmessenger.domain.model.local.LocalMessage
import org.niklasunrau.pqcmessenger.domain.repository.DBRepository
import javax.inject.Inject

class DBRepositoryImpl @Inject constructor(
    private val db: ChatDatabase
) : DBRepository {

    override suspend fun loadMessages(chatId: String): List<LocalMessage> {
        return db.dao.getMessages(chatId)
    }
    override suspend fun saveMessage(message: LocalMessage) {
        db.dao.insertMessage(message)
    }
}