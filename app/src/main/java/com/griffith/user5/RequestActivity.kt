package com.griffith.user5

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class RequestActivity : AppCompatActivity() {

    private lateinit var datePicker: DatePicker
    private lateinit var btnSubmitRequest: Button
    private lateinit var tvRequestStatus: TextView
    private lateinit var btnApprove: Button
    private lateinit var btnReject: Button
    private lateinit var employeeNameTextView: TextView
    private lateinit var requestDateTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var userRepository: UserRepository
    private val userDatabaseHelper = UserDatabaseHelper(this)

    // Variable to store the ID of the selected request
    private var selectedRequestId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)

        // Initialize UI elements
        userRepository = UserRepository(this)
        datePicker = findViewById(R.id.datePicker)
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest)
        tvRequestStatus = findViewById(R.id.tvRequestStatus)
        btnApprove = findViewById(R.id.approveButton)
        btnReject = findViewById(R.id.rejectButton)
        employeeNameTextView = findViewById(R.id.employeeName)
        requestDateTextView = findViewById(R.id.requestDate)
        statusTextView = findViewById(R.id.status)

        // Set listener for request submission button
        btnSubmitRequest.setOnClickListener {
            val day = datePicker.dayOfMonth
            val month = datePicker.month
            val year = datePicker.year
            val date = "$day/${month + 1}/$year"

            // Get logged-in user name
            val userLogin = userRepository.getLoggedInUserName()
            if (userLogin != null) {
                saveRequestToDatabase(userLogin, date)
                // Update display with request info
                updateRequestDisplay(userLogin, date, "Pending")
            }
        }

        // Check if logged-in user is an admin
        val userLogin = userRepository.getLoggedInUserName()
        val db = userDatabaseHelper.readableDatabase
        val isManager = userLogin?.let { checkUserAdmin(db, it) } ?: false

        if (isManager) {
            // Show admin buttons
            btnApprove.visibility = View.VISIBLE
            btnReject.visibility = View.VISIBLE
            loadRequestsForAdmin() // Load pending requests for admin

            // Listener for approve button
            btnApprove.setOnClickListener {
                selectedRequestId?.let {
                    updateRequestStatus(it, "Approved")
                }
            }

            // Listener for reject button
            btnReject.setOnClickListener {
                selectedRequestId?.let {
                    updateRequestStatus(it, "Rejected")
                }
            }
        } else {
            // Hide admin buttons for non-admin users
            btnApprove.visibility = View.GONE
            btnReject.visibility = View.GONE
        }
    }

    private fun saveRequestToDatabase(userName: String, date: String) {
        // Save request to the database
        val db = userDatabaseHelper.writableDatabase
        val sql = "INSERT INTO requests (user_name, requested_date, status) VALUES (?, ?, 'Pending')"
        db.execSQL(sql, arrayOf(userName, date))
        tvRequestStatus.text = "Request sent for $date"
    }

    private fun updateRequestDisplay(userName: String, date: String, status: String) {
        // Update UI with request information
        employeeNameTextView.text = "Employee Name: $userName"
        requestDateTextView.text = "Request Date: $date"
        statusTextView.text = "Status: $status"
    }

    private fun updateRequestStatus(requestId: Int, status: String) {
        val db = userDatabaseHelper.writableDatabase
        val sql = "UPDATE requests SET status = ? WHERE id = ?"
        db.execSQL(sql, arrayOf(status, requestId))
        loadRequestsForAdmin() // Refresh request list after status update
    }

    private fun checkUserAdmin(db: SQLiteDatabase, userName: String): Boolean {
        // Check if user has an admin role
        val cursor = db.rawQuery("SELECT * FROM users WHERE name = ? AND role = 'admin'", arrayOf(userName))
        val isAdmin = cursor.count > 0
        cursor.close()
        return isAdmin
    }

    @SuppressLint("Range")
    private fun loadRequestsForAdmin() {
        val db = userDatabaseHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM requests WHERE status = 'Pending'", null)

        val requests = mutableListOf<String>()
        if (cursor.moveToFirst()) {
            do {
                val requestId = cursor.getInt(cursor.getColumnIndex("id"))
                val userName = cursor.getString(cursor.getColumnIndex("user_name"))
                val requestedDate = cursor.getString(cursor.getColumnIndex("requested_date"))
                requests.add("ID: $requestId - Employee: $userName, Date: $requestedDate")

                // Store the first request ID as selected by default
                if (selectedRequestId == null) {
                    selectedRequestId = requestId
                }
            } while (cursor.moveToNext())
        }
        cursor.close()

        // Display requests or a message if there are no pending requests
        if (requests.isNotEmpty()) {
            tvRequestStatus.text = requests.joinToString("\n")
        } else {
            tvRequestStatus.text = "No pending requests"
        }
    }
}
