package org.niklasunrau.pqcmessenger.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import org.niklasunrau.pqcmessenger.domain.model.local.LocalChat
import org.niklasunrau.pqcmessenger.domain.model.local.LocalMessage

@Database(entities = [LocalChat::class, LocalMessage::class], version = 1)
abstract class ChatDatabase : RoomDatabase() {
    abstract val dao: ChatDao
}