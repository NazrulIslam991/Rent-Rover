package com.example.rent_rover

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import androidx.core.content.ContextCompat

class PostedCircularActivityShow : AppCompatActivity() {

    private lateinit var rentCircularList: MutableList<RentCircular>
    private lateinit var rentCircularAdapter: PostedCircularShowAdapter
    private val database = FirebaseDatabase.getInstance()
    private val rentCircularRef = database.getReference("Rent_Circular")
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var sessionManager: SessionManager
    private lateinit var backIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posted_circular_show)

        rentCircularList = mutableListOf()
        rentCircularAdapter = PostedCircularShowAdapter(rentCircularList)
        sessionManager = SessionManager(this)

        // Set up RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = rentCircularAdapter

        // Initialize loading dialog
        loadingDialog = LoadingDialog(this)

        // Set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        //back button click
        backIcon = findViewById(R.id.back)
        backIcon.setOnClickListener {
            finish()
        }

        // Fetch data from Firebase
        fetchRentCircularData()
    }

    private fun fetchRentCircularData() {
        loadingDialog.show()

        rentCircularRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rentCircularList.clear()

                // Get current userId from session
                val currentUserId = sessionManager.getUserDetails()[SessionManager.USER_ID] ?: ""

                for (dataSnapshot in snapshot.children) {
                    val rentCircular = dataSnapshot.getValue(RentCircular::class.java)
                    if (rentCircular != null && rentCircular.userId == currentUserId) {
                        rentCircularList.add(rentCircular)
                    }
                }
                rentCircularAdapter.notifyDataSetChanged()

                // Show or hide empty message
                val emptyMessage: TextView = findViewById(R.id.emptyMessage)
                if (rentCircularList.isEmpty()) {
                    emptyMessage.visibility = View.VISIBLE
                } else {
                    emptyMessage.visibility = View.GONE
                }

                loadingDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PostedCircularActivityShow, "Failed to load data", Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
            }
        })
    }

}
