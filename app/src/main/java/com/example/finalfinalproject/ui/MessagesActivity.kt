package com.example.finalfinalproject.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.finalfinalproject.R
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
        var curUser = User(0, "user0", "123", "name0", "https://i.imgur.com/YcP0tik.jpeg")
        var otherUser = User(1, "user1", "123", "name1", "https://i.imgur.com/S7fpU0O.jpeg")
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        var now: LocalDateTime = LocalDateTime.now()

        val messages: ArrayList<Message> = arrayListOf(
            Message(0, 1, 0, 1, "Hello", now.minusSeconds(50)),
            Message(1, 1, 1, 0, "Hi!", now.minusSeconds(40)),
            Message(2, 1, 0, 1, "What's up?", now.minusSeconds(30)),
            Message(3, 1, 0, 1, "How can I help you?", now.minusSeconds(20)),
            Message(4, 1, 1, 0, "Nothing just saying hi", now.minusSeconds(10))
        )
        print("HELLO DEBUG")
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