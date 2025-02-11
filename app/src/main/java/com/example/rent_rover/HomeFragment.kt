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
import com.mancj.materialsearchbar.MaterialSearchBar
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

class HomeFragment : Fragment() {

    private lateinit var rentCircularList: MutableList<RentCircular>
    private lateinit var filteredList: MutableList<RentCircular>
    private lateinit var rentCircularAdapter: RentCircularAdapter
    private lateinit var filterIcon: ImageView
    private lateinit var searchBar: MaterialSearchBar
    private val database = FirebaseDatabase.getInstance()
    private lateinit var noResultsText: TextView
    private val rentCircularRef = database.getReference("Rent_Circular")
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        rentCircularList = mutableListOf()
        filteredList = mutableListOf()
        rentCircularAdapter = RentCircularAdapter(filteredList)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = rentCircularAdapter

        // Initialize loading dialog
        loadingDialog = LoadingDialog(requireContext())

        // Set status bar color
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        requireActivity().window.decorView.systemUiVisibility = requireActivity().window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        noResultsText = view.findViewById(R.id.noResultsText)


        // Initialize filter icon and set click listener
        filterIcon = view.findViewById(R.id.filterIcon)
        filterIcon.setOnClickListener {
            val intent = Intent(requireContext(), Filter_form::class.java)
            startActivity(intent)
        }

        // Initialize search bar and set listener
        searchBar = view.findViewById(R.id.searchBar)
        searchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {
                if (!enabled) {
                    filteredList.clear()
                    filteredList.addAll(rentCircularList)
                    rentCircularAdapter.notifyDataSetChanged()
                    noResultsText.visibility = View.GONE

                    // Hide keyboard when search is canceled
                    val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(searchBar.windowToken, 0)
                }
            }

            override fun onSearchConfirmed(text: CharSequence) {
                filterRentCircular(text.toString().trim())

                // Hide keyboard when search is confirmed (Enter key is pressed)
                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(searchBar.windowToken, 0)
            }

            override fun onButtonClicked(buttonCode: Int) {}
        })


        // Fetch data from Firebase
        fetchRentCircularData()
    }

    private fun fetchRentCircularData() {
        loadingDialog.show()

        rentCircularRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rentCircularList.clear()
                for (dataSnapshot in snapshot.children) {
                    val rentCircular = dataSnapshot.getValue(RentCircular::class.java)
                    val key = dataSnapshot.key
                    if (rentCircular != null && key != null) {
                        rentCircular.key = key
                        rentCircularList.add(rentCircular)
                    }
                }
                filteredList.clear()
                filteredList.addAll(rentCircularList)
                rentCircularAdapter.notifyDataSetChanged()
                loadingDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
            }
        })
    }


    private fun filterRentCircular(query: String) {
        filteredList.clear()

        val lowerCaseQuery = query.lowercase()
        for (item in rentCircularList) {
            val propertyTypeMatch = item.propertyType.lowercase().contains(lowerCaseQuery)
            val addressMatch = item.address.lowercase().contains(lowerCaseQuery)

            val monthlyRentValue = item.monthlyRent.toIntOrNull() ?: Int.MAX_VALUE
            val queryAsNumber = query.toIntOrNull()

            val rentMatch = queryAsNumber != null && monthlyRentValue in 0..queryAsNumber

            if (propertyTypeMatch || addressMatch || rentMatch) {
                filteredList.add(item)
            }
        }

        // Show or hide "No products found" message based on search results
        if (filteredList.isEmpty()) {
            noResultsText.visibility = View.VISIBLE
        } else {
            noResultsText.visibility = View.GONE
        }
        rentCircularAdapter.notifyDataSetChanged()
    }
}
