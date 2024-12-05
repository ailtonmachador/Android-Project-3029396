package com.griffith.user5


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

// Constants for database version and name
 const val DATABASE_VERSION = 1
 const val DATABASE_NAME = "user_database"

// Constants for table and column names
 const val TABLE_USERS = "users"
 const val TABLE_MESSAGES = "messages"
const val TABLE_REQUEST = "request"
 const val COLUMN_ID = "id"
 const val COLUMN_NAME = "name"
 const val COLUMN_USER_ID = "user_id" // Foreign key for users
 const val COLUMN_USER_ID2 = "user_id"
 const val COLUMN_MESSAGE_ID = "message_id"
 const val COLUMN_PASSWORD = "password"
 const val COLUMN_MESSAGE = "message"
const val COLUMN_ROLE = "role" // Use for adm privileges
const val COLUMN_USER_NAME = "user_name"
const val COLUMN_REQUEST_DAY ="requested_date"
const val COLUMN_STATUS = "status"

/**
 * UserDatabaseHelper manages database creation, updates, and common operations for the application.
 * It includes tables for users, messages, and day-off requests.
 */


// UserDatabaseHelper class that extends SQLiteOpenHelper to manage database creation and version management
class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Creates the necessary tables when the database is first created
    override fun onCreate(db: SQLiteDatabase) {
        // Create the users table
        val createUsersTable = ("CREATE TABLE $TABLE_USERS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NAME TEXT, "
                + "$COLUMN_PASSWORD TEXT, "
                + "$COLUMN_ROLE TEXT" + ")")
        db.execSQL(createUsersTable)

        // Create the messages table
        val createMessagesTable = ("CREATE TABLE $TABLE_MESSAGES ("
                + "$COLUMN_MESSAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_USER_ID INTEGER, "
                + "$COLUMN_MESSAGE TEXT, "
                + "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID)" + ")")
        Log.d("UserDatabaseHelper", "Creating messages table")
        db.execSQL(createMessagesTable)

        // Create the requests table for day-off requests
        val createRequestsTable = ("CREATE TABLE requests ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_name TEXT, "
                + "requested_date TEXT, "
                + "status TEXT)")
        Log.d("UserDatabaseHelper", "Creating requests table")
        db.execSQL(createRequestsTable)

        // Insert initial admin user
        insertInitialAdmin(db)
    }

    // Handles database upgrades, dropping and recreating tables as needed
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS") // Drop users table
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES") // Drop messages table
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REQUEST") // Drop requests table
        onCreate(db) // Recreate tables
    }

    // Inserts an initial admin user if none exists
    private fun insertInitialAdmin(db: SQLiteDatabase) {
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_ROLE = 'admin'", null)
        if (cursor.count == 0) { // Create admin user if not found
            val insertAdminSQL = ("INSERT INTO $TABLE_USERS ($COLUMN_NAME, $COLUMN_PASSWORD, $COLUMN_ROLE) "
                    + "VALUES ('admin', 'admin', 'admin')")
            db.execSQL(insertAdminSQL)
        }
        cursor.close()
    }

    // Retrieves all day-off requests from the database
    fun getAllRequests(): List<Request> {
        val requests = mutableListOf<Request>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM requests", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("user_name"))
                val day = cursor.getString(cursor.getColumnIndexOrThrow("requested_date"))
                val status = cursor.getString(cursor.getColumnIndexOrThrow("status"))

                // Add the request to the list
                requests.add(Request(id, name, day, status))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return requests
    }

    // Deletes a specific day-off request by ID
    fun deleteRequest(id: Int): Int {
        val db = writableDatabase
        return db.delete("requests", "id = ?", arrayOf(id.toString()))
    }
}
