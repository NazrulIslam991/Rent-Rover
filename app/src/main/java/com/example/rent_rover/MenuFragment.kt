package com.example.rent_rover

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MenuFragment : Fragment() {

    private lateinit var lo_logout: RelativeLayout
    private lateinit var lo_changePassword: RelativeLayout
    private lateinit var lo_postedCircular: RelativeLayout
    private lateinit var lo_editProfile: RelativeLayout
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var sessionManager: SessionManager

    private var mobile: String = ""
    private var address: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set status bar color
        activity?.window?.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.p_bg)
        activity?.window?.decorView?.systemUiVisibility = activity?.window?.decorView?.systemUiVisibility ?: 0 or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Initialize views
        tvName = view.findViewById(R.id.tv_name)
        tvEmail = view.findViewById(R.id.tv_email)

        // Initialize loading dialog
        loadingDialog = LoadingDialog(requireContext())

        // Initialize SessionManager
        sessionManager = SessionManager(requireContext())

        // Fetch user data from Firebase
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            val uid = firebaseUser.uid
            fetchUserData(uid)
        }



        // Logout button click listener
        lo_logout = view.findViewById(R.id.lo_logout)
        lo_logout.setOnClickListener {
            showLogoutDialog()
        }

        // Reset password button click listener
        lo_changePassword = view.findViewById(R.id.lo_changePassword)
        lo_changePassword.setOnClickListener {
            val intent = Intent(requireContext(), Reset_Password_activity::class.java)
            startActivity(intent)
        }

        // Posted circular button click listener
        lo_postedCircular = view.findViewById(R.id.lo_postedCircular)
        lo_postedCircular.setOnClickListener {
            val intent = Intent(requireContext(), PostedCircularActivityShow::class.java)
            startActivity(intent)
        }

        // Edit profile button click listener
        lo_editProfile = view.findViewById(R.id.lo_editProfile)
        lo_editProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            // Pass the user data
            intent.putExtra("name", tvName.text.toString())
            intent.putExtra("email", tvEmail.text.toString())
            intent.putExtra("mobile", mobile)  // Pass mobile
            intent.putExtra("address", address) // Pass address
            startActivity(intent)
        }
    }

    // Fetch user data from Firebase and listen for updates
    private fun fetchUserData(uid: String) {
        loadingDialog.show()

        val databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(uid)
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                loadingDialog.dismiss()

                if (snapshot.exists()) {
                    val name = snapshot.child("name").value.toString()
                    val email = snapshot.child("email").value.toString()
                    mobile = snapshot.child("mobile").value.toString() // Fetch mobile
                    address = snapshot.child("address").value.toString() // Fetch address

                    // Set user details in TextViews
                    tvName.text = name
                    tvEmail.text = email
                }
            }

            override fun onCancelled(error: DatabaseError) {
                loadingDialog.dismiss()

            }
        })
    }

    // Show logout dialog
    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            logoutUser()
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    // Logout user and navigate to login screen
    private fun logoutUser() {
        try {
            // Clear session
            sessionManager.logoutUser()

            // Firebase logout
            FirebaseAuth.getInstance().signOut()

            // Navigate to login screen
            val intent = Intent(activity?.applicationContext, Login_Activity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
