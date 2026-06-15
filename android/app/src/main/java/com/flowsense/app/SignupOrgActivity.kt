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

class SignupOrgActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_org)

        val btnSignup = findViewById<android.widget.Button>(R.id.btnSignup)
        val etOrgName = findViewById<EditText>(R.id.etOrgName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        btnSignup.setOnClickListener {
            val orgName = etOrgName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (orgName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val json = JSONObject()
            json.put("org_name", orgName)
            json.put("email", email)
            json.put("password", password)

            val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(ApiClient.BASE_URL + "org/signup.php")
                .post(body)
                .build()

            ApiClient.client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread { Toast.makeText(this@SignupOrgActivity, "Connection error", Toast.LENGTH_SHORT).show() }
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()
                    runOnUiThread {
                        if (response.isSuccessful) {
                            val jsonResponse = JSONObject(responseData ?: "{}")
                            val orgId = jsonResponse.optInt("id", -1)
                            
                            val prefs = getSharedPreferences("OrgPrefs", MODE_PRIVATE)
                            prefs.edit().apply {
                                putInt("org_id", orgId)
                                putString("org_name", orgName)
                                putString("email", email)
                                apply()
                            }
                            
                            startActivity(Intent(this@SignupOrgActivity, OrgAddVideoActivity::class.java))
                            finish()
                        } else {
                            val jsonResponse = JSONObject(responseData ?: "{}")
                            val msg = jsonResponse.optString("message", "Signup failed")
                            Toast.makeText(this@SignupOrgActivity, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }

        findViewById<TextView>(R.id.tvLogin).setOnClickListener {
            startActivity(Intent(this, LoginOrgActivity::class.java))
            finish()
        }
    }
}
