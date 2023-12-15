package com.example.finalfinalproject.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony.Sms.Conversations
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.finalfinalproject.R
import com.example.finalfinalproject.adapter.DatabaseHandler
import com.example.finalfinalproject.model.Message
import com.example.messageuitest.ReceiveMessageView
import com.example.messageuitest.SendMessageView
import com.squareup.picasso.Picasso

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

        // load messages
        val tvName = findViewById<TextView>(R.id.tvOtherUserName)
        val tvUname = findViewById<TextView>(R.id.tvOtherUserUname)

        tvName.text = otherUser.name
        tvUname.text = otherUser.userName


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
        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        scrollView.post(Runnable {
            run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN)
            }
        })
        // messaging
        val etMsg = findViewById<EditText>(R.id.etMsg)
        val btnSend = findViewById<ImageButton>(R.id.btnSend)
        btnSend.setOnClickListener  {
            val msg = etMsg.text.toString()
            dbHandler.addMessage(msg, curUser.userId!!, otherUser.userId!!)
            etMsg.setText("")
            finish()
            overridePendingTransition(0,0)
            startActivity(getIntent())
            overridePendingTransition(0,0)
        }

        // some other stuff
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener  {
            val intent = Intent(this, ConversationsActivity::class.java)
            startActivity(intent)
        }
        val btnOtherUser = findViewById<ImageButton>(R.id.btnOtherUser)
        Picasso.with(this)
            .load(otherUser.image)
            .fit()
            .centerCrop()
            .into(btnOtherUser)
    }

}