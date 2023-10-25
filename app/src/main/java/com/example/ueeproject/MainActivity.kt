package com.example.comps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var createButton: Button
    private lateinit var readButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize createButton and readButton by finding them in the layout
        createButton = findViewById(R.id.createButton)
        readButton = findViewById(R.id.readButton)

        createButton.setOnClickListener {
            startActivity(Intent(this, CreateCompsActivity::class.java))
        }

        readButton.setOnClickListener {
            startActivity(Intent(this, CurrentCompsActivity::class.java))
        }
    }
}
