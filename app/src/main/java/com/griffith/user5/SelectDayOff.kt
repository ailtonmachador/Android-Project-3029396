package com.griffith.user5

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * This class handles the day-off request process for users.
 * It allows regular users to submit a request for a day off and managers to check pending requests.
 */
class SelectDayOff : BaseActivity() {

    // UI components for selecting a day off and submitting the request
    private lateinit var datePicker: DatePicker
    private lateinit var btnSubmitRequest: Button

    // Database helper and repository for managing user and request data
    private val userDatabaseHelper = UserDatabaseHelper(this)
    private lateinit var userRepository: UserRepository

    // Flag to determine if the logged-in user is a manager
    private var isManager: Boolean = false

    /**
     * Initializes the activity, sets up the user repository, checks user role,
     * and configures the UI components and button click behavior.
     */
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)

        // Initialize the repository and UI components
        userRepository = UserRepository(this)
        datePicker = findViewById(R.id.datePicker)
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest)

        // Check if the logged-in user is a manager
        val loggedInUser = userRepository.getLoggedInUserName()
        isManager = loggedInUser?.let {
           userRepository.checkUserAdmin(userDatabaseHelper.readableDatabase, it)
        } ?: false


        // Set button text and functionality based on the user role
        if (isManager) {
            btnSubmitRequest.text = "Check Pending Requests"
            datePicker.visibility = DatePicker.GONE
        }

        // Define button click behavior
        btnSubmitRequest.setOnClickListener {
            if (isManager) {
                navigateToRequestActivity() // Navigate to the request management activity for managers
            } else {
                submitDayOffRequest() // Submit a day-off request for regular users
                Toast.makeText(this, "Request sent!!!!!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        // Setup navigation drawer
        setupDrawer(
            toolbarId = R.id.toolbar,
            drawerLayoutId = R.id.drawer_layout,
            navigationViewId = R.id.navigation_view
        )
    }

    /**
     * Navigates to the RequestActivity where managers can review pending day-off requests.
     */
    private fun navigateToRequestActivity() {
        val intent = Intent(this, RequestActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Submits the day-off request for the logged-in user.
     */
    private fun submitDayOffRequest() {
        val day = datePicker.dayOfMonth
        val month = datePicker.month
        val year = datePicker.year
        val date = "$day/${month + 1}/$year" // Format the selected date

        // Get the logged-in user's name and save the request to the database
        userRepository.getLoggedInUserName()?.let { userName ->
            saveRequestToDatabase(userName, date)
        }
    }

    /**
     * Saves the day-off request to the database with the user's name and the selected date.
     * The request status is set to "Pending".
     */
    private fun saveRequestToDatabase(userName: String, date: String) {
        val db = userDatabaseHelper.writableDatabase
        val sql = "INSERT INTO requests (user_name, requested_date, status) VALUES (?, ?, 'Pending')"
        db.execSQL(sql, arrayOf(userName, date))
    }

}
