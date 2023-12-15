package com.example.finalfinalproject.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalfinalproject.R
import com.example.finalfinalproject.adapter.DatabaseHandler
import com.example.finalfinalproject.model.Conversation
import com.example.finalfinalproject.model.Message
import com.example.finalfinalproject.model.User
import com.squareup.picasso.Picasso

class SearchUsersActivity : AppCompatActivity() {
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UsersAdapter
    private lateinit var userList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_users_activity)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener  {
            val intent = Intent(this, ConversationsActivity::class.java)
            startActivity(intent)
        }

        val sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)
        var dbHandler = DatabaseHandler(this)
        var currentUserId = sharedPreferences.getInt("USER_ID", 0)
        userList = dbHandler.getUsers(currentUserId)

        searchView = findViewById<SearchView>(R.id.searchView)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UsersAdapter(this, currentUserId)
        recyclerView.adapter = adapter



        // Set up SearchView listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter the userList based on the search query
                val filteredList = userList.filter {
                    it.userName.contains(newText.orEmpty(), ignoreCase = true) || it.name.contains(newText.orEmpty(), ignoreCase = true)
                }

                // Update the RecyclerView with the filtered results
                adapter.updateData(ArrayList<User>(filteredList))
                return true
            }
        })
    }
}

class UsersAdapter(private val context: Context, private val currentUserId: Int) :
    RecyclerView.Adapter<UsersAdapter.UserViewHolder>()    {
    var userList: ArrayList<User> = ArrayList<User>()

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)    {
        // Declare UI elements in the ViewHolder
        val ivUserImage: ImageView = itemView.findViewById(R.id.ivUserImg)
        val tvName: TextView = itemView.findViewById(R.id.tvRName)
        val tvUname: TextView = itemView.findViewById(R.id.tvRUname)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        // Inflate the item layout and create a ViewHolder
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_row, parent, false)
        return UserViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        // Bind data to the ViewHolder
        val user: User = userList[position]
        val dbHandler = DatabaseHandler(context)

        Picasso.with(context)
            .load(user.image)
            .fit()
            .centerCrop()
            .into(holder.ivUserImage)
        holder.tvName.text = user.name
        holder.tvUname.text = user.userName

        holder.itemView.setOnClickListener  {
            val intent = Intent(context, MessagesActivity::class.java)
            intent.putExtra("otherUserId", user.userId)

            val conversation: Conversation? = dbHandler.getConversation(currentUserId, user.userId!!)
            if (conversation != null)
                intent.putExtra("conversationId", conversation.conversationId)

            context.startActivity(intent)
        }
    }

    fun updateData(newData: ArrayList<User>)    {
        userList = newData
        notifyDataSetChanged()
    }
}
