package com.flowsense.app

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class OrgCctvActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_org_cctv)

        findViewById<LinearLayout>(R.id.btnNavUpload).setOnClickListener {
            startActivity(Intent(this, OrgAddVideoActivity::class.java))
            finish()
        }

        findViewById<LinearLayout>(R.id.btnNavSettings).setOnClickListener {
            startActivity(Intent(this, OrgSettingsActivity::class.java))
            finish()
        }
    }
}
