package org.niklasunrau.pqcmessenger.domain.crypto

import org.niklasunrau.pqcmessenger.domain.crypto.mceliece.McEliece
import org.niklasunrau.pqcmessenger.domain.crypto.tmp.TestAlg

object Algorithms {
    enum class Type{
        MCELIECE,
        TEST
    }
    val map: Map<Type, AsymmetricAlgorithm<AsymmetricSecretKey, AsymmetricPublicKey>> = mapOf(
        Type.MCELIECE to McEliece,
        Type.TEST to TestAlg
    )
}
