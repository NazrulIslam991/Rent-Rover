package com.example.rent_rover

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat

class EditProfileActivity : AppCompatActivity() {
    private lateinit var exit: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        // Set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.p_bg)
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        exit=findViewById(R.id.back)
        exit.setOnClickListener{
            finish()
        }

    }
}