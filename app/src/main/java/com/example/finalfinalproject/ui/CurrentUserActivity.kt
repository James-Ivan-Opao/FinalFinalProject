package com.example.finalfinalproject.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalfinalproject.R
import com.example.finalfinalproject.adapter.DatabaseHandler
import com.example.finalfinalproject.model.User
import com.squareup.picasso.Picasso

class CurrentUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.current_user_activity)

        // get necessary info from shared prefs
        val sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)
        val dbHandler = DatabaseHandler(this)
        val curUser = dbHandler.getUser(sharedPreferences.getInt("USER_ID", 0))!!

        // modify information in page accordingly
        val userImg = findViewById<ImageView>(R.id.ivUserPic)
        Picasso.with(this)
            .load(curUser.image)
            .fit()
            .centerCrop()
            .into(userImg)
        userImg.setOnClickListener  {
            changeProfilePicture(userImg, curUser)
        }
        findViewById<TextView>(R.id.tvName).text = curUser.name
        findViewById<TextView>(R.id.tvUsername).text = curUser.userName

        // buttons
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener  {
            val intent = Intent(this, ConversationsActivity::class.java)
            startActivity(intent)
        }

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener    {
            // clear shared preferences
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            // redirect to login page
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val btnChangePw = findViewById<Button>(R.id.btnChangePass)
        btnChangePw.setOnClickListener  {
            changePassword(curUser)
        }

        val btnDeleteUser = findViewById<Button>(R.id.btnDeleteAcc)
        btnDeleteUser.setOnClickListener  {
            deleteAccount(curUser.userId!!)
        }
    }

    fun changeProfilePicture(imageView: ImageView, user: User)  {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.change_picture_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // add listener to radio buttons
        val btnChange = dialog.findViewById<Button>(R.id.btnChange)
        btnChange.setOnClickListener    {
            val dbHandler = DatabaseHandler(this)
            val newLink: String = dialog.findViewById<EditText>(R.id.etImgLink).text.toString()
            user.image = newLink

            try {
                Picasso.with(this)
                    .load(newLink)
                    .into(imageView)

                dbHandler.updateProfilePicture(user)
            } catch (e: Throwable) {
                Toast.makeText(this, "Invalid Image", Toast.LENGTH_LONG)
            }


            dialog.dismiss()
        }

        dialog.show()
    }

    fun changePassword(user: User)    {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.change_password_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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

        val btnChange = dialog.findViewById<Button>(R.id.btnChangePw)
        btnChange.setOnClickListener    {
            val etPw = dialog.findViewById<EditText>(R.id.etPasswordChange)
            val password = etPw.text.toString()
            val etCPw = dialog.findViewById<EditText>(R.id.etConfirmPasswordChange)
            val confirmPassword = etCPw.text.toString()

            if (pwConfirm(password, confirmPassword) && pwValidation(password))   {
                val dbHandler = DatabaseHandler(this)
                user.password = password
                dbHandler.updatePassword(user)
                dialog.dismiss()
            }
            else if (!pwValidation(password))   {
                Toast.makeText(this, "Invalid password", Toast.LENGTH_LONG).show()
            }
            else    {
                Toast.makeText(this, "No fields can be blank", Toast.LENGTH_LONG).show()
            }
        }

        dialog.show()
    }

    fun deleteAccount(userId: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.confirm_delete_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // add listener to radio buttons
        val btnNo = dialog.findViewById<Button>(R.id.btnNo)
        btnNo.setOnClickListener    {
            dialog.dismiss()
        }
        val btnYes = dialog.findViewById<Button>(R.id.btnYes)
        btnYes.setOnClickListener    {
            val dbHandler = DatabaseHandler(this)
            dbHandler.deleteAccount(userId)

            dialog.dismiss()

            // redirect to login page
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        dialog.show()
    }

}