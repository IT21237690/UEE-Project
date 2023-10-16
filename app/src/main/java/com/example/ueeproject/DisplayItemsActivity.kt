package com.example.ueeproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class DisplayItemsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: AuctionItemsAdapter
    private lateinit var itemsList: MutableList<AuctionItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_items)
        recyclerView = findViewById(R.id.recyclerView)

        itemsList = mutableListOf()

        adapter = AuctionItemsAdapter(object : AuctionItemsAdapter.OnItemClickListener {
            override fun onEditClick(position: Int) {
                val clickedItem = adapter.currentList[position]
                val itemId = clickedItem.itemId // Assuming itemId is a property in your AuctionItem class

                // Start EditItemActivity and pass itemId as an extra
                val intent = Intent(this@DisplayItemsActivity, EditItemActivity::class.java)
                intent.putExtra("itemId", itemId)
                startActivityForResult(intent, REQUEST_CODE_EDIT_ITEM)
            }
        })

        recyclerView.adapter = adapter

        // Query Firestore to get items
        db.collection("Auction")
            .get()
            .addOnSuccessListener { documents ->
                val itemsList = mutableListOf<AuctionItem>()
                for (document in documents) {
                    val item = document.toObject(AuctionItem::class.java)
                    itemsList.add(item)
                    Log.d("FirestoreData", "${document.id} => ${document.data}")
                }
                // Update RecyclerView with items
                adapter.submitList(itemsList)
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreData", "Error getting documents: $exception")
                println("Error getting documents: $exception")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_ITEM && resultCode == RESULT_OK) {
            // Handle result from EditItemActivity, for example, refresh the data in your RecyclerView adapter
            // Query the updated data from Firestore and update the RecyclerView
            db.collection("Auction")
                .get()
                .addOnSuccessListener { documents ->
                    val updatedItemsList = mutableListOf<AuctionItem>()
                    for (document in documents) {
                        val item = document.toObject(AuctionItem::class.java)
                        updatedItemsList.add(item)
                    }
                    // Update RecyclerView with updated items
                    adapter.submitList(updatedItemsList)
                }
                .addOnFailureListener { exception ->
                    Log.w("FirestoreData", "Error getting documents: $exception")
                    println("Error getting documents: $exception")
                }
        }
    }

    companion object {
        private const val REQUEST_CODE_EDIT_ITEM = 1
    }
}
