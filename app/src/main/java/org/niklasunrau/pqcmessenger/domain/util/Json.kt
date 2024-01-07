package org.niklasunrau.pqcmessenger.domain.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricPublicKey
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricSecretKey
import org.niklasunrau.pqcmessenger.domain.crypto.mceliece.McEliecePublicKey
import org.niklasunrau.pqcmessenger.domain.crypto.mceliece.McElieceSecretKey
import org.niklasunrau.pqcmessenger.domain.crypto.tmp.PK
import org.niklasunrau.pqcmessenger.domain.crypto.tmp.SK

object Json {
    private val module = SerializersModule {
        polymorphic(AsymmetricSecretKey::class) {
            subclass(McElieceSecretKey::class)
            subclass(SK::class)
        }
        polymorphic(AsymmetricPublicKey::class) {
            subclass(McEliecePublicKey::class)
            subclass(PK::class)
        }
    }
    val json = Json { serializersModule = module }

}