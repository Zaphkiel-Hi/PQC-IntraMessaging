package org.niklasunrau.pqcmessenger.domain.repository

import org.niklasunrau.pqcmessenger.domain.model.local.LocalMessage

interface DBRepository {
    suspend fun saveMessage(message: LocalMessage)
    suspend fun loadMessages(chatId: String): List<LocalMessage>
}