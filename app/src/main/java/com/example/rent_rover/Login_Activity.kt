package com.example.rent_rover

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText

class Login_Activity : AppCompatActivity() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var rememberMeCheckBox: CheckBox
    private lateinit var forgotPasswordText: TextView
    private lateinit var createAccountText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Set status bar color
        window.statusBarColor = ContextCompat.getColor(this@Login_Activity, R.color.white)

        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR


        // Initialize views
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.login_button)
        rememberMeCheckBox = findViewById(R.id.login_password_show_hide)
        forgotPasswordText = findViewById(R.id.forgot_password)
        createAccountText = findViewById(R.id.create_account)



        // Set click listeners
        loginButton.setOnClickListener {
            loginUser()
        }



        forgotPasswordText.setOnClickListener {
            // Handle forgot password logic (perhaps open a dialog or another activity)
            Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show()
        }




        createAccountText.setOnClickListener {
            // Navigate to the registration activity
            val intent = Intent(this, SignUp_Activity::class.java)
            startActivity(intent)
        }



        // Show/Hide Password functionality
        rememberMeCheckBox.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this, "Remember me ", Toast.LENGTH_SHORT).show()

        }
    }


    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (!validateUserEmail(email)) {
            return
        }

        else if (!validateUserPassword(password)) {
            return
        }
        else{
            // If all validations pass, show success message
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

            // Navigate to next activity after login
//           val intent = Intent(this, HomeActivity::class.java)  // Replace with actual next activity
//           startActivity(intent)
//          finish() // Close login activity to prevent back navigation
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

    // Password validation
    private fun validateUserPassword(password: String): Boolean {
        if (password.isEmpty()) {
            passwordEditText.error = "Password cannot be empty"
            passwordEditText.requestFocus()
            return false
        }
        else{
            passwordEditText.error=null
            return true
        }
        return true
    }
}
