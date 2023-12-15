package com.example.finalfinalproject.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalfinalproject.R
import com.example.finalfinalproject.adapter.DatabaseHandler
import com.example.finalfinalproject.model.Conversation
import com.example.finalfinalproject.model.Message
import com.example.finalfinalproject.model.User
import com.squareup.picasso.Picasso

class ConversationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.conversation_activity)

        // get current user's conversations
        val dbHandler = DatabaseHandler(this)
        val currentUserId: Int = sharedPreferences.getInt("USER_ID", 0)
        val conversations = dbHandler.getConversations(currentUserId)

        // set up adapter
        val conversationAdapter = ConversationAdapter(conversations, currentUserId, this)
        val recyclerView: RecyclerView = findViewById(R.id.rvConversations)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = conversationAdapter

        // header stuff
        val btnUser = findViewById<ImageButton>(R.id.btnUser)
        Picasso.with(this)
            .load(dbHandler.getUser(currentUserId)!!.image)
            .fit()
            .centerCrop()
            .into(btnUser)
        btnUser.setOnClickListener  {
            val intent = Intent(this, CurrentUserActivity::class.java)
            startActivity(intent)
        }
        val btnCompose = findViewById<ImageButton>(R.id.btnCompose)
        btnCompose.setOnClickListener   {
            val intent = Intent(this, SearchUsersActivity::class.java)
            startActivity(intent)
        }
    }
}

class ConversationAdapter(private val conversationList: List<Conversation>, private val currentUser: Int, private val context: Context) :
RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>()    {
    class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)    {
        val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvLastMsg: TextView = itemView.findViewById(R.id.tvLastMsg)
        val tvLastMsgTime: TextView = itemView.findViewById(R.id.tvLastMsgTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.conversation_preview, parent, false)
        return ConversationViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return conversationList.size
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val currentConversation: Conversation = conversationList[position]
        val dbHandler = DatabaseHandler(context)

        // get the other user, i.e., the user in the conversation that is not the current user
        val otherUser: User = if (currentConversation.conversationUserOne == currentUser)    {
            dbHandler.getUser(currentConversation.conversationUserTwo)!!
        }
        else    {
            dbHandler.getUser(currentConversation.conversationUserOne)!!
        }

        Picasso.with(context)
            .load(otherUser.image)
            .fit()
            .centerCrop()
            .into(holder.ivImage)
        holder.tvName.text = otherUser.name
        Log.d("USER_IMG", otherUser.image)
        Log.d("USER_NAME", otherUser.name)
        Log.d("USER_UNAME", otherUser.userName)
        Log.d("USER_PW", otherUser.password)

        // get last message in conversation
        val lastMsg: Message = dbHandler.getLastMessage(currentConversation.conversationId!!)!!
        val you = if (lastMsg.senderId == currentUser)  {
            "You: "
        } else {
            ""
        }

        holder.tvLastMsg.text = you + lastMsg.text
        holder.tvLastMsgTime.text = lastMsg.date.toString()

        holder.itemView.setOnClickListener  {
            val intent = Intent(context, MessagesActivity::class.java)
            intent.putExtra("conversationId", currentConversation.conversationId)
            intent.putExtra("otherUserId", otherUser.userId)
            context.startActivity(intent)
        }
    }
}