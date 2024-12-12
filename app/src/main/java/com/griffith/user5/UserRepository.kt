package com.griffith.user5

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log

/**
The UserRepository class handles all database operations related to users, including adding users, validating credentials,
sending messages, and checking authentication status. It also interacts with SharedPreferences to manage user session.
 */
class UserRepository(context: Context) {

    // Instantiate UserDatabaseHelper to manage database creation and access
    val dbHelper = UserDatabaseHelper(context)
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Add a new user to the database
    fun addUser(name: String, password: String) {
        // Get a writable database instance
        val db = dbHelper.writableDatabase
        // Create a ContentValues object to hold user data
        val values = ContentValues().apply {
            put("name", name) // Put the user's name in ContentValues
            put("password", password) // Add the password to ContentValues
        }

        // Insert the new user into the 'users' table
        val newRowId = db.insert("users", null, values)

        // Log the registration information
        Log.d("UserRegistration", "User added: Name: $name, Password: $password, Row ID: $newRowId")

        // Close the database connection
        db.close()
    }

    // Get the user ID by name
    @SuppressLint("Range")
    fun getUserIdByName(name: String): Int? {
        // Get a readable database instance
        val db = dbHelper.readableDatabase
        // Execute a raw SQL query to select the user ID based on the user's name
        val cursor: Cursor = db.rawQuery("SELECT id FROM users WHERE name = ?", arrayOf(name))

        // Check if the cursor contains any results
        return if (cursor.moveToFirst()) {
            // If there is a result, get the user ID from the cursor
            cursor.getInt(cursor.getColumnIndex("id"))
        } else {
            // If no user is found, return null
            null
        }.also {
            // Close the cursor to free up resources after use
            cursor.close()
        }
    }

    // Send a message to a user
    fun sendMessage(userId: Int, message: String) {
        // Get a writable database instance
        val db = dbHelper.writableDatabase
        // Create a ContentValues object to hold the message data
        val values = ContentValues().apply {
            // Put the user ID and message into the ContentValues object
            put("user_id", userId)
            put("message", message)
        }
        // Insert the message into the messages table
        db.insert("messages", null, values)
        db.close() // Close the database connection
    }

    //update location  for clock in
    fun updateClockin(lat: Double, lon: Double, rad: Double) {
        val db = dbHelper.writableDatabase
        db.execSQL("DELETE FROM $TABLE_CLOCKIN") // Clear previous data
        val contentValues = ContentValues().apply {
            put(COLUMN_LAT, lat)
            put(COLUMN_LON, lon)
            put(COLUMN_RAD, rad)
        }
        db.insert(TABLE_CLOCKIN, null, contentValues)
    }



    //retrive location already set for user do clock in
    fun getClockinLocations(): List<Triple<Double, Double, Double>> {
        val db = dbHelper.readableDatabase
        val locations = mutableListOf<Triple<Double, Double, Double>>() // list to store results

        val cursor = db.query(
            "clockin",
            arrayOf("lat", "lon", "rad"),
            null,
            null,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val lat = cursor.getDouble(cursor.getColumnIndexOrThrow("lat")) // get lat
                Log.d(TAG, "---> 2 Marker updated at $lat ")
                val lon = cursor.getDouble(cursor.getColumnIndexOrThrow("lon")) // get lon
                Log.d(TAG, "---> 2 Marker updated at $lon ")
                val rad = cursor.getDouble(cursor.getColumnIndexOrThrow("rad")) // get rad
                Log.d(TAG, "---> 2 Marker updated at $rad ")
                locations.add(Triple(lat, lon, rad)) //add results
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return locations // return location list
    }



    // Get messages by user name
    @SuppressLint("Range")
    fun getMsgByName(name: String): List<String> {
        val db = dbHelper.readableDatabase

        val cursor: Cursor = db.rawQuery("SELECT message FROM messages WHERE user_id IN (SELECT id FROM users WHERE name = ?)", arrayOf(name))

        val messages = mutableListOf<String>()
        while (cursor.moveToNext()) {
            // Add each message to the list
            messages.add(cursor.getString(cursor.getColumnIndex("message")))
        }
        cursor.close() // Close the cursor

        return messages // Return the list of messages
    }

    // Retrieve all users from the database
    fun getAllUsers(): Cursor {
        // Get a readable database instance
        val db = dbHelper.readableDatabase
        // Query the 'users' table and return a Cursor to iterate over the results
        return db.query("users", null, null, null, null, null, null)
    }

    // Validate user credentials and set authentication status
    fun validateUser(username: String, password: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            "users",
            arrayOf("name"),
            "name = ? AND password = ?",
            arrayOf(username, password),
            null,
            null,
            null
        )

        val isValid = cursor.count > 0
        Log.d("UserValidation", "Username: $username, Password: $password, IsValid: $isValid")
        cursor.close()
        return isValid
    }

    // Check if the user is authenticated
    fun isUserAuthenticated(): Boolean {
        return sharedPreferences.getBoolean("is_authenticated", false)
    }

    // Set user authentication status
    fun setUserAuthenticated(isAuthenticated: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_authenticated", isAuthenticated)
        editor.apply()
    }

    // Retrieve the logged-in user's name
    fun getLoggedInUserName(): String? {
        return sharedPreferences.getString("logged_in_username", null)
    }

    /**
     * Checks if the specified user is a manager by querying the database.
     */
     fun checkUserAdmin(db: SQLiteDatabase, userName: String): Boolean {
        val cursor = db.rawQuery("SELECT * FROM users WHERE name = ? AND role = 'admin'", arrayOf(userName))
        val isAdmin = cursor.count > 0 // If the cursor has any results, the user is an admin
        cursor.close()
        return isAdmin
    }



    // Store the logged-in user's name
    fun setLoggedInUserName(username: String) {
        val editor = sharedPreferences.edit()
        editor.putString("logged_in_username", username)
        editor.apply()
    }
    //clear all clearSharedPreferences, used in logout
    fun clearSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.clear() // clear data
        editor.apply() // Confirm
    }

//    fun addDayOffRequest(employeeName: String, requestDate: String) {
//        val db = dbHelper.writableDatabase
//        val values = ContentValues().apply {
//            put("user_name", employeeName)
//            put("requested_date", requestDate)
//            put("status", "PENDING") //Request initial state is pending
//        }
//        db.insert("requests", null, values)
//        db.close()
//    }

//    fun approveDayOffRequest(requestId: Int, userName: String) {
//        val db = dbHelper.writableDatabase
//        val values = ContentValues().apply {
//            put("status", "APPROVED")
//        }
//        db.update("requests", values, "id = ?", arrayOf(requestId.toString()))
//        db.close()
//
//        // Enviar notificação para o usuário logado
//        val userId = getUserIdByName(userName)
//        if (userId != null) {
//            val message = "Seu pedido de folga foi aprovado!"
//            sendMessage(userId, message)
//        }
//    }





//    @SuppressLint("Range")
//    fun getPendingDayOffRequests(): List<DayOffRequest> {
//        val db = dbHelper.readableDatabase
//        val cursor: Cursor = db.rawQuery("SELECT * FROM requests WHERE status = 'PENDING'", null)
//
//        val requests = mutableListOf<DayOffRequest>()
//        while (cursor.moveToNext()) {
//            val id = cursor.getInt(cursor.getColumnIndex("id"))
//            val employeeName = cursor.getString(cursor.getColumnIndex("employee_name"))
//            val requestDate = cursor.getString(cursor.getColumnIndex("request_date"))
//            val status = cursor.getString(cursor.getColumnIndex("status"))
//
//            requests.add(DayOffRequest(id, employeeName, requestDate, status))
//        }
//        cursor.close()
//        return requests
//    }

}

//data class DayOffRequest(
//    val id: Int,
//    val employeeName: String,
//    val requestDate: String,
//    val status: String
//)
