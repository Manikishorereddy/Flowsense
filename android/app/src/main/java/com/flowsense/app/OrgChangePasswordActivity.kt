package com.flowsense.app

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.flowsense.app.network.ApiClient
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class OrgChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnSave = findViewById<android.widget.Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val newPass = etNewPassword.text.toString()
            val confirmPass = etConfirmPassword.text.toString()

            if (newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPass != confirmPass) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("OrgPrefs", MODE_PRIVATE)
            val email = prefs.getString("email", "")

            if (email.isNullOrEmpty()) {
                Toast.makeText(this, "Error: Organization email not found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val json = JSONObject()
            json.put("email", email)
            json.put("new_password", newPass)

            val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(ApiClient.BASE_URL + "org/change_password.php")
                .post(body)
                .build()

            ApiClient.client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread { Toast.makeText(this@OrgChangePasswordActivity, "Connection error", Toast.LENGTH_SHORT).show() }
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()
                    runOnUiThread {
                        if (response.isSuccessful) {
                            Toast.makeText(this@OrgChangePasswordActivity, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            val jsonResponse = JSONObject(responseData ?: "{}")
                            val msg = jsonResponse.optString("message", "Update failed")
                            Toast.makeText(this@OrgChangePasswordActivity, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }
    }
}
