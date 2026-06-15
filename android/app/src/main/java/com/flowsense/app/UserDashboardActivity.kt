package com.flowsense.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flowsense.app.network.ApiClient
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var adapter: LocationAdapter
    private val items = mutableListOf<LocationItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        val rvLocations = findViewById<RecyclerView>(R.id.rvLocations)
        rvLocations.layoutManager = LinearLayoutManager(this)

        adapter = LocationAdapter(items) { clickedItem ->
            val intent = Intent(this, LiveAnalysisActivity::class.java)
            intent.putExtra("TITLE", clickedItem.name)
            intent.putExtra("VIDEO_PATH", clickedItem.videoPath)
            startActivity(intent)
        }
        rvLocations.adapter = adapter

        fetchFeeds()

        // Navigation
        findViewById<android.widget.LinearLayout>(R.id.btnNavMonitor).setOnClickListener {
            startActivity(Intent(this, MonitorActivity::class.java))
            overridePendingTransition(0, 0)
        }
        
        findViewById<android.widget.LinearLayout>(R.id.btnNavPredict).setOnClickListener {
            startActivity(Intent(this, PredictActivity::class.java))
            overridePendingTransition(0, 0)
        }



        findViewById<android.widget.LinearLayout>(R.id.btnNavHome).setOnClickListener {
            // Already on home
        }

        findViewById<android.widget.LinearLayout>(R.id.btnNavProfile).setOnClickListener {
            startActivity(Intent(this, UserSettingsActivity::class.java))
            overridePendingTransition(0, 0)
        }
    }

    private fun fetchFeeds() {
        val request = Request.Builder()
            .url(ApiClient.BASE_URL + "user/get_feeds.php")
            .get()
            .build()

        ApiClient.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { Toast.makeText(this@UserDashboardActivity, "Connection error", Toast.LENGTH_SHORT).show() }
            }

            override fun onResponse(call: Call, response: Response) {
                val resData = response.body?.string()
                runOnUiThread {
                    if (response.isSuccessful && resData != null) {
                        try {
                            val json = JSONObject(resData)
                            val feeds = json.optJSONArray("feeds")
                            val newItems = mutableListOf<LocationItem>()
                            if (feeds != null) {
                                for (i in 0 until feeds.length()) {
                                    val feed = feeds.getJSONObject(i)
                                    val title = feed.optString("title", "Unknown")
                                    val org = feed.optString("org_name", "Organization")
                                    val path = feed.optString("file_path", "")
                                    
                                    val waitTimeNum = (5..50).random()
                                    val accuracyNum = (90..99).random()
                                    val status = if (waitTimeNum > 30) "High" else if (waitTimeNum > 15) "Moderate" else "Low"
                                    
                                    val feedTitle = "$title of $org"
                                    
                                    newItems.add(
                                        LocationItem(
                                            feedTitle,
                                            "2.4 km away",
                                            status,
                                            "$waitTimeNum mins",
                                            "$accuracyNum%",
                                            path
                                        )
                                    )
                                }
                            }
                            adapter.updateItems(newItems)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        })
    }
}
