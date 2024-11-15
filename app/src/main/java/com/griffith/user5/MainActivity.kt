package com.griffith.user5

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Declare variables for the user repository and UI elements
    private lateinit var userRepository: UserRepository
    private lateinit var logout: Button
    private lateinit var request_day_off: Button
    private lateinit var notification: Button

    // Called when the activity is starting
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the user repository for database operations
        userRepository = UserRepository(this)

        // Retrieve the logged-in user's name (if needed for future use)
        val userLogin = userRepository.getLoggedInUserName()

        // Check if the user is authenticated; if not, redirect to LoginActivity
        if (!userRepository.isUserAuthenticated()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent) // Start the LoginActivity
            finish() // End the current MainActivity
            return // Exit onCreate if the user is not authenticated
        }

        // Set the layout for this activity to activity_main.xml
        setContentView(R.layout.activity_main)

        // Link UI components with their respective IDs in activity_main.xml
        logout = findViewById(R.id.logout)
        notification = findViewById(R.id.notification)
        request_day_off = findViewById(R.id.request_day_offBt)

        // Handle the notification button click to open NotificationActivity
        notification.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent) // Start the NotificationActivity
            finish() // End the current MainActivity
        }

        // Handle the notification button click to open RequestActivity
        request_day_off.setOnClickListener {
            val intent = Intent(this, RequestActivity::class.java)
            startActivity(intent) // Start the NotificationActivity
            finish() // End the current MainActivity
        }

        logout.setOnClickListener {
            // clean repository for logout
            userRepository.setLoggedInUserName("")
            userRepository.clearSharedPreferences() //clear all shared data
            // redirect user to loggin screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            //close currently activity not alloweing user to go back by pressing "back"
            finish()
        }
    }
}
