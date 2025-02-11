package com.example.rent_rover

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class SignUp_Activity : AppCompatActivity() {

    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var signUpButton: Button
    private lateinit var rememberMeCheckBox: CheckBox
    private lateinit var loginText: TextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var loadingDialog: LoadingDialog

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
        signUpButton = findViewById(R.id.signup_button)
        rememberMeCheckBox = findViewById(R.id.login_password_show_hide)
        loginText = findViewById(R.id.create_account)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize LoadingDialog
        loadingDialog = LoadingDialog(this)

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
        } else if (!validateUserEmail(email)) {
            return
        } else if (!validateUserPassword(password, confirmPassword)) {
            return
        } else {

            // Show loading dialog
            loadingDialog.show()

            // Firebase authentication
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        sendEmailVerification(user, name, email)
                    } else {
                        loadingDialog.dismiss()
                        Toast.makeText(this, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }


    private fun sendEmailVerification(user: FirebaseUser?, name: String, email: String) {
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Verification email sent to $email", Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()

                // Poll to check email verification status every 5 seconds for up to 1 minute
                val maxRetries = 24 // 12 * 5 seconds = 60 seconds
                var retries = 0

                val handler = Handler(Looper.getMainLooper())
                val runnable = object : Runnable {
                    override fun run() {
                        user.reload().addOnCompleteListener { reloadTask ->
                            if (reloadTask.isSuccessful && user.isEmailVerified) {
                                loadingDialog.show()
                                saveUserToDatabase(name, email, user.uid,"","")
                                handler.removeCallbacks(this) // Stop checking
                            } else {
                                retries++
                                if (retries < maxRetries) {
                                    handler.postDelayed(this, 5000) // Retry after 5 seconds
                                } else {
                                    deleteUnverifiedAccount(user)
                                    handler.removeCallbacks(this) // Stop after max retries
                                }
                            }
                        }
                    }
                }
                handler.post(runnable)

            } else {
                loadingDialog.dismiss()
                Toast.makeText(this, "Failed to send verification email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun saveUserToDatabase(name: String, email: String, uid: String, mobile: String = "", address: String = "") {
        val userMap = hashMapOf(
            "name" to name,
            "email" to email,
            "uid" to uid,
            "mobile" to mobile,
            "address" to address
        )

        FirebaseDatabase.getInstance().getReference("Users").child(uid).setValue(userMap)
            .addOnCompleteListener { task ->
                loadingDialog.dismiss()
                if (task.isSuccessful) {
                    Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Login_Activity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to save user data: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun deleteUnverifiedAccount(user: FirebaseUser) {
        user.delete().addOnCompleteListener { task ->
            loadingDialog.dismiss()
            if (task.isSuccessful) {
                Toast.makeText(this, "Account removed due to unverified email.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to remove account: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateUserName(name: String): Boolean {
        if (name.isEmpty()) {
            nameEditText.error = "Name cannot be empty"
            nameEditText.requestFocus()
            return false
        } else {
            nameEditText.error = null
            return true
        }
    }

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
        } else {
            emailEditText.error = null
            return true
        }
    }

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
        } else {
            passwordEditText.error = null
            confirmPasswordEditText.error = null
            return true
        }
    }
}
