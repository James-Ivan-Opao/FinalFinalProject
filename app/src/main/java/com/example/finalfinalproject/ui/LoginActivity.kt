package com.example.finalfinalproject.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.finalfinalproject.R
import com.example.finalfinalproject.adapter.DatabaseHandler
import com.example.finalfinalproject.model.User

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        // Background Animation Stuff
        var mainLayout = findViewById<LinearLayout>(R.id.gradientBackground);
        val frameAnimation: AnimationDrawable = mainLayout.background as AnimationDrawable
        frameAnimation.setEnterFadeDuration(250)
        frameAnimation.setExitFadeDuration(500)
        frameAnimation.start()
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
                showError("User with provided credentials not found")
            }
        }
    }

    fun signupRedirect(view: View)   {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun showError(msg: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.error_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val dialogClose = dialog.findViewById<Button>(R.id.bClose)
        dialogClose.setOnClickListener {
            dialog.dismiss()
        }

        val tvError = dialog.findViewById<TextView>(R.id.tvError)
        tvError.text = msg

        dialog.show()
    }
}