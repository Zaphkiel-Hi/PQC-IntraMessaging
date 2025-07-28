package org.niklasunrau.pqcmessenger.data.test

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import org.niklasunrau.pqcmessenger.domain.repository.AuthRepository
import org.niklasunrau.pqcmessenger.domain.util.Status

class AuthRepositoryTest() : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = null
    override val currentUserId: String
        get() = ""

    override val isUserSignedIn: Boolean
        get() = false

    override suspend fun login(email: String, password: String): Flow<Status<AuthResult>> {
        TODO("Not yet implemented")
    }

    override suspend fun signup(email: String, password: String): Flow<Status<AuthResult>> {
        TODO("Not yet implemented")
    }


    override fun signOut() {
        TODO("Not yet implemented")
    }

    override fun sendRecoveryEmail(email: String) {
        TODO("Not yet implemented")
    }

}