package com.example.rent_rover

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mancj.materialsearchbar.MaterialSearchBar

class FavoritesFragment : Fragment() {

    private lateinit var rentCircularList: MutableList<RentCircular>
    private lateinit var filteredList: MutableList<RentCircular>
    private lateinit var rentCircularAdapter: FavoritesRentC_Adapter
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var noResultsText: TextView
    private lateinit var searchBar: MaterialSearchBar
    private val database = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView and LoadingDialog
        rentCircularList = mutableListOf()
        filteredList = mutableListOf()

        // Pass the onDeleteClick lambda function to the adapter
        rentCircularAdapter = FavoritesRentC_Adapter(filteredList) { rentCircularKey ->
            removeFromFavorites(rentCircularKey)
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = rentCircularAdapter

        loadingDialog = LoadingDialog(requireContext())
        noResultsText = view.findViewById(R.id.noResultsText)
        searchBar = view.findViewById(R.id.searchBar)  // Initialize searchBar

        // Set up the search bar listener
        setupSearchBar()

        // Fetch the user's favorite rent circulars
        fetchUserFavorites()
    }

    private fun setupSearchBar() {
        searchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {
                if (!enabled) {
                    filteredList.clear()
                    filteredList.addAll(rentCircularList)  // Show all rent circulars when search is canceled
                    rentCircularAdapter.notifyDataSetChanged()
                    noResultsText.visibility = View.GONE
                }
            }

            override fun onSearchConfirmed(text: CharSequence) {
                filterFavorites(text.toString().trim())  // Filter the results based on search query
            }

            override fun onButtonClicked(buttonCode: Int) {}
        })
    }

    private fun fetchUserFavorites() {
        try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val databaseReference = FirebaseDatabase.getInstance().getReference("Favorites")
                val userFavoritesRef = databaseReference.child(currentUser.uid)

                // Fetch the favorite keys for the user
                userFavoritesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val favoriteRentCircularKeys = mutableListOf<String>()
                        for (child in snapshot.children) {
                            val rentCircularKey = child.key
                            if (rentCircularKey != null) {
                                favoriteRentCircularKeys.add(rentCircularKey)
                            }
                        }
                        // Once we have the favorite keys, fetch the corresponding Rent_Circular data
                        fetchRentCircularData(favoriteRentCircularKeys)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(), "Failed to load favorites", Toast.LENGTH_SHORT).show()
                        loadingDialog.dismiss()
                    }
                })
            }
        } catch (e: Exception) {
            // Handle exceptions if any error occurs during fetching user favorites
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            loadingDialog.dismiss()
        }
    }

    private fun fetchRentCircularData(favoriteRentCircularKeys: List<String>) {
        try {
            if (favoriteRentCircularKeys.isEmpty()) {
                noResultsText.visibility = View.VISIBLE
                loadingDialog.dismiss()
                return
            }

            val rentCircularRef = FirebaseDatabase.getInstance().getReference("Rent_Circular")
            for (key in favoriteRentCircularKeys) {
                rentCircularRef.child(key).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val rentCircular = snapshot.getValue(RentCircular::class.java)
                        if (rentCircular != null) {
                            rentCircular.key = snapshot.key
                            rentCircularList.add(rentCircular)
                        }
                        // Show or hide "No results" message based on availability of data
                        filteredList.clear()
                        filteredList.addAll(rentCircularList)
                        rentCircularAdapter.notifyDataSetChanged()
                        noResultsText.visibility = if (rentCircularList.isEmpty()) View.VISIBLE else View.GONE
                        loadingDialog.dismiss()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(), "Failed to load Rent Circulars", Toast.LENGTH_SHORT).show()
                        loadingDialog.dismiss()
                    }
                })
            }
        } catch (e: Exception) {
            // Handle exceptions if any error occurs during fetching Rent Circular data
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            loadingDialog.dismiss()
        }
    }

    private fun filterFavorites(query: String) {
        filteredList.clear()
        val lowerCaseQuery = query.lowercase()

        // Filter the list based on the query matching property type or address
        for (item in rentCircularList) {
            val propertyTypeMatch = item.propertyType.lowercase().contains(lowerCaseQuery)
            val addressMatch = item.address.lowercase().contains(lowerCaseQuery)

            if (propertyTypeMatch || addressMatch) {
                filteredList.add(item)
            }
        }

        // Show or hide the "No results" text based on the search result
        noResultsText.visibility = if (filteredList.isEmpty()) View.VISIBLE else View.GONE
        rentCircularAdapter.notifyDataSetChanged()
    }

    // Method to remove item from favorites
    private fun removeFromFavorites(rentCircularKey: String) {
        try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val databaseReference = FirebaseDatabase.getInstance().getReference("Favorites")
                val userFavoritesRef = databaseReference.child(currentUser.uid)

                // Remove the rent circular from the user's favorites in Firebase
                userFavoritesRef.child(rentCircularKey).removeValue()
                    .addOnSuccessListener {
                        // Remove the item from the list and notify the adapter
                        rentCircularList.removeAll { it.key == rentCircularKey }
                        filteredList.removeAll { it.key == rentCircularKey }
                        rentCircularAdapter.notifyDataSetChanged()

                        // Check if the list is empty, and show the "No results" text
                        if (rentCircularList.isEmpty()) {
                            noResultsText.visibility = View.VISIBLE
                        }

                        Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(requireContext(), "Failed to remove from favorites: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        } catch (e: Exception) {
            // Catch any other exceptions that may occur
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
