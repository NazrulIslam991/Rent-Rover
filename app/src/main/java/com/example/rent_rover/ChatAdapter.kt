package com.example.rent_rover

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(
    private val mContext: Context,
    private val mChatList: MutableList<ChatModelClass>
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val senderID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == 1) {
            val view = LayoutInflater.from(mContext)
                .inflate(R.layout.message_item_right, parent, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater.from(mContext)
                .inflate(R.layout.message_item_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = mChatList[position]
        holder.showMessage.text = chat.getMessage()
        holder.timeTextView.text = chat.getTime()
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mChatList[position].getSender() == senderID) 1 else 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val showMessage: TextView = itemView.findViewById(R.id.show_message)
        val timeTextView: TextView = itemView.findViewById(R.id.time_tv)
    }
}
