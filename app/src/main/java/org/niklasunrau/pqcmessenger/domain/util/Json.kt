package org.niklasunrau.pqcmessenger.domain.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricPublicKey
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricSecretKey
import org.niklasunrau.pqcmessenger.domain.crypto.mceliece.McEliecePublicKey
import org.niklasunrau.pqcmessenger.domain.crypto.mceliece.McElieceSecretKey

object Json {
    private val module = SerializersModule {
        polymorphic(AsymmetricSecretKey::class) {
            subclass(McElieceSecretKey::class)
        }
        polymorphic(AsymmetricPublicKey::class) {
            subclass(McEliecePublicKey::class)
        }
    }
    val json = Json { serializersModule = module }

}