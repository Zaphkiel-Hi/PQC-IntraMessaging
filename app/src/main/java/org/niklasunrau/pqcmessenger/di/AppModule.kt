@file:Suppress("unused")

package org.niklasunrau.pqcmessenger.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.niklasunrau.pqcmessenger.data.repository.AuthRepositoryImpl
import org.niklasunrau.pqcmessenger.data.repository.ChatRepositoryImpl
import org.niklasunrau.pqcmessenger.data.repository.DBRepositoryImpl
import org.niklasunrau.pqcmessenger.data.repository.UserRepositoryImpl
import org.niklasunrau.pqcmessenger.domain.repository.AuthRepository
import org.niklasunrau.pqcmessenger.domain.repository.ChatRepository
import org.niklasunrau.pqcmessenger.domain.repository.DBRepository
import org.niklasunrau.pqcmessenger.domain.repository.UserRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule{

    @Binds
    abstract fun providesAuthRepositoryImpl(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun providesUserRepositoryImpl(impl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun providesChatRepositoryImpl(impl: ChatRepositoryImpl): ChatRepository

    @Binds
    abstract fun providesDBRepositoryImpl(impl: DBRepositoryImpl): DBRepository


}