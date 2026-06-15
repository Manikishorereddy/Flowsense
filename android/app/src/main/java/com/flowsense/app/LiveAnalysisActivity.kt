package com.flowsense.app

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.flowsense.app.network.ApiClient
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class LiveAnalysisActivity : AppCompatActivity() {

    private var analyticsData: org.json.JSONArray? = null
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private var videoView: VideoView? = null
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_analysis)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val videoPath = intent.getStringExtra("VIDEO_PATH")
        val title = intent.getStringExtra("TITLE")
        val isOrg = intent.getBooleanExtra("IS_ORG", false)
        val videoId = intent.getIntExtra("VIDEO_ID", -1)

        val btnDelete = findViewById<ImageView>(R.id.btnDelete)
        if (isOrg && videoId != -1) {
            btnDelete.visibility = View.VISIBLE
            btnDelete.setOnClickListener {
                deleteVideo(videoId)
            }
        } else {
            btnDelete.visibility = View.GONE
        }

        if (title != null) {
            findViewById<TextView>(R.id.tvTitle).text = title
        }

        if (videoPath != null) {
            videoView = findViewById(R.id.videoView)
            val fullUrl = (ApiClient.BASE_URL.replace("api/", "") + videoPath).replace(" ", "%20")
            videoView?.setVideoURI(Uri.parse(fullUrl))
            
            videoView?.setOnPreparedListener { mp ->
                mp.isLooping = true
                videoView?.start()
                isPlaying = true
                startAnalyticsSync()
            }

            // Fetch analytics JSON
            val jsonUrl = fullUrl.replace(".mp4", ".json")
            fetchAnalytics(jsonUrl)
        }
    }

    private fun fetchAnalytics(jsonUrl: String) {
        val request = Request.Builder().url(jsonUrl).get().build()
        ApiClient.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                handler.postDelayed({ fetchAnalytics(jsonUrl) }, 2000)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonData = response.body?.string()
                    if (jsonData != null) {
                        try {
                            analyticsData = org.json.JSONArray(jsonData)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            handler.postDelayed({ fetchAnalytics(jsonUrl) }, 2000)
                        }
                    }
                } else {
                    handler.postDelayed({ fetchAnalytics(jsonUrl) }, 2000)
                }
            }
        })
    }

    private fun startAnalyticsSync() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isPlaying && videoView != null && analyticsData != null) {
                    val currentSec = videoView!!.currentPosition / 1000
                    updateStatsForSecond(currentSec)
                }
                if (isPlaying) {
                    handler.postDelayed(this, 500)
                }
            }
        }, 500)
    }

    private fun updateStatsForSecond(currentSec: Int) {
        val data = analyticsData ?: return
        for (i in 0 until data.length()) {
            val item = data.getJSONObject(i)
            if (item.getInt("time_sec") == currentSec) {
                val count = item.getInt("count")
                val waitTime = count * 3 // Estimate 3 mins per person
                val density = Math.min((count * 100) / 20, 100) // Assume 20 people = 100% capacity

                findViewById<TextView>(R.id.tvLiveCount)?.text = count.toString()
                findViewById<TextView>(R.id.tvEstWait)?.text = waitTime.toString()
                findViewById<TextView>(R.id.tvDensityPct)?.text = "$density%"
                findViewById<android.widget.ProgressBar>(R.id.pbDensity)?.progress = density

                val countStatus = findViewById<TextView>(R.id.tvCountStatus)
                if (count > 10) {
                    countStatus?.text = "High"
                    countStatus?.setTextColor(android.graphics.Color.parseColor("#FF4D4D"))
                } else if (count > 5) {
                    countStatus?.text = "Moderate"
                    countStatus?.setTextColor(android.graphics.Color.parseColor("#F59E0B"))
                } else {
                    countStatus?.text = "Low"
                    countStatus?.setTextColor(android.graphics.Color.parseColor("#1CB5E0"))
                }
                val boxContainer = findViewById<android.widget.FrameLayout>(R.id.boxContainer)
                boxContainer?.removeAllViews()

                val boxes = item.optJSONArray("boxes")
                if (boxes != null) {
                    val containerWidth = boxContainer?.width ?: 0
                    val containerHeight = boxContainer?.height ?: 0

                    for (j in 0 until boxes.length()) {
                        val box = boxes.getJSONObject(j)
                        val x = box.getDouble("x")
                        val y = box.getDouble("y")
                        val w = box.getDouble("w")
                        val h = box.getDouble("h")

                        val view = View(this)
                        view.setBackgroundResource(R.drawable.bg_bounding_box)
                        
                        val params = android.widget.FrameLayout.LayoutParams(
                            (w * containerWidth).toInt(),
                            (h * containerHeight).toInt()
                        )
                        params.leftMargin = (x * containerWidth).toInt()
                        params.topMargin = (y * containerHeight).toInt()
                        
                        view.layoutParams = params
                        boxContainer?.addView(view)
                    }
                }

                break
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isPlaying = false
        handler.removeCallbacksAndMessages(null)
    }

    private fun deleteVideo(videoId: Int) {
        val json = JSONObject()
        json.put("video_id", videoId)
        json.put("org_id", 1) // Hardcoded for now

        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())

        val request = Request.Builder()
            .url(ApiClient.BASE_URL + "org/delete_video.php")
            .post(requestBody)
            .build()

        ApiClient.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { Toast.makeText(this@LiveAnalysisActivity, "Delete Failed", Toast.LENGTH_SHORT).show() }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@LiveAnalysisActivity, "Video deleted", Toast.LENGTH_SHORT).show()
                        finish() // Close the preview
                    } else {
                        Toast.makeText(this@LiveAnalysisActivity, "Delete Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
