package org.niklasunrau.pqcmessenger.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import org.niklasunrau.pqcmessenger.domain.model.User
import org.niklasunrau.pqcmessenger.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun getUserById(uid: String): User? {
        Log.d("API", "getUserById Call")
        val result = firestore.collection(USER_COLLECTION).document(uid).get().await()
        return if(result.exists()) result.toObject<User>() else null

    }
    override suspend fun getUserByUsername(username: String): User? {
        Log.d("API", "getUserByUsername Call")
        val result = firestore.collection(USER_COLLECTION).whereEqualTo(USERNAME_FIELD, username).get().await()
        return if(result.isEmpty) null else result.toObjects(User::class.java)[0]
    }


    override suspend fun createUser(user: User) {
        Log.d("API", "createUser Call")
        firestore.collection(USER_COLLECTION).add(user)
    }

    override suspend fun isUsernameInUse(username: String): Boolean {
        Log.d("API", "isUsernameInUse Call")
        return !firestore.collection(USER_COLLECTION).whereEqualTo(USERNAME_FIELD, username).get().await().isEmpty
    }

    companion object {
        private const val USERNAME_FIELD = "username"
        private const val CHATS_FIELD = "chats"
        private const val USER_COLLECTION = "users"
    }
}