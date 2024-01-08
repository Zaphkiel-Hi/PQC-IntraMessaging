package org.niklasunrau.pqcmessenger.domain.model

data class Message(
    val fromId: String,
    val encryptedText: String,
    val encryptedKeys: Map<String, String>,
    val algorithm: String,
    val timestamp: Long
) {
    @Suppress("unused")
    constructor() : this("", "", mapOf(), "", 0L)
}