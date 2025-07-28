package org.niklasunrau.pqcmessenger.domain.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_table")
data class LocalChat(
    @PrimaryKey val chatId: String,
    val name: String,
    val icon: String
)