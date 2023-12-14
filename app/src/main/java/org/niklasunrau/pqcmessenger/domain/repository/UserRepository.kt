package org.niklasunrau.pqcmessenger.domain.repository

import org.niklasunrau.pqcmessenger.domain.model.User

interface UserRepository {
    suspend fun getUser(uid: String): User?
    suspend fun createUser(user: User)
    suspend fun updateUser(user: User)
}