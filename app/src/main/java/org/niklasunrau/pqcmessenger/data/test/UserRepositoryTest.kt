package org.niklasunrau.pqcmessenger.data.test

import org.niklasunrau.pqcmessenger.domain.model.User
import org.niklasunrau.pqcmessenger.domain.repository.UserRepository

class UserRepositoryTest : UserRepository {
    override suspend fun createUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(uid: String): User? {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(user: User) {
        TODO("Not yet implemented")
    }
}