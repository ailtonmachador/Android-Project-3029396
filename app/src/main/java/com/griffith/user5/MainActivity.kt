package com.griffith.user5

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
/**
 * MainActivity is the entry point after user login.
 * It verifies authentication, sets up the UI and navigation drawer,
 * and provides access to features like notifications, day-off requests, and clock-in.
 */
class MainActivity : BaseActivity() {

    // Declare variables for the user repository and UI elements
    private lateinit var userRepository: UserRepository
    private lateinit var logout: Button
    private lateinit var request_day_off: Button
    private lateinit var notification: Button
    private lateinit var clockInButton: Button
    private lateinit var RequestButton: Button
    private lateinit var mapButton: Button
    private lateinit var mapTxt : TextView

    // Flag to determine if the logged-in user is a manager
    private var isManager: Boolean = false
    private val userDatabaseHelper = UserDatabaseHelper(this)



    // Called when the activity is starting
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the user repository for database operations
        userRepository = UserRepository(this)

        // Check if the logged-in user is a manager
        val loggedInUser = userRepository.getLoggedInUserName()
        isManager = loggedInUser?.let {
            userRepository.checkUserAdmin(userDatabaseHelper.readableDatabase, it)
        } ?: false

        // Check if the user is authenticated; if not, redirect to LoginActivity
        if (!userRepository.isUserAuthenticated()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent) // Start the LoginActivity
            finish() // End the current MainActivity
            return // Exit onCreate if the user is not authenticated
        }


        // Set the layout for this activity to activity_main.xml
        setContentView(R.layout.activity_main)

        //setupDrawer  for the navagationview
        setupDrawer(
            toolbarId = R.id.toolbar,
            drawerLayoutId = R.id.drawer_layout,
            navigationViewId = R.id.navigation_view
        )


        // Link UI components with their respective IDs in activity_main.xml
        logout = findViewById(R.id.logout)
        notification = findViewById(R.id.notification)
        request_day_off = findViewById(R.id.request_day_offBt)
        clockInButton =  findViewById(R.id.clockIn)
        RequestButton =  findViewById(R.id.request_day_offBt)
        mapButton =  findViewById(R.id.mapButton)
        mapTxt = findViewById(R.id.map)

        //MAP ONLY VISIBLE FOR ADM
        if (isManager) {
            mapButton.visibility = Button.VISIBLE
            mapTxt.visibility = TextView.VISIBLE
        } else {
            mapButton.visibility = Button.GONE
            mapTxt.visibility = TextView.GONE
        }

        mapButton.setOnClickListener {
            val intent = Intent(this, Map::class.java)
            startActivity(intent) // Start the NotificationActivity
            finish() // End the current MainActivity
        }

        RequestButton.setOnClickListener {
            val intent = Intent(this, SelectDayOff::class.java)
            startActivity(intent) // Start the NotificationActivity
            finish() // End the current MainActivity
        }

        // Handle the notification button click to open NotificationActivity
        notification.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent) // Start the NotificationActivity
            finish() // End the current MainActivity
        }

        // Handle the clockInButton button
        clockInButton.setOnClickListener {
            val intent = Intent(this, ClockInActivity::class.java)
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

