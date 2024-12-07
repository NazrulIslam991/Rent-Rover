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

class SignUp_Activity : AppCompatActivity() {

    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var signUpButton: Button
    private lateinit var rememberMeCheckBox: CheckBox
    private lateinit var loginText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Set status bar color
        window.statusBarColor = ContextCompat.getColor(this@SignUp_Activity, R.color.white)

        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Initialize views
        nameEditText = findViewById(R.id.name)
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        confirmPasswordEditText = findViewById(R.id.con_password)
        signUpButton = findViewById(R.id.login_button)
        rememberMeCheckBox = findViewById(R.id.login_password_show_hide)
        loginText = findViewById(R.id.create_account)

        // Set click listeners
        signUpButton.setOnClickListener {
            signUpUser()
        }

        loginText.setOnClickListener {
            // Navigate back to the login activity
            val intent = Intent(this, Login_Activity::class.java)
            startActivity(intent)
            finish()
        }

        // Show/Hide Password functionality
        rememberMeCheckBox.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signUpUser() {
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        if (!validateUserName(name)) {
            return
        }
        else if (!validateUserEmail(email)) {
            return
        }
        else if (!validateUserPassword(password, confirmPassword)) {
            return
        }
        else {
            // If all validations pass, show success message
            Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
            // Navigate to next activity after sign up (e.g., HomeActivity or LoginActivity)
            val intent = Intent(this, Login_Activity::class.java)
            startActivity(intent)
            finish() // Close SignUp activity
        }
    }



    // Name validation
    private fun validateUserName(name: String): Boolean {
        if (name.isEmpty()) {
            nameEditText.error = "Name cannot be empty"
            nameEditText.requestFocus()
            return false
        }
        else{
            nameEditText.error = null
            return true
        }
    }



    // Email validation
    private fun validateUserEmail(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9._%+-]+@(gmail\\.com|outlook\\.com|yahoo\\.com|lus\\.ac\\.bd)$"
        if (email.isEmpty()) {
            emailEditText.error = "Email cannot be empty"
            emailEditText.requestFocus()
            return false
        } else if (!email.matches(emailRegex.toRegex()) && !email.matches("^cse_[0-9]{16}@lus\\.ac\\.bd$".toRegex())) {
            emailEditText.error = "Invalid email format"
            emailEditText.requestFocus()
            return false
        }
        else{
            emailEditText.error = null
            return true
        }
    }



    // Password and Confirm Password validation
    private fun validateUserPassword(password: String, confirmPassword: String): Boolean {
        if (password.isEmpty()) {
            passwordEditText.error = "Password cannot be empty"
            passwordEditText.requestFocus()
            return false
        } else if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.error = "Confirm password cannot be empty"
            confirmPasswordEditText.requestFocus()
            return false
        } else if (password != confirmPassword) {
            confirmPasswordEditText.error = "Passwords do not match"
            confirmPasswordEditText.requestFocus()
            return false
        }
        else{
            passwordEditText.error = null
            confirmPasswordEditText.error = null
            return true
        }
    }
}
