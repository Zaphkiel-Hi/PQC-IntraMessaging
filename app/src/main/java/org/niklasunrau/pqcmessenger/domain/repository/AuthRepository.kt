package org.niklasunrau.pqcmessenger.domain.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import org.niklasunrau.pqcmessenger.domain.util.Status

interface AuthRepository {
    val currentUser: FirebaseUser?
    val currentUserId: String
    val isUserSignedIn: Boolean
    suspend fun login(email: String, password: String): Flow<Status<AuthResult>>
    suspend fun signup(email: String, password: String): Flow<Status<AuthResult>>


    fun signOut()
    fun sendRecoveryEmail(email: String)
}