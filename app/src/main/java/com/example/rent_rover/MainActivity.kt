package com.example.rent_rover

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.Home)

        Handler().postDelayed({
            startActivity(Intent(this@MainActivity, Login_Activity::class.java))
            finish()
        }, 800) // Delay for 800 milliseconds

    }
}