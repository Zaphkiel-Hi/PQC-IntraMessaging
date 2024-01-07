package org.niklasunrau.pqcmessenger.domain.util

import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricAlgorithm
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricPublicKey
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricSecretKey
import org.niklasunrau.pqcmessenger.domain.crypto.mceliece.McEliece
import org.niklasunrau.pqcmessenger.domain.crypto.tmp.TestAlg

object Algorithm {
    enum class Type{
        MCELIECE,
        TEST
    }
    val map: Map<Type, AsymmetricAlgorithm<AsymmetricSecretKey, AsymmetricPublicKey>> = mapOf(
        Type.MCELIECE to McEliece,
        Type.TEST to TestAlg
    )

    val name: Map<Type, String> = mapOf(
        Type.MCELIECE to "McEliece",
        Type.TEST to "Test Algo"
    )
}
