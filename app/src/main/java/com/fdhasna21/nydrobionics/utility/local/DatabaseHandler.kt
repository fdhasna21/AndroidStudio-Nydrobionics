package com.fdhasna21.nydrobionics.utility.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class DatabaseHandler(val context: Context)
    :SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION){
    companion object{
        private val DB_NAME = "NydrobionicsDB"
        private val DB_VERSION = 1

        private val USER_TABLE = "users"
        private val ID = "id"
        private val USER_UID = "uid"
        private val USER_EMAIL = "email"
        private val USER_REMEMBER = "remember"
    }

    private fun Boolean.toInt() = if (this) 1 else 0
    private fun Int.toBoolean() = this==1

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS $USER_TABLE(" +
                "$ID INTEGER PRIMARY KEY, " +
                "$USER_UID TEXT, " +
                "$USER_EMAIL TEXT, " +
                "$USER_REMEMBER INTEGER DEFAULT 0);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $USER_TABLE;")
        onCreate(db)
    }

    fun size():Int{
        val countQuery = "SELECT  * FROM $USER_TABLE"
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(countQuery, null)
        val count: Int = cursor.count
        cursor.close()
        return count
    }

    private fun getUser(uid: String) : DatabaseModel{
        val db = this.writableDatabase
        val output = DatabaseModel()
        val sqlCommand = "SELECT * FROM $USER_TABLE WHERE $USER_UID = \"$uid\" LIMIT 1;"
        val cursor = db.rawQuery(sqlCommand, null)

        if(cursor.moveToFirst()){
            output.id = cursor.getInt(cursor.getColumnIndex(ID))
            output.uid = cursor.getString(cursor.getColumnIndex(USER_UID))
            output.email = cursor.getString(cursor.getColumnIndex(USER_EMAIL))
            output.remember = cursor.getInt(cursor.getColumnIndex(USER_REMEMBER)).toBoolean()
        }
        cursor.close()
        return output
    }

    fun signIn(data: DatabaseModel):Boolean{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.apply {
            put(USER_UID, data.uid)
            put(USER_EMAIL, data.email)
            put(USER_REMEMBER, data.remember.toInt())
        }

        val lastData = getUser(data.uid!!)
        if(lastData.email == data.email){
            db.update(USER_TABLE, contentValues, null, null)
        } else {
            db.insert(USER_TABLE, null, contentValues)
        }
        db.close()
        return true
    }

    fun signOut(uid:String):Boolean{
        val db = this.writableDatabase
        val lastData = getUser(uid)
        if(!lastData.remember && lastData.uid == uid){
            db.delete(USER_TABLE, null, null)
        }
        db.close()
        return true
    }
}