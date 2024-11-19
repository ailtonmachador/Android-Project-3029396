package com.griffith.user5

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// LoginActivity class that extends AppCompatActivity
class LoginActivity : AppCompatActivity() {

    // Declare variables for UI components and user repository
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var messageTextView: TextView
    private lateinit var userRepository: UserRepository


    // Override onCreate method to set up the activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content view to the login activity layout
        setContentView(R.layout.activity_login)

        // Initialize the user repository
        userRepository = UserRepository(this)

        // Initialize UI components by finding them by their IDs
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        messageTextView = findViewById(R.id.messageTextView)

        // Set an OnClickListener for the loginButton
        loginButton.setOnClickListener {
            // Get the text from the username and password EditTexts
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Validate the user credentials using the user repository
            if (userRepository.validateUser(username, password)) {
                //save name in preference
                userRepository.setLoggedInUserName(username)

                // Optionally, set user authentication status
                userRepository.setUserAuthenticated(true)

                // If valid, set the user as authenticated
                userRepository.setUserAuthenticated(true)
                // Show a toast message indicating successful login
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                //save user name

                // Log the successful login event
                Log.d("LoginActivity", "Login successful, navigating to MainActivity")

                // Start MainActivity and finish the current LoginActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // If invalid, show a toast message indicating failure
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }

        // Set an OnClickListener for the register button to navigate to RegistrationActivity
        findViewById<Button>(R.id.registerButton).setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }
}
