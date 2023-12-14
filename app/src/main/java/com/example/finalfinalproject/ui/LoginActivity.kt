package com.example.finalfinalproject.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.finalfinalproject.R
import com.example.finalfinalproject.adapter.DatabaseHandler
import com.example.finalfinalproject.model.User

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
    }

    fun login(view: View)   {
        val etUsername = findViewById<EditText>(R.id.etLoginUsername)
        val etPassword = findViewById<EditText>(R.id.etLoginPassword)

        val uname = etUsername.text.toString();
        val password = etPassword.text.toString();

        // DB stuff
        val dbHandler = DatabaseHandler(this)

        fun notNull(str: String):Boolean    {
            return str.trim() != ""
        }

        if (notNull(uname) && notNull(password)) {
            val user: User? = dbHandler.getUser(uname, password)

            if (user != null)    {
                val sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.apply {
                    putInt("USER_ID", user.userId!!)
                    putString("USER_NAME", user.name)
                    putString("USER_UNAME", user.userName)
                    putString("USER_IMAGE", user.image)
                }.apply()

                // Redirect to login page
                val i = Intent(this, ConversationsActivity::class.java)
                startActivity(i)
            }
            else    {
                Toast.makeText(this, "User with provided credentials not found", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun signupRedirect(view: View)   {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}