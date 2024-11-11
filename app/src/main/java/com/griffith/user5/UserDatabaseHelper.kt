package com.griffith.user5

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Constants for database version and name
 const val DATABASE_VERSION = 1
 const val DATABASE_NAME = "user_database"

// Constants for table and column names
 const val TABLE_USERS = "users"
 const val TABLE_MESSAGES = "messages"
 const val COLUMN_ID = "id"
 const val COLUMN_NAME = "name"
 const val COLUMN_USER_ID = "user_id" // Foreign key for users
 const val COLUMN_MESSAGE_ID = "message_id"
 const val COLUMN_PASSWORD = "password"
 const val COLUMN_MESSAGE = "message"
 const val COLUMN_ROLE = "role" // Use for adm priveliges



// UserDatabaseHelper class that extends SQLiteOpenHelper to manage database creation and version management
class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    // Override the onCreate method to create the database tables
    override fun onCreate(db: SQLiteDatabase) {
        // SQL statement to create the users table
        val createUsersTable = ("CREATE TABLE $TABLE_USERS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NAME TEXT, "
                + "$COLUMN_PASSWORD TEXT, "
                + "$COLUMN_ROLE TEXT" + ")")
        db.execSQL(createUsersTable)


        // SQL statement to create the messages table
        val createMessagesTable = ("CREATE TABLE $TABLE_MESSAGES ("
                + "$COLUMN_MESSAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_USER_ID INTEGER, "
                + "$COLUMN_MESSAGE TEXT, "
                + "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID)" + ")")
        db.execSQL(createMessagesTable)
        //create admin user
        insertInitialAdmin(db)
    }

    // Override the onUpgrade method to handle database version upgrades
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop the existing users table if it exists
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        // Call onCreate to recreate the table
        onCreate(db)
    }

    private fun insertInitialAdmin(db: SQLiteDatabase) {

        // check if adm already exist
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_ROLE = 'admin'", null)
        if (cursor.count == 0) {// else crate adm user
            val insertAdminSQL = ("INSERT INTO $TABLE_USERS ($COLUMN_NAME, $COLUMN_PASSWORD, $COLUMN_ROLE) "
                    + "VALUES ('admin', 'admin', 'admin')")
            db.execSQL(insertAdminSQL)
        }
        cursor.close()
    }

}
