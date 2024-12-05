package com.griffith.user5

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
/**
 * RequestActivity manages the approval and rejection of day-off requests.
 * It retrieves requests from the database, displays them in a RecyclerView,
 * and handles user interactions for approval or rejection.
 */

class RequestActivity : BaseActivity()  {

    // Declare necessary components for database and UI interactions
    private lateinit var databaseHelper: UserDatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RequestAdapter
    private lateinit var requests: MutableList<Request>
    private lateinit var userRepository: UserRepository

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_request)

        // Initialize repository, database helper, and RecyclerView
        userRepository = UserRepository(this)
        databaseHelper = UserDatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerView)

        // Load all requests from the database
        requests = databaseHelper.getAllRequests().toMutableList()

        // Set up the RecyclerView adapter with click listeners for approval/rejection
        adapter = RequestAdapter(
            requests,
            onApproved = { request ->
                val userId = userRepository.getUserIdByName(request.name)
                val dayRequest = request.requestDay

                adapter.notifyDataSetChanged()
                if (userId != null) {
                    // Send approval message and update the database
                    userRepository.sendMessage(
                        userId,
                        "Your request has been approved. day: $dayRequest"
                    )
                    databaseHelper.deleteRequest(request.id)
                    requests.remove(request)
                    Toast.makeText(this, "Request approved!!!!!", Toast.LENGTH_SHORT).show()
                }
            },
            onRejectClick = { request ->
                val userId = userRepository.getUserIdByName(request.name)
                val dayRequest = request.requestDay

                adapter.notifyDataSetChanged()
                if (userId != null) {
                    // Send rejection message and update the database
                    userRepository.sendMessage(
                        userId,
                        "Your request has been rejected. day: $dayRequest"
                    )
                    databaseHelper.deleteRequest(request.id)
                    requests.remove(request)
                    Toast.makeText(this, "Request rejected!!!!!", Toast.LENGTH_SHORT).show()
                }
            }
        )

        // Configure RecyclerView layout and adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Setup navigation drawer
        setupDrawer(
            toolbarId = R.id.toolbar,
            drawerLayoutId = R.id.drawer_layout,
            navigationViewId = R.id.navigation_view
        )
    }
}
