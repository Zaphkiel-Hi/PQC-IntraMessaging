package org.niklasunrau.pqcmessenger.domain.model

data class Message(
    val fromId: String,
    val encryptedText: String,
    val encryptedKeys: Map<String, String>,
    val algorithm: String,
    val timestamp: Long
) {
    constructor() : this("", "", mapOf(), "", 0L)
}