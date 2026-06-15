package com.flowsense.app

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class OrgSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_org_settings)

        findViewById<LinearLayout>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, OrgProfileDetailsActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.btnPrivacy).setOnClickListener {
            startActivity(Intent(this, PrivacyActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.btnChangePassword).setOnClickListener {
            startActivity(Intent(this, OrgChangePasswordActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.btnHelp).setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.btnNavUpload).setOnClickListener {
            startActivity(Intent(this, OrgAddVideoActivity::class.java))
            finish()
        }

        findViewById<LinearLayout>(R.id.btnNavCctv).setOnClickListener {
            startActivity(Intent(this, OrgCctvActivity::class.java))
            finish()
        }
        
        findViewById<LinearLayout>(R.id.btnLogout).setOnClickListener {
            val intent = Intent(this, RoleSelectionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
