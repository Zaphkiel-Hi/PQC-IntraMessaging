package org.niklasunrau.pqcmessenger.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import org.niklasunrau.pqcmessenger.domain.repository.AuthRepository
import org.niklasunrau.pqcmessenger.domain.util.Status
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {
    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val isUserSignedIn: Boolean
        get() = auth.currentUser != null

    override suspend fun login(email: String, password: String): Flow<Status<AuthResult>> {
        return flow {
            emit(Status.Loading())
            val result = auth.signInWithEmailAndPassword(email, password).await()
            emit(Status.Success(result))
        }.catch {
            emit(Status.Error(it))
        }
    }

    override suspend fun signup(email: String, password: String): Flow<Status<AuthResult>> {
        return flow {
            emit(Status.Loading())
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            emit(Status.Success(result))
        }.catch {
            emit(Status.Error(it))
        }
    }


    override fun signOut() {
         auth.signOut()
    }

    override fun sendRecoveryEmail(email: String) {
        auth.sendPasswordResetEmail(email)
    }

}