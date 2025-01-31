package com.example.rent_rover

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private lateinit var rentCircularList: MutableList<RentCircular>
    private lateinit var rentCircularAdapter: RentCircularAdapter
    private lateinit var filterIcon: ImageView  // Declare filter icon
    private val database = FirebaseDatabase.getInstance()
    private val rentCircularRef = database.getReference("Rent_Circular")
    private lateinit var loadingDialog: LoadingDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        rentCircularList = mutableListOf()
        rentCircularAdapter = RentCircularAdapter(rentCircularList)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = rentCircularAdapter

        // Initialize loading dialog
        loadingDialog = LoadingDialog(requireContext())

        // Set status bar color
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        requireActivity().window.decorView.systemUiVisibility =
            requireActivity().window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Initialize filter icon and set click listener
        filterIcon = view.findViewById(R.id.filterIcon)
        filterIcon.setOnClickListener {
            val intent = Intent(requireContext(), Filter_form::class.java)
            startActivity(intent)
        }

        // Fetch data from Firebase
        fetchRentCircularData()
    }

    private fun fetchRentCircularData() {
        // Show loading dialog before fetching data
        loadingDialog.show()

        rentCircularRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rentCircularList.clear()
                for (dataSnapshot in snapshot.children) {
                    val rentCircular = dataSnapshot.getValue(RentCircular::class.java)
                    if (rentCircular != null) {
                        rentCircularList.add(rentCircular)
                    }
                }
                rentCircularAdapter.notifyDataSetChanged()

                // Hide loading dialog after data is fetched
                loadingDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()

                // Hide loading dialog if data fetch fails
                loadingDialog.dismiss()
            }
        })
    }
}

