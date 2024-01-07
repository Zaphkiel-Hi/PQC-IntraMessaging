package org.niklasunrau.pqcmessenger.domain.crypto

import kotlinx.serialization.Serializable

@Serializable
abstract class AsymmetricSecretKey
@Serializable
abstract class AsymmetricPublicKey
abstract class AsymmetricAlgorithm <S : AsymmetricSecretKey, P : AsymmetricPublicKey>{


    abstract suspend fun generateKeyPair() : Pair<S, P>
    abstract fun encrypt(message: String, publicKey: P): String
    abstract fun decrypt(cipher: String, secretKey: S): String

}