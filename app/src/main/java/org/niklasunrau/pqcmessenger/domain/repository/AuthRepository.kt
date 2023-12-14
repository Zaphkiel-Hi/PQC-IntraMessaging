package org.niklasunrau.pqcmessenger.domain.repository

import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import org.niklasunrau.pqcmessenger.domain.util.Status

interface AuthRepository {
    val currentUserId: String
    suspend fun login(email: String, password: String): Flow<Status<AuthResult>>
    suspend fun signup(email: String, password: String): Flow<Status<AuthResult>>

    suspend fun signOut()
    suspend fun sendRecoveryEmail(email: String)
}