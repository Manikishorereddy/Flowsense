package com.flowsense.app

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class OrgProfileDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_org_profile_details)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val prefs = getSharedPreferences("OrgPrefs", MODE_PRIVATE)
        var orgName = prefs.getString("org_name", "Organization") ?: "Organization"
        if (orgName == "null" || orgName.isEmpty()) orgName = "Organization"
        
        var email = prefs.getString("email", "email@example.com") ?: "email@example.com"
        if (email == "null" || email.isEmpty()) email = "email@example.com"

        findViewById<TextView>(R.id.tvProfileName).text = orgName
        findViewById<TextView>(R.id.tvProfileEmail).text = email
        findViewById<TextView>(R.id.tvDetailsName).text = orgName
        findViewById<TextView>(R.id.tvDetailsEmail).text = email
    }
}
