package com.flowsense.app

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var dotsLayout: LinearLayout
    private lateinit var btnNext: TextView
    private lateinit var btnSkip: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)
        dotsLayout = findViewById(R.id.dotsLayout)
        btnNext = findViewById(R.id.btnNext)
        btnSkip = findViewById(R.id.btnSkip)

        val items = listOf(
            OnboardingItem(
                R.drawable.ic_eye,
                "Monitor live crowd density",
                "Get real-time updates on crowd levels at hospitals, banks, and public offices before you visit."
            ),
            OnboardingItem(
                R.drawable.ic_clock,
                "Predict waiting time using AI",
                "Our advanced AI analyzes historical data and live feeds to give you highly accurate wait time predictions."
            ),
            OnboardingItem(
                R.drawable.ic_shield,
                "Avoid queues & save time",
                "Plan your visits during off-peak hours and never waste your valuable time standing in long lines again."
            )
        )

        viewPager.adapter = OnboardingAdapter(items)
        setupDots(items.size)
        updateDots(0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDots(position)
                if (position == items.size - 1) {
                    btnNext.text = "Get Started"
                } else {
                    btnNext.text = "Continue >"
                }
            }
        })

        btnNext.setOnClickListener {
            if (viewPager.currentItem < items.size - 1) {
                viewPager.currentItem += 1
            } else {
                startActivity(android.content.Intent(this, RoleSelectionActivity::class.java))
                finish()
            }
        }

        btnSkip.setOnClickListener {
            startActivity(android.content.Intent(this, RoleSelectionActivity::class.java))
            finish()
        }
    }

    private fun setupDots(count: Int) {
        val dots = arrayOfNulls<ImageView>(count)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(8, 0, 8, 0) }

        for (i in 0 until count) {
            dots[i] = ImageView(this)
            dots[i]?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_inactive))
            dots[i]?.layoutParams = params
            dotsLayout.addView(dots[i])
        }
    }

    private fun updateDots(position: Int) {
        for (i in 0 until dotsLayout.childCount) {
            val imageView = dotsLayout.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_active))
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_inactive))
            }
        }
    }
}
