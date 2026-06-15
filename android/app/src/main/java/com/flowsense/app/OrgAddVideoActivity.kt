package com.flowsense.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flowsense.app.network.ApiClient
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class OrgAddVideoActivity : AppCompatActivity() {

    private lateinit var adapter: OrgVideoAdapter
    private val items = mutableListOf<OrgVideoItem>()
    private var cctvNameStr: String = ""
    private var currentOrgId: Int = -1

    private val pickVideo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let { uploadVideo(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_org_add_video)

        val rvOrgVideos = findViewById<RecyclerView>(R.id.rvOrgVideos)
        rvOrgVideos.layoutManager = LinearLayoutManager(this)
        
        adapter = OrgVideoAdapter(items, 
            onPlayClick = { item ->
                val intent = Intent(this, LiveAnalysisActivity::class.java)
                intent.putExtra("TITLE", item.title)
                intent.putExtra("VIDEO_PATH", item.path)
                intent.putExtra("VIDEO_ID", item.id)
                intent.putExtra("IS_ORG", true)
                startActivity(intent)
            },
            onDeleteClick = { item ->
                deleteVideo(item.id)
            }
        )
        rvOrgVideos.adapter = adapter

        val prefs = getSharedPreferences("OrgPrefs", MODE_PRIVATE)
        currentOrgId = prefs.getInt("org_id", -1)

        fetchVideos()

        findViewById<Button>(R.id.btnBrowse).setOnClickListener {
            val etCctvName = findViewById<TextInputEditText>(R.id.etCctvName)
            cctvNameStr = etCctvName.text.toString().trim()
            if (cctvNameStr.isEmpty()) {
                Toast.makeText(this, "Please enter a CCTV Name first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            pickVideo.launch(intent)
        }

        findViewById<LinearLayout>(R.id.btnNavCctv).setOnClickListener {
            startActivity(Intent(this, OrgCctvActivity::class.java))
            finish()
        }

        findViewById<LinearLayout>(R.id.btnNavSettings).setOnClickListener {
            startActivity(Intent(this, OrgSettingsActivity::class.java))
            finish()
        }
    }

    private fun fetchVideos() {
        if (currentOrgId == -1) return
        
        val request = Request.Builder()
            .url(ApiClient.BASE_URL + "org/get_videos.php?org_id=$currentOrgId")
            .get()
            .build()

        ApiClient.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                val resData = response.body?.string()
                runOnUiThread {
                    if (response.isSuccessful && resData != null) {
                        try {
                            val json = JSONObject(resData)
                            val videos = json.optJSONArray("videos")
                            val newItems = mutableListOf<OrgVideoItem>()
                            if (videos != null) {
                                for (i in 0 until videos.length()) {
                                    val video = videos.getJSONObject(i)
                                    val id = video.getInt("id")
                                    val title = video.optString("title", "Unknown")
                                    val path = video.optString("file_path", "")
                                    val date = video.optString("created_at", "Just now")
                                    
                                    newItems.add(OrgVideoItem(id, title, date, path))
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

    private fun deleteVideo(videoId: Int) {
        val json = JSONObject()
        json.put("video_id", videoId)
        json.put("org_id", currentOrgId)

        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())

        val request = Request.Builder()
            .url(ApiClient.BASE_URL + "org/delete_video.php")
            .post(requestBody)
            .build()

        ApiClient.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { Toast.makeText(this@OrgAddVideoActivity, "Delete Failed", Toast.LENGTH_SHORT).show() }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@OrgAddVideoActivity, "Video deleted", Toast.LENGTH_SHORT).show()
                        fetchVideos()
                    } else {
                        Toast.makeText(this@OrgAddVideoActivity, "Delete Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun uploadVideo(uri: Uri) {
        Toast.makeText(this, "Uploading video...", Toast.LENGTH_SHORT).show()
        
        // Copy to temp file to upload
        val tempFile = File(cacheDir, "temp_video.mp4")
        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("org_id", currentOrgId.toString())
            .addFormDataPart("title", cctvNameStr)
            .addFormDataPart(
                "video_file",
                tempFile.name,
                RequestBody.create("video/*".toMediaTypeOrNull(), tempFile)
            )
            .build()

        val request = Request.Builder()
            .url(ApiClient.BASE_URL + "org/upload_video.php")
            .post(requestBody)
            .build()

        ApiClient.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { Toast.makeText(this@OrgAddVideoActivity, "Upload Failed", Toast.LENGTH_SHORT).show() }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@OrgAddVideoActivity, "Video uploaded successfully", Toast.LENGTH_SHORT).show()
                        findViewById<TextInputEditText>(R.id.etCctvName).setText("")
                        fetchVideos()
                    } else {
                        Toast.makeText(this@OrgAddVideoActivity, "Upload Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
