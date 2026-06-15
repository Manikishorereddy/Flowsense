package com.flowsense.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class RoleSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role_selection)

        val cardOrg = findViewById<ConstraintLayout>(R.id.cardOrg)
        val cardUser = findViewById<ConstraintLayout>(R.id.cardUser)

        cardOrg.setOnClickListener {
            startActivity(android.content.Intent(this, LoginOrgActivity::class.java))
        }

        cardUser.setOnClickListener {
            startActivity(android.content.Intent(this, LoginUserActivity::class.java))
        }
    }
}
