package com.griffith.user5

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MessagesActivity : BaseActivity() {

    private lateinit var userRepository: UserRepository
    private lateinit var messagesListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        //setupDrawer  for the navagationview
        setupDrawer(
            toolbarId = R.id.toolbar,
            drawerLayoutId = R.id.drawer_layout,
            navigationViewId = R.id.navigation_view
        )

        // Initialize user repository
        userRepository = UserRepository(this)

        // connect ListView
        messagesListView = findViewById(R.id.messagesListView)

        // get user logged
        val loggedInUserName = userRepository.getLoggedInUserName() ?: ""

        // search messages for user logged
        val messages = userRepository.getMsgByName(loggedInUserName)

        if (messages.isNotEmpty()) {
            // ArrayAdapter to show messages in ListView
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messages)
            messagesListView.adapter = adapter
        } else {
            Toast.makeText(this, "No Messages found for $loggedInUserName", Toast.LENGTH_SHORT).show()
        }
    }
}
