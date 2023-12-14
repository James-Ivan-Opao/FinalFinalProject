package com.example.finalfinalproject.model

import java.util.Date

data class Message(val messageId: Int, val senderId: User, val receiverId: User, val text: String, val date: Date)
