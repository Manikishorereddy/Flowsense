package com.flowsense.app

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Setup WebView for Map
        val webView = findViewById<WebView>(R.id.webViewMap)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        // Load Google Maps with dark mode filter via CSS in an iframe
        val mapHtml = """
            <!DOCTYPE html>
            <html>
            <body style="margin:0;padding:0;">
                <iframe 
                    src="https://maps.google.com/maps?q=Downtown,NY&t=m&z=14&ie=UTF8&iwloc=&output=embed" 
                    width="100%" 
                    height="100%" 
                    style="border:0; filter: invert(90%) hue-rotate(180deg); position:absolute; top:0; left:0; width:100%; height:100%;" 
                    allowfullscreen="" 
                    loading="lazy">
                </iframe>
            </body>
            </html>
        """.trimIndent()
        webView.loadDataWithBaseURL(null, mapHtml, "text/html", "utf-8", null)

        // Bottom Nav setup
        findViewById<android.widget.LinearLayout>(R.id.btnNavHome).setOnClickListener {
            startActivity(android.content.Intent(this, UserDashboardActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
        
        findViewById<android.widget.LinearLayout>(R.id.btnNavMonitor).setOnClickListener {
            startActivity(android.content.Intent(this, MonitorActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
        
        findViewById<android.widget.LinearLayout>(R.id.btnNavPredict).setOnClickListener {
            startActivity(android.content.Intent(this, PredictActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        findViewById<android.widget.LinearLayout>(R.id.btnNavProfile).setOnClickListener {
            android.widget.Toast.makeText(this, "Profile Coming Soon", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}
