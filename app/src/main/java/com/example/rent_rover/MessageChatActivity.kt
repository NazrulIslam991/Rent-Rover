package com.example.rent_rover
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class MessageChatActivity : AppCompatActivity() {

    private lateinit var textSend: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var receiverName: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var backButton: ImageView
    private lateinit var chatMenu: ImageView
    private var chatList = mutableListOf<ChatModelClass>()
    private var senderID: String? = null
    private var receiverID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        receiverName = findViewById(R.id.username)
        textSend = findViewById(R.id.text_send)
        btnSend = findViewById(R.id.btn_send)
        recyclerView = findViewById(R.id.recycler_view)
        backButton = findViewById(R.id.back)
        chatMenu = findViewById(R.id.chat_menu)

        recyclerView.layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatAdapter(this, chatList)
        recyclerView.adapter = chatAdapter

        // Set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        senderID = FirebaseAuth.getInstance().currentUser?.uid
        receiverID = intent.getStringExtra("USER_ID")

        if (receiverID == null || senderID == null) {
            Toast.makeText(this, "Error: User IDs are missing!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Fetch receiver's name
        val reference = FirebaseDatabase.getInstance().reference.child("Users").child(receiverID!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                if (user?.name != null && user.name.isNotEmpty()) {
                    receiverName.text = user.name
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MessageChatActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })


        btnSend.setOnClickListener {
            val message = textSend.text.toString().trim()
            if (message.isEmpty()) {
                Toast.makeText(this, "Please write a message first...", Toast.LENGTH_SHORT).show()
            } else {
                sendMessageToUser(senderID!!, receiverID!!, message)
            }
        }

        backButton.setOnClickListener {
            finish() // Finish activity when back button is clicked
        }

        chatMenu.setOnClickListener { showPopupMenu(it) } // Show popup menu on click

        readMessages(senderID!!, receiverID!!)
    }

    private fun sendMessageToUser(senderID: String, receiverID: String, message: String) {

        if (senderID == receiverID) {
            Toast.makeText(this, "You cannot send messages to yourself!", Toast.LENGTH_SHORT).show()
            return
        }
        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.child("Chats").push().key ?: return

        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

        val messageHashMap = hashMapOf(
            "sender" to senderID,
            "message" to message,
            "receiver" to receiverID,
            "isSeen" to false,
            "messageId" to messageKey,
            "time" to currentTime
        )

        reference.child("Chats").child(messageKey).setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val chatListReference = FirebaseDatabase.getInstance().reference.child("ChatList")
                    chatListReference.child(senderID).child(receiverID).child("id").setValue(receiverID)
                    chatListReference.child(receiverID).child(senderID).child("id").setValue(senderID)
                    textSend.text.clear()
                } else {
                    Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun readMessages(senderID: String, receiverID: String) {
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (data in snapshot.children) {
                    val chat = data.getValue(ChatModelClass::class.java)
                    if (chat != null && ((chat.sender == senderID && chat.receiver == receiverID) || (chat.sender == receiverID && chat.receiver == senderID))) {
                        chatList.add(chat)
                    }
                }
                chatAdapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(chatList.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MessageChatActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.chat_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.clear_chat -> {
                    clearChat()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun clearChat() {
        if (senderID == null || receiverID == null) return

        val chatReference = FirebaseDatabase.getInstance().reference.child("Chats")
        chatReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val chat = data.getValue(ChatModelClass::class.java)
                    if (chat != null && ((chat.sender == senderID && chat.receiver == receiverID) || (chat.sender == receiverID && chat.receiver == senderID))) {
                        data.ref.removeValue()
                    }
                }
                chatList.clear()
                chatAdapter.notifyDataSetChanged()
                Toast.makeText(this@MessageChatActivity, "Chat cleared successfully", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MessageChatActivity, "Failed to clear chat", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

