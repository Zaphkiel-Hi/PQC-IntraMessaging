package org.niklasunrau.pqcmessenger.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.niklasunrau.pqcmessenger.domain.model.local.LocalMessage

@Dao
interface ChatDao {

    @Query("SELECT * FROM message_table WHERE chatId = :chatId")
    suspend fun getMessages(chatId: String) : List<LocalMessage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: LocalMessage)


}