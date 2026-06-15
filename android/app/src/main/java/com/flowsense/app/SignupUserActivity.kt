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

class SignupUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_user)

        val btnSignup = findViewById<android.widget.Button>(R.id.btnSignup)
        val etName = findViewById<EditText>(R.id.etName)
        val etLocation = findViewById<EditText>(R.id.etLocation)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        btnSignup.setOnClickListener {
            val name = etName.text.toString()
            val location = etLocation.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (name.isEmpty() || location.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val json = JSONObject()
            json.put("full_name", name)
            json.put("location", location)
            json.put("email", email)
            json.put("password", password)

            val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(ApiClient.BASE_URL + "user/signup.php")
                .post(body)
                .build()

            ApiClient.client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread { Toast.makeText(this@SignupUserActivity, "Connection error", Toast.LENGTH_SHORT).show() }
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()
                    runOnUiThread {
                        if (response.isSuccessful) {
                            val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                            prefs.edit().apply {
                                putString("full_name", name)
                                putString("location", location)
                                putString("email", email)
                                apply()
                            }
                            startActivity(Intent(this@SignupUserActivity, UserDashboardActivity::class.java))
                            finish()
                        } else {
                            val jsonResponse = JSONObject(responseData ?: "{}")
                            val msg = jsonResponse.optString("message", "Signup failed")
                            Toast.makeText(this@SignupUserActivity, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }

        findViewById<TextView>(R.id.tvLogin).setOnClickListener {
            startActivity(Intent(this, LoginUserActivity::class.java))
            finish()
        }
    }
}
