package org.niklasunrau.pqcmessenger.data.test

import org.niklasunrau.pqcmessenger.domain.model.User
import org.niklasunrau.pqcmessenger.domain.repository.UserRepository

class UserRepositoryTest : UserRepository {
    override suspend fun createUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun getUserById(uid: String): User {
        TODO("Not yet implemented")
    }

    override suspend fun getUserByUsername(username: String): User? {
        TODO("Not yet implemented")
    }

//    override suspend fun updateUser(user: User) {
//        TODO("Not yet implemented")
//    }

    override suspend fun isUsernameInUse(username: String): Boolean {
        TODO("Not yet implemented")
    }
}