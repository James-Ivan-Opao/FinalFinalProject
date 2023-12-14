package com.example.finalfinalproject.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalfinalproject.R
import com.example.finalfinalproject.adapter.DatabaseHandler
import com.example.finalfinalproject.model.User

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)
    }

    fun signUp(view: View)   {
        val etName = findViewById<EditText>(R.id.etName)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etPasswordConfirm = findViewById<EditText>(R.id.etConfirmPassword)

        val name = etName.text.toString()
        val username = etUsername.text.toString()
        val password = etPassword.text.toString()
        val passwordConfirm = etPasswordConfirm.text.toString()

        // DB stuff
        val dbHandler = DatabaseHandler(this)

        fun notNull(str: String):Boolean    {
            return str.trim() != ""
        }
        fun pwConfirm(str1: String, str2: String):Boolean   {
            return (str1 == str2)
        }
        fun pwValidation(str: String):Boolean   {
            // must contain small letter, capital letter, number and special character. Minimum of 8 characters
            return (str.length >= 8
                    && str.contains(Regex("[a-z]"))
                    && str.contains(Regex("[A-Z]"))
                    && str.contains(Regex("[0-9]"))
                    && str.contains(Regex("[!\"`'#%&,:;<>=@{}~\\\$\\(\\)\\*\\+\\/\\\\\\?\\[\\]\\^\\|]"))
                    )
        }

        if (notNull(name) && notNull(username) && notNull(password) && notNull(passwordConfirm) && pwConfirm(password, passwordConfirm) && pwValidation(password)) {
            val status = dbHandler.addUser(User(userName=username, name=name, password=password, userId = null, image = "https://i.imgur.com/S7fpU0O.jpeg"))
            if (status > -1)    {
                // Display successful Sign Up toast
                Toast.makeText(this, "Signed up successfully", Toast.LENGTH_LONG).show()

                // Redirect to login page
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        else if (!pwValidation(password))   {
            Toast.makeText(this, "Invalid password", Toast.LENGTH_LONG).show()
        }
        else    {
            Toast.makeText(this, "No fields can be blank", Toast.LENGTH_LONG).show()
        }
    }

    fun loginPageRedirect(view: View)   {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}