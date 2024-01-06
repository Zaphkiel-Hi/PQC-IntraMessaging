package org.niklasunrau.pqcmessenger.domain.crypto.tmp

import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricAlgorithm
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricPublicKey
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricSecretKey

data class SK(val a: Int) : AsymmetricSecretKey()
data class PK(val a: Int) : AsymmetricPublicKey()
object TestAlg : AsymmetricAlgorithm<SK, PK>() {
    override suspend fun generateKeyPair(): Pair<SK, PK> {
        TODO("Not yet implemented")
    }

    override fun decrypt(cipher: String, secretKey: SK): String {
        TODO("Not yet implemented")
    }

    override fun encrypt(message: String, publicKey: PK): String {
        TODO("Not yet implemented")
    }
}