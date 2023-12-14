package com.example.finalfinalproject.adapter

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.finalfinalproject.model.Conversation
import com.example.finalfinalproject.model.Message
import com.example.finalfinalproject.model.User
import java.time.LocalDateTime
import java.util.Date

class DatabaseHandler (context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "UserDB"

        private val TABLE_USER = "UserTable"
        private val USER_ID = "id"
        private val USER_FULLNAME = "name"
        private val USER_UNAME = "username"
        private val USER_PASSWORD = "password"
        private val USER_IMAGE = "image"

        private val TABLE_MESSAGE = "MessageTable"
        private val MESSAGE_ID = "id"
        private val MESSAGE_SENDER = "sender"
        private val MESSAGE_RECIPIENT = "recipient"
        private val MESSAGE_TEXT = "message"
        private val MESSAGE_DATETIME = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_USER_TABLE = ("CREATE TABLE $TABLE_USER ("
                + "$USER_ID INT PRIMARY KEY AUTOINCREMENT, "
                + "$USER_FULLNAME TEXT, "
                + "$USER_UNAME TEXT UNIQUE, "
                + "$USER_PASSWORD TEXT,"
                + "$USER_IMAGE TEXT)")
        val CREATE_MESSAGE_TABLE = ("CREATE TABLE $TABLE_MESSAGE ("
                + "$MESSAGE_ID INT PRIMARY KEY AUTOINCREMENT,"
                + "$MESSAGE_SENDER INT,"
                + "$MESSAGE_RECIPIENT INT,"
                + "$MESSAGE_TEXT TEXT,"
                + "$MESSAGE_DATETIME TEXT,"
                + "FOREIGN KEY ($MESSAGE_SENDER) REFERENCES $TABLE_USER($USER_ID),"
                + "FOREIGN KEY ($MESSAGE_RECIPIENT) REFERENCES $TABLE_USER($USER_ID)"
                )
        db?.execSQL(CREATE_USER_TABLE)
        db?.execSQL(CREATE_MESSAGE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_USER)
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE)
        onCreate(db)
    }

    fun addUser(user: User):Long  {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(USER_ID, user.userId)
        contentValues.put(USER_FULLNAME, user.name)
        contentValues.put(USER_UNAME, user.userName)
        contentValues.put(USER_PASSWORD, user.password)
        contentValues.put(USER_IMAGE, user.image)

        val success = db.insert(TABLE_USER, null, contentValues)
        db.close()
        return success
    }

    fun addMessages(message: Message, sender: User, recipient: User):Long  {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(MESSAGE_ID, message.messageId)
        contentValues.put(MESSAGE_SENDER, sender.userId)
        contentValues.put(MESSAGE_RECIPIENT, recipient.userId)
        contentValues.put(MESSAGE_TEXT, message.text)
        contentValues.put(MESSAGE_DATETIME, message.date.toString())

        val success = db.insert(TABLE_MESSAGE, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getUser(id: Int):User?    {
        val selectQuery = "SELECT * FROM $TABLE_USER WHERE $USER_ID=$id"
        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)

            // Check if the cursor has data
            if (cursor != null && cursor.moveToFirst()) {
                // Extract data from the cursor and create a User object
                val userId = cursor.getInt(cursor.getColumnIndex(USER_ID))
                val fullName = cursor.getString(cursor.getColumnIndex(USER_FULLNAME))
                val userName = cursor.getString(cursor.getColumnIndex(USER_UNAME))
                val password = cursor.getString(cursor.getColumnIndex(USER_PASSWORD))
                val image = cursor.getString(cursor.getColumnIndex(USER_IMAGE))

                return User(userId, fullName, userName, password, image)
            }
        } finally {
            cursor?.close()
            db.close()
        }

        return null
    }

    @SuppressLint("Range")
    fun getMessages(sender: User, recipient: User): Conversation? {
        val msgList:ArrayList<Message> = ArrayList<Message>()
        val selectQuery = "SELECT * FROM $TABLE_MESSAGE WHERE $MESSAGE_SENDER=$sender AND $MESSAGE_RECIPIENT=$recipient"
        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        }
        catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return null
        }

        var messageId: Int
        var senderId: Int
        var receiverId: Int
        var text: String
        var date: String

        if(cursor.moveToFirst()){
            do{
                messageId = cursor.getInt(cursor.getColumnIndex(MESSAGE_ID))
                senderId = cursor.getInt(cursor.getColumnIndex(MESSAGE_SENDER))
                receiverId = cursor.getInt(cursor.getColumnIndex(MESSAGE_RECIPIENT))
                text = cursor.getString(cursor.getColumnIndex(MESSAGE_TEXT))
                date = cursor.getString(cursor.getColumnIndex(MESSAGE_DATETIME))

                val msg = Message(messageId = messageId, senderId = senderId, receiverId = receiverId, text = text, date = LocalDateTime.parse(date))
                msgList.add(msg)
            } while (cursor.moveToNext())
        }

        return Conversation(recipient, msgList)
    }
}