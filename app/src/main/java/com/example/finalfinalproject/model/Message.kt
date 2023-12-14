package com.example.finalfinalproject.model

import java.time.LocalDateTime

data class Message(val messageId: Int, val senderId: Int, val receiverId: Int, val text: String, val date: LocalDateTime)
