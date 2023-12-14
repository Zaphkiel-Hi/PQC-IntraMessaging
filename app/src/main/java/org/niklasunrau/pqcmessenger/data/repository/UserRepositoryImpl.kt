package org.niklasunrau.pqcmessenger.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import org.niklasunrau.pqcmessenger.domain.model.User
import org.niklasunrau.pqcmessenger.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun getUser(uid: String): User? =
        firestore.collection(USER_COLLECTION).document(uid).get().await().toObject<User>()


    override suspend fun createUser(user: User) {
        firestore.collection(USER_COLLECTION).add(user).await()
    }

    override suspend fun updateUser(user: User) {
        firestore.collection(USER_COLLECTION).document(user.id).set(user).await()
    }

    companion object {
        private const val USER_COLLECTION = "users"
    }
}