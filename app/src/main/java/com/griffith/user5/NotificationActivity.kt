package com.griffith.user5

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NotificationActivity : AppCompatActivity() {

    // Declare variables for user repository and UI components
    private lateinit var userRepository: UserRepository
    private lateinit var sendButton: Button
    private lateinit var messageEditText: EditText
    private lateinit var userSpinner: Spinner // Spinner to display the list of users
    private lateinit var checkMsgButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the user repository for database operations
        userRepository = UserRepository(this)

        // Set the content view to the notification layout
        setContentView(R.layout.notification)

        // Link UI components with their IDs in the layout
        messageEditText = findViewById(R.id.messageEditText)
        userSpinner = findViewById(R.id.userSpinner)
        sendButton = findViewById(R.id.sendButton)
        checkMsgButton = findViewById(R.id.checkMsgButton)

        // Handle the send button click to send a message to the selected user
        sendButton.setOnClickListener {
            val selectedUserName = userSpinner.selectedItem.toString() // Get selected user from spinner
            val message = messageEditText.text.toString() // Get message input

            // Retrieve the user ID based on the selected user name
            val userId = userRepository.getUserIdByName(selectedUserName)

            if (userId != null) {
                userRepository.sendMessage(userId, message) // Send the message to the user
                Log.d("NotificationActivity", "User ID: $userId, Message: $message")
                Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show() // Show confirmation
            } else {
                Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show() // Show error if user not found
            }
        }

        checkMsgButton.setOnClickListener {
            val intent = Intent(this, MessagesActivity::class.java)
            startActivity(intent)
        }

        // Retrieve messages for the logged-in user and display them
        val loggedInUserName = userRepository.getLoggedInUserName() ?: ""
        val messages = userRepository.getMsgByName(loggedInUserName)

        if (messages.isNotEmpty()) {
            val messageString = messages.joinToString("\n") // Convert message list to a single string
            Log.d("NotificationActivity", "Messages retrieved: $messages")
            Toast.makeText(this, messageString, Toast.LENGTH_SHORT).show() // Display messages
        } else {
            Toast.makeText(this, "No messages found for $loggedInUserName", Toast.LENGTH_SHORT).show()
        }

        // Populate the user spinner with user names
        displayUsers()
    }

    // Suppress lint warnings for using column names directly in the following method
    @SuppressLint("Range")
    private fun displayUsers() {
        // Retrieve all users from the database as a Cursor object
        val cursor: Cursor = userRepository.getAllUsers()
        val userList = mutableListOf<String>() // List to store user names

        // Iterate through the Cursor to retrieve and add user names to the list
        while (cursor.moveToNext()) {
            userList.add(cursor.getString(cursor.getColumnIndex("name"))) // Add each user name to list
        }

        cursor.close() // Close the cursor to free up resources

        // Set up an ArrayAdapter to display user names in the spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, userList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userSpinner.adapter = adapter // Attach the adapter to the spinner
    }
}
