package com.example.rent_rover

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.cloudinary.android.MediaManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set status bar color
        window.statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.white)
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Initialize Cloudinary configuration
        val config = HashMap<String, String>()
        config["cloud_name"] = "dzoadbvof"
        config["api_key"] = "127214459725141"
        config["api_secret"] = "jl6A5zOHd8BMonsddPzSZCIWNok"

        MediaManager.init(this, config)

        // Start the Login Activity after a short delay
        Handler().postDelayed({
            startActivity(Intent(this@MainActivity, Login_Activity::class.java))
            finish()
        }, 800) // Delay for 800 milliseconds
    }
}
