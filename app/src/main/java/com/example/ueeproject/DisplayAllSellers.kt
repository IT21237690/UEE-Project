package com.example.ueeproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class DisplayAllSellers : AppCompatActivity(), sellersadapter.OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_sellers)

        // Initialize RecyclerView and its adapter
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch data from Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                val itemList = mutableListOf<Seller>()
                for (document in documents) {
                    val item = document.toObject(Seller::class.java)
                    itemList.add(item)
                }

                // Initialize and set up the adapter
                val adapter = sellersadapter(itemList, this)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }

    override fun onItemClick(item: Seller) {
        // Handle item click by opening ItemDetailsActivity
        val intent = Intent(this, SellerDetails::class.java)
        intent.putExtra("ITEM_ID", item.User_ID) // Assuming your SellItem class has a property named 'id' for item ID
        startActivity(intent)
    }


}
