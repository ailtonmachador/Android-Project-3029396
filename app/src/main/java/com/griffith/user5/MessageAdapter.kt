//package com.griffith.user5
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//
//class MessageAdapter(
//    private val messages: MutableList<Message>,
//    private val onRemoveClick: (Message) -> Unit
//) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
//
//    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val messageTextView: TextView = view.findViewById(R.id.textViewName)
//        val removeButton: Button = view.findViewById(R.id.buttonRemove)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_contact, parent, false) // Reaproveitando o layout
//        return MessageViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
//        val message = messages[position]
////        holder.messageTextView.text = "User: ${message.userId} - ${message.content}"
////        holder.removeButton.setOnClickListener {
////            onRemoveClick(message)
////        }
//    }
//
//    override fun getItemCount(): Int = messages.size
//
//    fun removeMessage(message: Message) {
//        messages.remove(message)
//        notifyDataSetChanged()
//    }
//}
