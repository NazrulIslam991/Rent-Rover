package com.example.rent_rover

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class FilterRentPost : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var rentCircularAdapter: RentCircularAdapter
    private lateinit var backButton: ImageView
    private lateinit var rentCircularList: MutableList<RentCircular>
    private val database = FirebaseDatabase.getInstance().getReference("Rent_Circular")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_rent_post)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewFiltered)
        recyclerView.layoutManager = LinearLayoutManager(this)
        rentCircularList = mutableListOf()
        rentCircularAdapter = RentCircularAdapter(rentCircularList)
        recyclerView.adapter = rentCircularAdapter

        // Get filter data from Intent
        val division = intent.getStringExtra("division") ?: ""
        val district = intent.getStringExtra("district") ?: ""
        val upazila = intent.getStringExtra("upazila") ?: ""
        val floorNo = intent.getStringExtra("floorNo") ?: ""
        val bedrooms = intent.getStringExtra("bedrooms") ?: ""
        val bathrooms = intent.getStringExtra("bathrooms") ?: ""
        val minRent = intent.getStringExtra("minRent") ?: "0"
        val maxRent = intent.getStringExtra("maxRent") ?: "999999999"  // Set a very high max value

        backButton = findViewById(R.id.exit)
        backButton.setOnClickListener {
            finish() // Finish activity when back button is clicked
        }

        // Fetch filtered data
        fetchFilteredData(division, district, upazila, floorNo, bedrooms, bathrooms, minRent, maxRent)
    }

    private fun fetchFilteredData(
        division: String, district: String, upazila: String, floorNo: String,
        bedrooms: String, bathrooms: String, minRent: String, maxRent: String
    ) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rentCircularList.clear()
                for (dataSnapshot in snapshot.children) {
                    val rentCircular = dataSnapshot.getValue(RentCircular::class.java)

                    if (rentCircular != null) {
                        // Apply filters
                        val matchesDivision = division.isEmpty() || rentCircular.division == division
                        val matchesDistrict = district.isEmpty() || rentCircular.district == district
                        val matchesUpazila = upazila.isEmpty() || rentCircular.upazila == upazila
                        val matchesFloor = floorNo.isEmpty() || rentCircular.floorNo.replace(" Floor", "") == floorNo
                        val matchesBedrooms = bedrooms.isEmpty() || rentCircular.bedrooms == bedrooms
                        val matchesBathrooms = bathrooms.isEmpty() || rentCircular.bathrooms == bathrooms

                        // Compare rent values as strings
                        val matchesRent = rentCircular.monthlyRent.toIntOrNull()?.let {
                            it >= minRent.toIntOrNull() ?: 0 && it <= maxRent.toIntOrNull() ?: Int.MAX_VALUE
                        } ?: false

                        // Apply filters without the facilities filter
                        if (matchesDivision && matchesDistrict && matchesUpazila && matchesFloor &&
                            matchesBedrooms && matchesBathrooms && matchesRent) {
                            rentCircularList.add(rentCircular)
                        }
                    }
                }

                val noResultsText = findViewById<TextView>(R.id.noResultsText)
                if (rentCircularList.isEmpty()) {
                    noResultsText.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    noResultsText.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }

                rentCircularAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FilterRentPost, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

