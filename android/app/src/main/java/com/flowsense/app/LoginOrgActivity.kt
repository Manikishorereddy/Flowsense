package com.flowsense.app

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.flowsense.app.network.ApiClient
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class LoginOrgActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_org)

        val btnLogin = findViewById<android.widget.Button>(R.id.btnLogin)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val json = JSONObject()
            json.put("email", email)
            json.put("password", password)

            val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(ApiClient.BASE_URL + "org/login.php")
                .post(body)
                .build()

            ApiClient.client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread { Toast.makeText(this@LoginOrgActivity, "Connection error", Toast.LENGTH_SHORT).show() }
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()
                    runOnUiThread {
                        if (response.isSuccessful) {
                            val jsonResponse = JSONObject(responseData ?: "{}")
                            val orgId = jsonResponse.optInt("id", -1)
                            val orgName = jsonResponse.optString("org_name", "Organization")
                            
                            val prefs = getSharedPreferences("OrgPrefs", MODE_PRIVATE)
                            prefs.edit().apply {
                                putInt("org_id", orgId)
                                putString("org_name", orgName)
                                putString("email", email)
                                apply()
                            }
                            
                            startActivity(Intent(this@LoginOrgActivity, OrgAddVideoActivity::class.java))
                            finish()
                        } else {
                            val jsonResponse = JSONObject(responseData ?: "{}")
                            val msg = jsonResponse.optString("message", "Login failed")
                            Toast.makeText(this@LoginOrgActivity, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }

        findViewById<TextView>(R.id.tvSignUp).setOnClickListener {
            startActivity(Intent(this, SignupOrgActivity::class.java))
            finish()
        }
    }
}
