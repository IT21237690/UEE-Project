package com.example.ueeproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class DisplaysellingItemsToBuyers : AppCompatActivity(), buyerviewadapter.OnItemClickListener {



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_displayselling_items_to_buyers)


        // Initialize RecyclerView and its adapter
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)



        // Fetch data from Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("Sell Items")
            .get()
            .addOnSuccessListener { documents ->
                val itemList = mutableListOf<SellItem>()
                for (document in documents) {
                    val item = document.toObject(SellItem::class.java)
                    itemList.add(item)
                }

                // Initialize and set up the adapter
                val adapter = buyerviewadapter(itemList, this)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }

    override fun onItemClick(item: SellItem) {
        // Handle item click by opening ItemDetailsActivity
        val intent = Intent(this, ItemDetailsActivity::class.java)
        intent.putExtra("ITEM_ID", item.itemId) // Assuming your SellItem class has a property named 'id' for item ID
        startActivity(intent)
    }


}
