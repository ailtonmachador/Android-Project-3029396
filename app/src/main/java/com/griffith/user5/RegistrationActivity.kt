package com.griffith.user5

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.griffith.user5.R
import com.griffith.user5.UserRepository

// RegistrationActivity class that extends AppCompatActivity
class RegistrationActivity : AppCompatActivity() {

    // Declare variables for UI components and user repository
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var messageTextView: TextView
    private lateinit var userRepository: UserRepository // Declare the userRepository variable

    // Override onCreate method to set up the activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content view to the registration activity layout
        setContentView(R.layout.activity_registration)

        // Initialize the UI components by finding them by their IDs
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        registerButton = findViewById(R.id.registerButton)
        messageTextView = findViewById(R.id.messageTextView)

        // Initialize the UserRepository here, before any usage
        userRepository = UserRepository(this)

        // Set an OnClickListener for the registerButton
        registerButton.setOnClickListener {
            // Get the text from the username, password, and confirm password EditTexts
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // Validate the registration input
            if (validateRegistration(username, password, confirmPassword)) {
                // Call the method to register the user
                registerUser(username, password)
                // Send the user to the login activity after registration
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // Finish the current activity
            } else {
                // Display an error message if validation fails
                messageTextView.text = "Registration failed. Please check your input."
            }
        }
    }

    // Function to register the user
    private fun registerUser(username: String, password: String) {
        // Add the user to the repository
        userRepository.addUser(username, password)
        // Display a success message
        messageTextView.text = "Registration successful!"
    }

    // Function to validate user input for registration
    private fun validateRegistration(username: String, password: String, confirmPassword: String): Boolean {
        // Check if username and password are not blank and if they match
        return username.isNotBlank() &&
                password.isNotBlank() &&
                password == confirmPassword
    }
}
