package com.flowsense.app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flowsense.app.network.ApiClient
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MonitorActivity : AppCompatActivity() {

    private lateinit var adapter: FeedAdapter
    private val items = mutableListOf<FeedItem>()
    private lateinit var tvActiveCamerasCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor)

        tvActiveCamerasCount = findViewById(R.id.tvActiveCamerasCount)
        val rvFeeds = findViewById<RecyclerView>(R.id.rvFeeds)
        rvFeeds.layoutManager = LinearLayoutManager(this)

        adapter = FeedAdapter(items) { clickedItem ->
            val intent = Intent(this, LiveAnalysisActivity::class.java)
            intent.putExtra("TITLE", clickedItem.title)
            intent.putExtra("VIDEO_PATH", clickedItem.videoUrl)
            startActivity(intent)
        }
        rvFeeds.adapter = adapter

        fetchFeeds()

        // Setup bottom nav
        findViewById<android.widget.LinearLayout>(R.id.btnNavHome).setOnClickListener {
            startActivity(Intent(this, UserDashboardActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
        
        findViewById<android.widget.LinearLayout>(R.id.btnNavPredict).setOnClickListener {
            startActivity(Intent(this, PredictActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
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
                runOnUiThread { Toast.makeText(this@MonitorActivity, "Connection error", Toast.LENGTH_SHORT).show() }
            }

            override fun onResponse(call: Call, response: Response) {
                val resData = response.body?.string()
                runOnUiThread {
                    if (response.isSuccessful && resData != null) {
                        try {
                            val json = JSONObject(resData)
                            val feeds = json.optJSONArray("feeds")
                            val newItems = mutableListOf<FeedItem>()
                            if (feeds != null) {
                                tvActiveCamerasCount.text = feeds.length().toString()
                                for (i in 0 until feeds.length()) {
                                    val feed = feeds.getJSONObject(i)
                                    val title = feed.optString("title", "Unknown")
                                    val org = feed.optString("org_name", "Organization")
                                    val videoPath = feed.optString("file_path", "")
                                    
                                    val feedTitle = "$title of $org"
                                    
                                    // Simulated count and density
                                    val count = (10..200).random()
                                    val density = (count * 100) / 250
                                    val pct = Math.min(density, 100)
                                    val showHighDensityAlert = pct > 80
                                    
                                    newItems.add(
                                        FeedItem(
                                            feedTitle,
                                            count.toString(),
                                            "$pct%",
                                            showHighDensityAlert,
                                            videoPath
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
