package com.example.finalfinalproject.adapter

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.finalfinalproject.model.Message
import com.example.finalfinalproject.model.User

class DatabaseHandler (context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "UserDB"

        private val TABLE_USER = "UserTable"
        private val USER_ID = "id"
        private val USER_FULLNAME = "name"
        private val USER_UNAME = "username"
        private val USER_PASSWORD = "password"

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
                + "$USER_PASSWORD TEXT)")
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
}