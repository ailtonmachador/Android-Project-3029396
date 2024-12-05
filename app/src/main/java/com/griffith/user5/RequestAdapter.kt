package com.griffith.user5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
/**
 * RequestAdapter is a RecyclerView adapter for displaying a list of day-off requests.
 * It provides functionality to approve or reject requests through callback functions.
 */
class RequestAdapter(
    private val requests: MutableList<Request>, // List of requests to display
    private val onApproved: (Request) -> Unit,  // Callback for approval action
    private val onRejectClick: (Request) -> Unit // Callback for rejection action
) : RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    // ViewHolder class to hold references to each item's views
    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView) // Displays requester's name
        val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)   // Displays requested day
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView) // Displays request status
        val approvedButton: Button = itemView.findViewById(R.id.approvedButton)  // Button for approval
        val rejectButton: Button = itemView.findViewById(R.id.rejectButton)      // Button for rejection
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        // Inflate the layout for each item in the RecyclerView
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_request, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        // Bind data to the ViewHolder for the current item
        val request = requests[position]
        holder.nameTextView.text = request.name // Set requester's name
        holder.dayTextView.text = request.requestDay // Set requested day
        holder.statusTextView.text = request.status // Set request status

        // Set click listener for approval action
        holder.approvedButton.setOnClickListener {
            onApproved(request) // Invoke approval callback
            val context = holder.itemView.context
            Toast.makeText(context, "Item approved", Toast.LENGTH_SHORT).show()
        }

        // Set click listener for rejection action
        holder.rejectButton.setOnClickListener {
            onRejectClick(request) // Invoke rejection callback
            val context = holder.itemView.context
            Toast.makeText(context, "Item rejected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = requests.size // Return total number of requests
}