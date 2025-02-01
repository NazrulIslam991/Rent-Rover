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

class MenuFragment : Fragment() {
    private lateinit var lo_logout: RelativeLayout
    private lateinit var lo_changePassword: RelativeLayout

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


        // Initialize session manager
        val sessionManager = SessionManager(requireContext())
        val userDetails = sessionManager.getUserDetails()


        // Set user details in TextViews
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val tvEmail: TextView = view.findViewById(R.id.tv_email)

        tvName.text = userDetails[SessionManager.USER_NAME] ?: "Guest"
        tvEmail.text = userDetails[SessionManager.USER_EMAIL] ?: "guest@example.com"



        // Logout button click listener
        lo_logout = view.findViewById(R.id.lo_logout)
        lo_logout.setOnClickListener {
            showLogoutDialog()
        }



        // reset password button click listener
        lo_changePassword = view.findViewById(R.id.lo_changePassword)
        lo_changePassword.setOnClickListener{
            val intent = Intent(requireContext(), Reset_Password_activity::class.java)
            startActivity(intent)
        }
    }


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



    private fun logoutUser() {
        val sessionManager = SessionManager(requireContext())
        sessionManager.logoutUser()

        // Navigate to login screen
        val intent = Intent(requireContext(), Login_Activity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
