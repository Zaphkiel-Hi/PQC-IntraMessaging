package org.niklasunrau.pqcmessenger.domain.model

import com.google.firebase.firestore.DocumentId

data class Message(
    @DocumentId val id: String = ""
)