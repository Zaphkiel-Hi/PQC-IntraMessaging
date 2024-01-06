package org.niklasunrau.pqcmessenger.domain.util

import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricAlgorithm
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricPublicKey
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricSecretKey
import org.niklasunrau.pqcmessenger.domain.crypto.mceliece.McEliece

object Algorithm {
    enum class Type{
        MCELIECE
    }
    val map: Map<Type, AsymmetricAlgorithm<out AsymmetricSecretKey, out AsymmetricPublicKey>> = mapOf(
        Type.MCELIECE to McEliece
    )
}
