package org.niklasunrau.pqcmessenger.domain.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_table")
data class LocalMessage(
    @PrimaryKey val messageId: String,
    val chatId: String,
    val fromId: String,
    val text: String,
    val timestamp: Long

)
