package org.niklasunrau.pqcmessenger.presentation.util

data class DecryptedMessage(
    val fromId: String,
    val text: String,
    val timestamp: Long
)
