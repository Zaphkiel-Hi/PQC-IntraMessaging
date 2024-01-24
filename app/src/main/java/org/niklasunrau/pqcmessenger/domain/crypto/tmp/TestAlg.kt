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

    override fun decrypt(cipher: ByteArray, secretKey: AsymmetricSecretKey): ByteArray {
        return cipher
    }

    override fun encrypt(message: ByteArray, publicKey: AsymmetricPublicKey): ByteArray {
        return message
    }
}