package com.example.rent_rover

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class Login_Activity : AppCompatActivity() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var rememberMeCheckBox: CheckBox
    private lateinit var forgotPasswordText: TextView
    private lateinit var createAccountText: TextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var googleSignInClient: GoogleSignInClient

    private val RC_SIGN_IN = 100



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

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize LoadingDialog
        loadingDialog = LoadingDialog(this)


        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)



        // Google Sign-In button
        val googleSignInButton: com.google.android.gms.common.SignInButton = findViewById(R.id.google_sign_in_button)
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }



        // Set click listeners
        loginButton.setOnClickListener {
            loginUser()
        }



        forgotPasswordText.setOnClickListener {
            // Handle forgot password logic (perhaps open a dialog or another activity)
            Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Reset_Password_activity::class.java)
            startActivity(intent)
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
        } else if (!validateUserPassword(password)) {
            return
        } else {
            loadingDialog.show()
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    loadingDialog.dismiss()
                    if (task.isSuccessful) {
                        // Save user session
                        val user = firebaseAuth.currentUser
                        val sessionManager = SessionManager(this)
                        user?.let {
                            sessionManager.createLoginSession(it.uid, it.email ?: "", it.displayName ?: "")
                        }

                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }



    private fun signInWithGoogle() {
        loadingDialog.show()
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                loadingDialog.dismiss()
                Log.w("GoogleSignIn", "Google sign-in failed", e)
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                loadingDialog.dismiss()
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val sessionManager = SessionManager(this)
                    user?.let {
                        sessionManager.createLoginSession(it.uid, it.email ?: "", it.displayName ?: "")
                        saveUserDetailsToDatabase(it.uid, it.displayName, it.email)
                    }

                    Toast.makeText(this, "Google Sign-In successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }



    private fun saveUserDetailsToDatabase(uid: String?, name: String?, email: String?) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("Users")
        val user = mapOf(
            "name" to name,
            "email" to email,
            "uid" to uid
        )

        uid?.let {
            usersRef.child(it).setValue(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("RealtimeDatabase", "Successfully")
                } else {
                    Log.w("RealtimeDatabase", "Failed to save user details", task.exception)
                }
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
