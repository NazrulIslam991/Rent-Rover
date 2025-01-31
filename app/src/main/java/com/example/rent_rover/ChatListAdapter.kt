package com.example.rent_rover

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatListAdapter(private val chatList: List<ChatListModelClass>) : RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_list_layout, parent, false)
        return ChatListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        val chat = chatList[position]

        // Set the user's profile image and name based on the user ID
        val userId = chat.getId()

        // Fetch user details using userId from Firebase
        val userReference = FirebaseDatabase.getInstance().reference.child("Users").child(userId!!)
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                holder.username.text = user?.name
                // Set profile picture
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        // Set the click listener to open the chat
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, MessageChatActivity::class.java)
            intent.putExtra("USER_ID", userId)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    class ChatListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username)
        val profileImage: ImageView = itemView.findViewById(R.id.image_profile)
        val arrow: ImageView = itemView.findViewById(R.id.arrow)
    }
}
