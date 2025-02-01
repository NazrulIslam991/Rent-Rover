package com.example.rent_rover

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class Reset_Password_activity : AppCompatActivity() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var resetPasswordButton: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var backIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        // Set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Initialize views
        emailEditText = findViewById(R.id.email)
        resetPasswordButton = findViewById(R.id.reset_password_button)
        backIcon = findViewById(R.id.back_icon)

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()


        // Initialize LoadingDialog
        loadingDialog = LoadingDialog(this)

        // Set click listener for reset password button
        resetPasswordButton.setOnClickListener {
            sendPasswordResetLink()
        }

        //back button click
        backIcon.setOnClickListener {
            finish()
        }
    }

    private fun sendPasswordResetLink() {
        val email = emailEditText.text.toString().trim()

        if (!validateUserEmail(email)) {
            return
        }


        // Show loading dialog
        loadingDialog.show()

        // Send reset password email
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                // Hide loading dialog
                loadingDialog.dismiss()

                if (task.isSuccessful) {
                    Toast.makeText(this, "Reset password link has been sent to your email.", Toast.LENGTH_LONG).show()
//                    val intent = Intent(this, Login_Activity::class.java)
//                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to send reset email. Please check the email address and try again.", Toast.LENGTH_LONG).show()
                }
            }
    }


    // Email validation
    private fun validateUserEmail(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9._%+-]+@(gmail\\.com|outlook\\.com|yahoo\\.com|lus\\.ac\\.bd)$"
        if (email.isEmpty()) {
            emailEditText.error = "Email cannot be empty"
            emailEditText.requestFocus()
            return false
        }

        // Check if the email format is valid (gmail.com, outlook.com, yahoo.com, cse_[16 digits]@lus.ac.bd)
        else if (!email.matches(emailRegex.toRegex()) && !email.matches("^cse_[0-9]{16}@lus\\.ac\\.bd$".toRegex())) {
            emailEditText.error = "Invalid email format"
            emailEditText.requestFocus()
            return false
        }
        else{
            emailEditText.error = null
            return true
        }

        return true
    }
}
