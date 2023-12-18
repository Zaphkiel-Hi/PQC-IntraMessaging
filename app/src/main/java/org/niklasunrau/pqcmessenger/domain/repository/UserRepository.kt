package org.niklasunrau.pqcmessenger.domain.repository

import org.niklasunrau.pqcmessenger.domain.model.User

interface UserRepository {
    suspend fun getUserById(uid: String): User?
    suspend fun getUserByUsername(username: String): User?
    suspend fun createUser(user: User)
//    suspend fun updateUser(user: User)
    suspend fun isUsernameInUse(username: String): Boolean
}