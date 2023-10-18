package com.example.ueeproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class OngoingAuctions : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: AuctionItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ongoing_auctions)

        recyclerView = findViewById(R.id.recyclerView)

        adapter = AuctionItemsAdapter(object : AuctionItemsAdapter.OnItemClickListener {
            override fun onEditClick(position: Int) {
                val clickedItem = adapter.currentList[position]
                val itemId = clickedItem.itemId // Assuming itemId is a property in your AuctionItem class

                // Start EditItemActivity and pass itemId as an extra
                val intent = Intent(this@OngoingAuctions, EditItemActivity::class.java)
                intent.putExtra("itemId", itemId)
                startActivityForResult(intent, REQUEST_CODE_EDIT_ITEM)
            }

            override fun onDeleteClick(position: Int) {
                // Implement delete functionality if needed
            }
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Query Firestore to get ongoing items
        val currentTime = System.currentTimeMillis()

        db.collection("Auction")
            .whereLessThan("startTime", currentTime)
            .get()
            .addOnSuccessListener { documents ->
                val itemsList = mutableListOf<AuctionItem>()
                for (document in documents) {
                    val item = document.toObject(AuctionItem::class.java)
                    // Filter by endTime in your code
                    if (item.endTime > currentTime) {
                        itemsList.add(item)
                        Log.d("FirestoreData", "${document.id} => ${document.data}")
                    }
                }
                // Update RecyclerView with ongoing items
                adapter.submitList(itemsList)
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreData", "Error getting documents: $exception")
                // Handle error if data retrieval fails
            }

    }

    companion object {
        private const val REQUEST_CODE_EDIT_ITEM = 1
    }
}