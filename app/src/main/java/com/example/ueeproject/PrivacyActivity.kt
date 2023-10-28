package com.example.ueeproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ueeproject.databinding.ActivityPrivacyBinding

class PrivacyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_privacy)
    }
}