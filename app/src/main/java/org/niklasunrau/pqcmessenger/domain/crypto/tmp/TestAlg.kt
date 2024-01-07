package org.niklasunrau.pqcmessenger.domain.crypto.tmp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricAlgorithm
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricPublicKey
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricSecretKey

@Serializable
@SerialName("TestAlg")
data class SK(val a: Int) : AsymmetricSecretKey()

@Serializable
@SerialName("TestAlg")
data class PK(val a: Int) : AsymmetricPublicKey()
object TestAlg : AsymmetricAlgorithm<SK, PK>() {
    override suspend fun generateKeyPair(): Pair<SK, PK> {
        return SK(1) to PK(2)
    }

    override fun decrypt(cipher: LongArray, secretKey: AsymmetricSecretKey): LongArray {
        return cipher
    }

    override fun encrypt(message: LongArray, publicKey: AsymmetricPublicKey): LongArray {
        return message
    }
}