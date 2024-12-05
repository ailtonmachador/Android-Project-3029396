//package com.griffith.user5
//
//import android.annotation.SuppressLint
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//
//class ContactAdapter(
//    private var contacts: MutableList<Contact>,
//    private val onRemoveClick: (Contact) -> Unit
//) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {
//
//    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val nameTextView: TextView = view.findViewById(R.id.textViewName)
//        val removeButton: Button = view.findViewById(R.id.buttonRemove)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_contact, parent, false)
//        return ContactViewHolder(view)
//    }
//
//    @SuppressLint("SetTextI18n")
//    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
//        val contact = contacts[position]
//        holder.nameTextView.text = "${contact.name} (${contact.phoneNumber})"
//        holder.removeButton.setOnClickListener {
//            onRemoveClick(contact)
//        }
//    }
//
//    override fun getItemCount(): Int = contacts.size
//
//    fun removeContact(contact: Contact) {
//        contacts.remove(contact)
//        notifyDataSetChanged()
//    }
//}
