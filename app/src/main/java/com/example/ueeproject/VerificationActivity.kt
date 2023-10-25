package com.example.ueeproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ueeproject.R
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

                val items = snapshot?.documents?.map { documentSnapshot ->
                    val title = documentSnapshot.getString("itemName") ?: ""
                    //val sellername = documentSnapshot.getString("sellername") ?: ""
                    Comps(
                        id = documentSnapshot.id,
                        title = title,
                        // sellername = sellername
                    )
                } ?: emptyList()

                adapter.setItems(items)
            }
    }
}
