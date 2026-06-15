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

class PredictActivity : AppCompatActivity() {

    private lateinit var adapter: PredictAdapter
    private val items = mutableListOf<PredictItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_predict)

        val rvPredicts = findViewById<RecyclerView>(R.id.rvPredicts)
        rvPredicts.layoutManager = LinearLayoutManager(this)
        
        adapter = PredictAdapter(items) { clickedItem ->
            val intent = Intent(this, AnalysisDetailActivity::class.java)
            intent.putExtra("TITLE", clickedItem.title)
            intent.putExtra("VIDEO_PATH", clickedItem.videoPath)
            startActivity(intent)
        }
        rvPredicts.adapter = adapter

        fetchFeeds()

        // Setup Bottom Navigation
        findViewById<android.widget.LinearLayout>(R.id.btnNavHome).setOnClickListener {
            startActivity(Intent(this, UserDashboardActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
        
        findViewById<android.widget.LinearLayout>(R.id.btnNavMonitor).setOnClickListener {
            startActivity(Intent(this, MonitorActivity::class.java))
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
                runOnUiThread { Toast.makeText(this@PredictActivity, "Connection error", Toast.LENGTH_SHORT).show() }
            }

            override fun onResponse(call: Call, response: Response) {
                val resData = response.body?.string()
                runOnUiThread {
                    if (response.isSuccessful && resData != null) {
                        try {
                            val json = JSONObject(resData)
                            val feeds = json.optJSONArray("feeds")
                            val newItems = mutableListOf<PredictItem>()
                            if (feeds != null) {
                                for (i in 0 until feeds.length()) {
                                    val feed = feeds.getJSONObject(i)
                                    val title = feed.optString("title", "Unknown")
                                    val org = feed.optString("org_name", "Organization")
                                    val videoPath = feed.optString("file_path", "")
                                    
                                    val feedTitle = "$title of $org"
                                    
                                    val r = (1..3).random()
                                    var subtitle = ""
                                    var color = ""
                                    
                                    if (r == 1) {
                                        subtitle = "Peak expected soon"
                                        color = "#ff4b4b"
                                    } else if (r == 2) {
                                        subtitle = "Low traffic expected"
                                        color = "#00e676"
                                    } else {
                                        subtitle = "Steady flow"
                                        color = "#1cb5e0"
                                    }
                                    
                                    newItems.add(
                                        PredictItem(
                                            feedTitle,
                                            subtitle,
                                            color,
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
