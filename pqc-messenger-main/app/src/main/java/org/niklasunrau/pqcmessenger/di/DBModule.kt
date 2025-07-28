package org.niklasunrau.pqcmessenger.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.niklasunrau.pqcmessenger.data.local.ChatDao
import org.niklasunrau.pqcmessenger.data.local.ChatDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {

    @Singleton
    @Provides
    fun provideChatDatabase(@ApplicationContext context: Context): ChatDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ChatDatabase::class.java,
            "chat_database"
        ).build()
    }

    @Provides
    fun provideDao(habitDatabase: ChatDatabase): ChatDao {
        return habitDatabase.dao
    }

}