package com.example.ueeproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class SellItemsDisplay : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: SellItemAdapter
    private lateinit var itemsList: MutableList<AuctionItem>
    private lateinit var userMaleImageView: ImageView
    private lateinit var itemsListener: ListenerRegistration

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell_items_display)
        recyclerView = findViewById(R.id.recyclerView)
        userMaleImageView = findViewById(R.id.user_male)

        itemsList = mutableListOf()

        userMaleImageView.setOnClickListener {
            // Navigate to AddItemActivity when user_male ImageView is clicked
            val intent = Intent(this@SellItemsDisplay, AddItems::class.java)
            startActivity(intent)
        }

        itemsListener = db.collection("Sell Items")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("FirestoreData", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val itemsList = mutableListOf<SellItem>()
                    for (document in snapshot) {
                        val item = document.toObject(SellItem::class.java)
                        itemsList.add(item)
                    }
                    // Update RecyclerView with items
                    adapter.submitList(itemsList)
                } else {
                    Log.d("FirestoreData", "Current data: null")
                }
            }

        adapter = SellItemAdapter(object : SellItemAdapter.OnItemClickListener {
            override fun onEditClick(position: Int) {
                val clickedItem = adapter.currentList[position]
                val itemId = clickedItem.itemId // Assuming itemId is a property in your AuctionItem class

                // Start EditItemActivity and pass itemId as an extra
                val intent = Intent(this@SellItemsDisplay, EditSellItem::class.java)
                intent.putExtra("itemId", itemId)
                startActivityForResult(intent, REQUEST_CODE_EDIT_ITEM)
            }

            override fun onDeleteClick(position: Int) {
                val clickedItem = adapter.currentList[position]
                val itemId = clickedItem.itemId

                // Delete item from Firestore
                db.collection("Sell Items").document(itemId)
                    .delete()
                    .addOnSuccessListener {
                        Log.d("FirestoreData", "DocumentSnapshot successfully deleted!")

                        // Show success message
                        Toast.makeText(this@SellItemsDisplay, "Item successfully deleted!", Toast.LENGTH_SHORT).show()

                        // Refresh the page to show updated data
                        refreshData()
                    }
                    .addOnFailureListener { e ->
                        Log.w("FirestoreData", "Error deleting document", e)

                        // Show error message if deletion fails
                        Toast.makeText(this@SellItemsDisplay, "Error deleting item: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }






        })

        recyclerView.adapter = adapter

        // Query Firestore to get items
        db.collection("Sell Items")
            .get()
            .addOnSuccessListener { documents ->
                val itemsList = mutableListOf<SellItem>()
                for (document in documents) {
                    val item = document.toObject(SellItem::class.java)
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

    override fun onDestroy() {
        super.onDestroy()
        // Remove the Firestore snapshot listener when the activity is destroyed
        itemsListener.remove()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }


    private fun refreshData() {
        // Query Firestore to get updated items
        db.collection("Sell Items")
            .get()
            .addOnSuccessListener { documents ->
                val updatedItemsList = mutableListOf<SellItem>()
                for (document in documents) {
                    val item = document.toObject(SellItem::class.java)
                    updatedItemsList.add(item)
                }
                // Update RecyclerView with updated items
                adapter.submitList(updatedItemsList)
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreData", "Error getting documents: $exception")
                // Handle error if data retrieval fails
            }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_ITEM && resultCode == RESULT_OK) {

            val newItemAdded = data?.getBooleanExtra("newItemAdded", false) ?: false
            if (newItemAdded) {
                // Refresh the data if a new item was added
                refreshData()
            }
            // Handle result from EditItemActivity, for example, refresh the data in your RecyclerView adapter
            // Query the updated data from Firestore and update the RecyclerView
            db.collection("Sell Items")
                .get()
                .addOnSuccessListener { documents ->
                    val updatedItemsList = mutableListOf<SellItem>()
                    for (document in documents) {
                        val item = document.toObject(SellItem::class.java)
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
