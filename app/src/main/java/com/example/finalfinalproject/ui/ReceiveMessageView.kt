
package com.example.messageuitest

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.finalfinalproject.R
import com.squareup.picasso.Picasso
import kotlin.math.abs

class ReceiveMessageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr){
    private val userImage: ImageView
    private val messageText: TextView

    init {
        inflate(this.context, R.layout.message_receive,this)

        userImage = findViewById<ImageView>(R.id.ivUserProfile)
        messageText = findViewById<TextView>(R.id.tvMessage)
    }


    fun setProfileImage(imageUrl: String) {
        Picasso.with(context)
            .load(imageUrl)
            .fit()
            .centerCrop()
            .into(userImage);

    }

    fun setMessageText(text: String) {
        messageText.text = text
    }

    fun setMessageDate(date: String) {
        messageText.setOnClickListener {
            Toast.makeText(context, date, Toast.LENGTH_LONG).show()
        }
    }
}