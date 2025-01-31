package com.example.rent_rover

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MessagesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatListAdapter: ChatListAdapter
    private val chatList = mutableListOf<ChatListModelClass>()
    private var currentUserID: String? = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_messages, container, false)

        // Set status bar color
        activity?.window?.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.blue)
        activity?.window?.decorView?.systemUiVisibility = activity?.window?.decorView?.systemUiVisibility ?: 0 or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        recyclerView = view.findViewById(R.id.recycler_view_Customer_list)
        recyclerView.layoutManager = LinearLayoutManager(context)

        chatListAdapter = ChatListAdapter(chatList)
        recyclerView.adapter = chatListAdapter

        // Fetch chat list from Firebase
        fetchChatList()

        return view
    }

    private fun fetchChatList() {
        val chatListReference = FirebaseDatabase.getInstance().reference.child("ChatList").child(currentUserID!!)
        chatListReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapshot in snapshot.children) {
                    val chat = dataSnapshot.getValue(ChatListModelClass::class.java)
                    chat?.let {
                        chatList.add(it)
                    }
                }
                chatListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}