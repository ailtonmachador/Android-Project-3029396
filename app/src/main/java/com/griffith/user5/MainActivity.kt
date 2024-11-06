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

class MainActivity : AppCompatActivity() {
    // Declare variables for the user repository and UI elements
    private lateinit var userRepository: UserRepository
    private lateinit var nameEditText: EditText
    private lateinit var addButton: Button
    private lateinit var userSpinner: Spinner // Spinner to display the list of users
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var checkMsgButton: Button

    // Called when the activity is starting
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        // Initialize the user repository for database operations
        userRepository = UserRepository(this)
        val userLogin = userRepository.getLoggedInUserName() // Assuming this method retrieves the logged-in user's name

        // Check if the user is authenticated; if not, redirect to LoginActivity
        if (!userRepository.isUserAuthenticated()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent) // Start the LoginActivity
            finish() // Finish the current activity
            return // Exit the method if the user is not authenticated
        }

        // Set the content view to the activity_main layout
        setContentView(R.layout.activity_main)

        // Link the UI components with their corresponding IDs
        nameEditText = findViewById(R.id.nameEditText)
        addButton = findViewById(R.id.addButton)
        userSpinner = findViewById(R.id.userSpinner) // Initialize the spinner for user selection
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        checkMsgButton = findViewById(R.id.checkMsgButton)

        // Send message to user button
        sendButton.setOnClickListener {
            // Get the selected user from the spinner
            val selectedUserName = userSpinner.selectedItem.toString()
            val message = messageEditText.text.toString()

            // Retrieve the user ID based on the selected user name
            val userId = userRepository.getUserIdByName(selectedUserName)

            if (userId != null) {
                userRepository.sendMessage(userId, message) // Send the message
                Log.d("MainActivity", "User ID: $userId, Message: $message")


                Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show()
            }
        }

        checkMsgButton.setOnClickListener {
            // Get the logged-in user's name
            val loggedInUserName = userRepository.getLoggedInUserName()

            // Retrieve messages for the logged-in user
            val messages = userRepository.getMsgByName((loggedInUserName ?: "").toString())

            if (messages.isNotEmpty()) {
                // If messages is a list, join them into a single string
                val messageString = messages.joinToString("\n") // Assuming messages is a List<String>
               // Log.d("MainActivity", "Logged in user: $loggedInUserName")
                Log.d("MainActivity", "Messages retrieved: $messages")
                // Display messages
                Toast.makeText(this, messageString, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No messages found for $loggedInUserName", Toast.LENGTH_SHORT).show()
            }
        }

        // Add button to add a user
        addButton.setOnClickListener {
            // Get the name input from the EditText
            val name = nameEditText.text.toString()
            val password = "adadsa" // Hardcoded password for user addition (to be removed or changed)
            userRepository.addUser(name, password) // Add the user to the database
            displayUsers() // Refresh the spinner to show the updated user list
        }

        // Load the users from the database and display them in the spinner on activity creation
        displayUsers()
    }

    // Suppress lint warnings for range usage in the following method
    @SuppressLint("Range")
    private fun displayUsers() {
        // Retrieve all users from the database as a Cursor object
        val cursor: Cursor = userRepository.getAllUsers()
        val userList = mutableListOf<String>() // Create a mutable list to hold user names

        // Iterate through the Cursor to retrieve user names
        while (cursor.moveToNext()) {
            userList.add(cursor.getString(cursor.getColumnIndex("name"))) // Add each user name to the list
        }

        cursor.close() // Close the cursor to free resources

        // Set up an ArrayAdapter to display the user names in the spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, userList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userSpinner.adapter = adapter // Attach the adapter to the spinner
    }
}
