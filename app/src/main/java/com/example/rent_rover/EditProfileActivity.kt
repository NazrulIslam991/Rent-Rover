package com.example.rent_rover

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EditProfileActivity : AppCompatActivity() {
    private lateinit var exit: ImageView
    private lateinit var userName: EditText
    private lateinit var userEmail: TextView
    private lateinit var user_email_top: TextView
    private lateinit var userMobile: EditText
    private lateinit var userAddress: EditText
    private lateinit var saveButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.p_bg)
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Find Views
        exit = findViewById(R.id.back)
        userName = findViewById(R.id.user_name)
        userEmail = findViewById(R.id.user_email)
        user_email_top = findViewById(R.id.user_email_top)
        userMobile = findViewById(R.id.user_mobile)
        userAddress = findViewById(R.id.user_address)
        saveButton = findViewById(R.id.save)

        // Initialize LoadingDialog
        loadingDialog = LoadingDialog(this)

        // Retrieve data passed from MenuFragment
        val name = intent.getStringExtra("name")
        val email = intent.getStringExtra("email")
        val mobile = intent.getStringExtra("mobile")
        val address = intent.getStringExtra("address")

        // Set data to fields
        userName.setText(name)
        userEmail.text = email
        user_email_top.text = email
        userMobile.setText(mobile)
        userAddress.setText(address)

        // Back button functionality
        exit.setOnClickListener {
            finish()
        }

        // Save Button Click - Store Data in Firebase
        saveButton.setOnClickListener {
            saveUserDataToFirebase()
        }
    }

    private fun saveUserDataToFirebase() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        val name = userName.text.toString().trim()
        val email = userEmail.text.toString().trim()
        val mobile = userMobile.text.toString().trim()
        val address = userAddress.text.toString().trim()

        // Check if required fields are filled
        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Name and Email are required!", Toast.LENGTH_SHORT).show()
            return
        }

        // Show Loading Dialog
        loadingDialog.show()

        // Create a map of user data
        val userMap = HashMap<String, Any>()
        userMap["name"] = name
        userMap["email"] = email
        userMap["mobile"] = mobile
        userMap["address"] = address

        // Save to Firebase Database
        val databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(uid)
        databaseRef.setValue(userMap)
            .addOnSuccessListener {
                loadingDialog.dismiss()
                Toast.makeText(this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                loadingDialog.dismiss()

                Toast.makeText(this, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}
