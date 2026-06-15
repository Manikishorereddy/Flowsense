package com.flowsense.app

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_details)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        var fullName = prefs.getString("full_name", "User") ?: "User"
        if (fullName == "null" || fullName.isEmpty()) fullName = "User"
        
        var email = prefs.getString("email", "email@example.com") ?: "email@example.com"
        if (email == "null" || email.isEmpty()) email = "email@example.com"

        findViewById<TextView>(R.id.tvProfileName).text = fullName
        findViewById<TextView>(R.id.tvProfileEmail).text = email
        findViewById<TextView>(R.id.tvDetailsName).text = fullName
        findViewById<TextView>(R.id.tvDetailsEmail).text = email
    }
}
