package com.example.finalfinalproject.ui

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.finalfinalproject.R
import com.example.finalfinalproject.adapter.DatabaseHandler
import com.example.finalfinalproject.model.Message
import com.example.finalfinalproject.model.User
import com.example.messageuitest.ReceiveMessageView
import com.example.messageuitest.SendMessageView
import java.text.SimpleDateFormat
import java.time.LocalDateTime

class MessagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.messages_activity)

        // get necessary info from shared prefs and intent
        val sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)
        val dbHandler = DatabaseHandler(this)

        var curUser = dbHandler.getUser(sharedPreferences.getInt("USER_ID", 0))!!
        var otherUser = dbHandler.getUser(intent.getIntExtra("otherUserId", 0))!!
        val messages: ArrayList<Message> = dbHandler.getMessages(intent.getIntExtra("conversationId", 0))

        val mainView = findViewById<LinearLayout>(R.id.messagesView)

        val img1 = findViewById<ImageView>(R.id.imageView1)
        val img2 = findViewById<ImageView>(R.id.imageView2)

        for ((i, message) in messages.withIndex()) {
            if (message.receiverId == curUser.userId) {
                var messageView: ReceiveMessageView = ReceiveMessageView(this)
                messageView.setMessageText(message.text)
                messageView.setProfileImage(otherUser.image!!)
                messageView.setMessageDate(message.date.toString())
                mainView.addView(messageView, i)
            } else {
                var messageView: SendMessageView = SendMessageView(this)
                messageView.setMessageText(message.text)
                messageView.setProfileImage(curUser.image!!)
                messageView.setMessageDate(message.date.toString())
                mainView.addView(messageView, i)
            }
        }
    }
}