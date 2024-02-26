package org.niklasunrau.pqcmessenger.domain.crypto

import kotlinx.serialization.Serializable

@Serializable
abstract class AsymmetricSecretKey
@Serializable
abstract class AsymmetricPublicKey
abstract class AsymmetricAlgorithm <out S : AsymmetricSecretKey, out P : AsymmetricPublicKey>{

    abstract val name: String

    abstract suspend fun generateKeyPair() : Pair<S, P>
    abstract fun encrypt(message: ByteArray, publicKey: AsymmetricPublicKey): ByteArray
    abstract fun decrypt(cipher: ByteArray, secretKey: AsymmetricSecretKey): ByteArray

}