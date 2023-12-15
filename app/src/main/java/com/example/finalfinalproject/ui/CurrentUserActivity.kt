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
        findViewById<TextView>(R.id.tvName).text = curUser.name
        findViewById<TextView>(R.id.tvUsername).text = curUser.userName
        userImg.setOnClickListener  {
            changeProfilePicture(userImg, curUser)
        }

        // misc.
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener  {
            val intent = Intent(this, ConversationsActivity::class.java)
            startActivity(intent)
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
            dbHandler.updateProfilePicture(user)

            dialog.dismiss()
        }

        dialog.show()
    }

}