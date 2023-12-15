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
        private val MESSAGE_CONVERSATION = "conversation"
        private val MESSAGE_ID = "id"
        private val MESSAGE_SENDER = "sender"
        private val MESSAGE_RECIPIENT = "recipient"
        private val MESSAGE_TEXT = "message"
        private val MESSAGE_DATETIME = "date"

        private val TABLE_CONVERSATION = "ConversationTable"
        private val CONVERSATION_ID = "id"
        private val CONVERSATION_USERONE = "user_one"
        private val CONVERSATION_USERTWO = "user_two"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_USER_TABLE = ("CREATE TABLE $TABLE_USER ("
                + "$USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$USER_FULLNAME TEXT, "
                + "$USER_UNAME TEXT UNIQUE, "
                + "$USER_PASSWORD TEXT,"
                + "$USER_IMAGE TEXT)")
        val CREATE_MESSAGE_TABLE = ("CREATE TABLE $TABLE_MESSAGE ("
                + "$MESSAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$MESSAGE_CONVERSATION INTEGER,"
                + "$MESSAGE_SENDER INTEGER,"
                + "$MESSAGE_RECIPIENT INTEGER,"
                + "$MESSAGE_TEXT TEXT,"
                + "$MESSAGE_DATETIME TEXT,"
                + "FOREIGN KEY ($MESSAGE_SENDER) REFERENCES $TABLE_USER($USER_ID),"
                + "FOREIGN KEY ($MESSAGE_RECIPIENT) REFERENCES $TABLE_USER($USER_ID),"
                + "FOREIGN KEY ($MESSAGE_CONVERSATION) REFERENCES $TABLE_CONVERSATION($CONVERSATION_ID))"
                )
        val CREATE_CONVERSATION_TABLE = ("CREATE TABLE $TABLE_CONVERSATION ("
                + "$CONVERSATION_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$CONVERSATION_USERONE INTEGER,"
                + "$CONVERSATION_USERTWO INTEGER,"
                + "FOREIGN KEY ($CONVERSATION_USERONE) REFERENCES $TABLE_USER($USER_ID),"
                + "FOREIGN KEY ($CONVERSATION_USERTWO) REFERENCES $TABLE_USER($USER_ID))"
                )
        db?.execSQL(CREATE_USER_TABLE)
        db?.execSQL(CREATE_MESSAGE_TABLE)
        db?.execSQL(CREATE_CONVERSATION_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_USER)
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE)
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVERSATION)
        onCreate(db)
    }

    // creates a user
    fun addUser(user: User):Long  {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(USER_FULLNAME, user.name)
        contentValues.put(USER_UNAME, user.userName)
        contentValues.put(USER_PASSWORD, user.password)
        contentValues.put(USER_IMAGE, user.image)

        val success = db.insert(TABLE_USER, null, contentValues)
        db.close()
        return success
    }

    // adds a conversation to the database
    fun addMessage(message: String, sender: Int, recipient: Int): Int  {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        // if no conversation exists between the two users, this should create a new one
        val conversation: Conversation =
            if (getConversation(sender, recipient) == null)   {
                addConversation(sender, recipient)
                getConversation(sender, recipient)!!
            }
            else    {
                getConversation(sender, recipient)!!
            }

        contentValues.put(MESSAGE_CONVERSATION, conversation.conversationId)
        contentValues.put(MESSAGE_SENDER, sender)
        contentValues.put(MESSAGE_RECIPIENT, recipient)
        contentValues.put(MESSAGE_TEXT, message)
        contentValues.put(MESSAGE_DATETIME, LocalDateTime.now().toString())

        val success = db.insert(TABLE_MESSAGE, null, contentValues)
        db.close()

        return conversation.conversationId ?: 0
    }

    // starts a new conversation
    fun addConversation(userOne: Int, userTwo: Int):Long   {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(CONVERSATION_USERONE, userOne)
        contentValues.put(CONVERSATION_USERTWO, userTwo)

        val success = db.insert(TABLE_CONVERSATION, null, contentValues)
        return success
    }

    // this is for getting a user based on their ID
    // you might use it for getting names, images, etc.
    @SuppressLint("Range")
    fun getUser(id: Int):User?    {
        val selectQuery = "SELECT * FROM $TABLE_USER WHERE $USER_ID='$id'"
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

                return User(userId=userId, name=fullName, userName = userName, password = password, image = image)
            }
        } finally {
            cursor?.close()
            db.close()
        }

        return null
    }

    // this is for getting a user based on their login credentials
    @SuppressLint("Range")
    fun getUser(username: String, password: String):User?    {
        val selectQuery = "SELECT * FROM $TABLE_USER WHERE $USER_UNAME='$username' AND $USER_PASSWORD='$password'"
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

                return User(userId=userId, name=fullName, userName = userName, password = password, image = image)
            }
        } finally {
            cursor?.close()
            db.close()
        }

        return null
    }

    // this is for getting messages from a conversation
    @SuppressLint("Range")
    fun getMessages(conversation: Int): ArrayList<Message> {
        val msgList:ArrayList<Message> = ArrayList<Message>()
        val selectQuery = "SELECT * FROM $TABLE_MESSAGE WHERE $MESSAGE_CONVERSATION='$conversation'"
        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        }
        catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return msgList
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

                val msg = Message(messageId = messageId, conversationId = conversation, senderId = senderId, receiverId = receiverId, text = text, date = LocalDateTime.parse(date.replace(" ","T")))
                msgList.add(msg)
            } while (cursor.moveToNext())
        }

        return msgList
    }

    // this returns a list of conversations where a given user is a participant
    @SuppressLint("Range")
    fun getConversations(userId: Int): ArrayList<Conversation>    {
        val conversationList:ArrayList<Conversation> = ArrayList<Conversation>()
        val selectQuery = "SELECT * FROM $TABLE_CONVERSATION WHERE $CONVERSATION_USERONE='$userId' OR $CONVERSATION_USERTWO='$userId'"
        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        }
        catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return conversationList
        }

        var conversationId: Int
        var userOneId: Int
        var userTwoId: Int

        if(cursor.moveToFirst()){
            do{
                conversationId = cursor.getInt(cursor.getColumnIndex(CONVERSATION_ID))
                userOneId = cursor.getInt(cursor.getColumnIndex(CONVERSATION_USERONE))
                userTwoId = cursor.getInt(cursor.getColumnIndex(CONVERSATION_USERTWO))

                val conversation = Conversation(conversationId = conversationId, conversationUserOne = userOneId, conversationUserTwo = userTwoId)

                conversationList.add(conversation)
            } while (cursor.moveToNext())
        }

        return conversationList
    }

    // this is a function for getting a specific conversation between two users
    @SuppressLint("Range")
    fun getConversation(userOne: Int, userTwo: Int): Conversation?  {
        val selectQuery = ("SELECT * FROM $TABLE_CONVERSATION WHERE "
                            + "($CONVERSATION_USERONE='$userOne' AND $CONVERSATION_USERTWO='$userTwo')"
                            + "OR ($CONVERSATION_USERONE='$userTwo' AND $CONVERSATION_USERTWO='$userOne')")
        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        }
        catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return null
        }

        var conversationId: Int
        var userOneId: Int
        var userTwoId: Int

        if(cursor.moveToFirst()){
            do{
                conversationId = cursor.getInt(cursor.getColumnIndex(CONVERSATION_ID))
                userOneId = cursor.getInt(cursor.getColumnIndex(CONVERSATION_USERONE))
                userTwoId = cursor.getInt(cursor.getColumnIndex(CONVERSATION_USERTWO))

                return Conversation(conversationId = conversationId, conversationUserOne = userOneId, conversationUserTwo = userTwoId)
            } while (cursor.moveToNext())
        }

        return null
    }

    // get last message from conversation
    @SuppressLint("Range")
    fun getLastMessage(conversation: Int): Message?   {
        val selectQuery = "SELECT * FROM $TABLE_MESSAGE WHERE $MESSAGE_CONVERSATION='$conversation' ORDER BY $MESSAGE_DATETIME DESC LIMIT 1"
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

                return Message(messageId = messageId, conversationId = conversation, senderId = senderId, receiverId = receiverId, text = text, date = LocalDateTime.parse(date.replace(" ","T")))
            } while (cursor.moveToNext())
        }

        return null
    }

    fun updateProfilePicture(user: User ) : Int  {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(USER_FULLNAME, user.name)
        contentValues.put(USER_UNAME, user.userName)
        contentValues.put(USER_PASSWORD, user.password)
        contentValues.put(USER_IMAGE, user.image)

        var success = db.update(TABLE_USER, contentValues, "id="+user.userId, null)
        db.close()
        return success
    }

    fun updatePassword(user: User) : Int    {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(USER_FULLNAME, user.name)
        contentValues.put(USER_UNAME, user.userName)
        contentValues.put(USER_PASSWORD, user.password)
        contentValues.put(USER_IMAGE, user.image)

        var success = db.update(TABLE_USER, contentValues, "id="+user.userId, null)
        db.close()
        return success
    }

    fun deleteAccount(userId: Int) : Int    {
        val db = this.writableDatabase

        val success = db.delete(TABLE_USER, "id=$userId", null)
        db.close()
        return success
    }

    // get all users (except the current user)
   @SuppressLint("Range")
    fun getUsers(userId: Int) : ArrayList<User> {
        val userList: ArrayList<User> = ArrayList<User>()
        val query = "SELECT * FROM $TABLE_USER WHERE $USER_ID <> $userId"
        val db = this.readableDatabase
        var cursor:Cursor? = null
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: SQLiteException) {
            db.execSQL(query)
            return ArrayList()
        }

        var userId: Int
        var fullName: String
        var userName: String
        var password: String
        var image: String

        if (cursor.moveToFirst()) {
            do {
                userId = cursor.getInt(cursor.getColumnIndex(USER_ID))
                fullName = cursor.getString(cursor.getColumnIndex(USER_FULLNAME))
                userName = cursor.getString(cursor.getColumnIndex(USER_UNAME))
                password = cursor.getString(cursor.getColumnIndex(USER_PASSWORD))
                image = cursor.getString(cursor.getColumnIndex(USER_IMAGE))

                val user = User(userId=userId, name=fullName, userName = userName, password = password, image = image)
                userList.add(user)
            } while (cursor.moveToNext())
        }
        return userList
    }
}