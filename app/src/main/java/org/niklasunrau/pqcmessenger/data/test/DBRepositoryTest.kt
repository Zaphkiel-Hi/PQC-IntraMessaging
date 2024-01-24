package org.niklasunrau.pqcmessenger.data.test

import org.niklasunrau.pqcmessenger.domain.model.local.LocalMessage
import org.niklasunrau.pqcmessenger.domain.repository.DBRepository

class DBRepositoryTest: DBRepository {

    override suspend fun saveMessage(message: LocalMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun loadMessages(chatId: String): List<LocalMessage> {
        TODO("Not yet implemented")
    }
}