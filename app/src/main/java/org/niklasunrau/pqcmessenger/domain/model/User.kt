package org.niklasunrau.pqcmessenger.domain.model

import com.google.firebase.firestore.DocumentId
import org.niklasunrau.pqcmessenger.domain.util.Algorithm
import javax.crypto.SecretKey

data class User(
    @DocumentId val id: String,
    val username: String,
    val email: String,
    val image: String = "",
    val encryptedPrivateKeys: Map<Algorithm, SecretKey> = mapOf(),
    val publicKeys: Map<Algorithm, String> = mapOf()
){
    constructor() : this("", "", "")
}
