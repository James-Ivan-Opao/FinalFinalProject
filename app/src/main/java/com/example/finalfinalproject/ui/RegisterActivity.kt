package com.example.finalfinalproject.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalfinalproject.R
import com.example.finalfinalproject.adapter.DatabaseHandler
import com.example.finalfinalproject.model.User

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        // Background Animation Stuff
        var mainLayout = findViewById<LinearLayout>(R.id.gradientBackground);
        val frameAnimation: AnimationDrawable = mainLayout.background as AnimationDrawable
        frameAnimation.setEnterFadeDuration(250)
        frameAnimation.setExitFadeDuration(500)
        frameAnimation.start()
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
            val status = dbHandler.addUser(User(userName=username, name=name, password=password, userId = null, image = getString(R.string.default_picture)))
            if (status > -1)    {
                // Display successful Sign Up toast
                Toast.makeText(this, "Signed up successfully", Toast.LENGTH_LONG).show()
                showSuccess()
                // Redirect to login page
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        else if (!pwValidation(password))   {
            showError("Invalid password")
        }
        else    {
            showError("No fields can be blank")
        }
    }

    private fun showSuccess() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.success_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val dialogClose = dialog.findViewById<Button>(R.id.bClose)
        dialogClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun loginPageRedirect(view: View)   {
        val intent = Intent(this, LoginActivity::class.java)
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