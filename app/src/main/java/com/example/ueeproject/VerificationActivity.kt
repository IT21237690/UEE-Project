package com.example.comps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class VerificationActivity : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VerificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = VerificationAdapter()

        // Set up RecyclerView with LinearLayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Load data from Firestore and populate the RecyclerView
        loadItemsFromFirestore()
    }

    private fun loadItemsFromFirestore() {
        // Assuming you have a Firestore collection named "comps"
        firestore.collection("Sell Items")
            .whereEqualTo("IsChecked", true) // Only fetch items with isChecked == true
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle the error
                    return@addSnapshotListener
                }

                val items = snapshot?.toObjects(Comps::class.java) ?: emptyList()
                adapter.setItems(items)
            }
    }
}
