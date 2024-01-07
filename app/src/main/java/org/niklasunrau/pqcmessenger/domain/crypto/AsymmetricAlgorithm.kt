package org.niklasunrau.pqcmessenger.domain.crypto

import kotlinx.serialization.Serializable

@Serializable
abstract class AsymmetricSecretKey
@Serializable
abstract class AsymmetricPublicKey
abstract class AsymmetricAlgorithm <out S : AsymmetricSecretKey, out P : AsymmetricPublicKey>{


    abstract suspend fun generateKeyPair() : Pair<S, P>
    abstract fun encrypt(message: LongArray, publicKey: AsymmetricPublicKey): LongArray
    abstract fun decrypt(cipher: LongArray, secretKey: AsymmetricSecretKey): LongArray

}