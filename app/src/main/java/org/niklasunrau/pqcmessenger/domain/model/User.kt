package org.niklasunrau.pqcmessenger.domain.model

import com.google.firebase.firestore.DocumentId
import org.niklasunrau.pqcmessenger.domain.crypto.mceliece.McEliecePublicKey
import org.niklasunrau.pqcmessenger.domain.crypto.mceliece.McElieceSecretKey

data class User(
    @DocumentId val id: String,
    val username: String,
    val email: String,
    val image: String = "",
    val mcEliecePublicKey: McEliecePublicKey = McEliecePublicKey(),
    val mcElieceSecretKey: McElieceSecretKey = McElieceSecretKey(),
){
    constructor() : this("", "", "")
}
