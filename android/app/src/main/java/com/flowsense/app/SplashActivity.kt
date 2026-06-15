package com.flowsense.app

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Apply gradient to AI text programmatically
        val aiText = findViewById<TextView>(R.id.aiText)
        val paint = aiText.paint
        val width = paint.measureText("AI")
        val textShader: Shader = LinearGradient(0f, 0f, width, aiText.textSize,
            intArrayOf(Color.parseColor("#1CB5E0"), Color.parseColor("#764BA2")),
            null, Shader.TileMode.CLAMP)
        aiText.paint.shader = textShader

        // Delay and route to onboarding activity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
        }, 3000)
    }
}
