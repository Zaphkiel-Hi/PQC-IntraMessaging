package org.niklasunrau.pqcmessenger.domain.model

data class Message(
    val fromId: String,
    val text: String,
    val timestamp: Long
){
    constructor() : this("", "", 0L)
}