package org.niklasunrau.pqcmessenger.domain.model

import com.google.firebase.firestore.DocumentId
import java.sql.Timestamp

data class Message(
    @DocumentId val id: String,
    val fromId: String,
    val text: String,
    val timestamp: Timestamp
)