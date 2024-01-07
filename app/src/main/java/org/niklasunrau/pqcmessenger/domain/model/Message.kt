package org.niklasunrau.pqcmessenger.domain.model

data class Message(
    val fromId: String,
    val encryptedText: String,
    val encryptedKey: String,
    val algorithm: String,
    val timestamp: Long
){
    constructor() : this("", "", "", "", 0L)
}