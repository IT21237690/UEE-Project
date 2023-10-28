package com.example.ueeproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class DisplayItemsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: AuctionItemsAdapter
    private lateinit var itemsList: MutableList<AuctionItem>
    private lateinit var userMaleImageView: ImageView
    private lateinit var home: ImageView
    private lateinit var itemsListener: ListenerRegistration
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_items)
        recyclerView = findViewById(R.id.recyclerView)
        userMaleImageView = findViewById(R.id.user_male)
        home = findViewById(R.id.home)

        itemsList = mutableListOf()

        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid

        userMaleImageView.setOnClickListener {
            // Navigate to AddItemActivity when user_male ImageView is clicked
            val intent = Intent(this@DisplayItemsActivity, profileActivity::class.java)
            startActivity(intent)
        }

        home.setOnClickListener {
            // Navigate to AddItemActivity when user_male ImageView is clicked
            val intent = Intent(this@DisplayItemsActivity, HomeActivity::class.java)
            startActivity(intent)
        }

        itemsListener = db.collection("Auction")
            .whereEqualTo("userId", uid) // Filter items by user ID
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("FirestoreData", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val itemsList = mutableListOf<AuctionItem>()
                    for (document in snapshot) {
                        val item = document.toObject(AuctionItem::class.java)
                        itemsList.add(item)
                    }
                    // Update RecyclerView with items
                    adapter.submitList(itemsList)
                } else {
                    Log.d("FirestoreData", "Current data: null")
                }
            }

        adapter = AuctionItemsAdapter(object : AuctionItemsAdapter.OnItemClickListener {


            override fun onEditClick(position: Int) {
                val clickedItem = adapter.currentList[position]
                val itemId = clickedItem.itemId
                val startTime = clickedItem.startTime
                val endTime = clickedItem.endTime

                // Get the current time in milliseconds
                val currentTimeMillis = System.currentTimeMillis()

                if (endTime < currentTimeMillis) {
                    // The auction has ended, fetch the winner's details from Users collection
                    getWinningUserId(itemId, { winningUserId ->
                        // Retrieve winner's details using winningUserId
                        db.collection("users").document(winningUserId)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document != null && document.exists()) {
                                    val winnerName = document.getString("name") ?: "Unknown Winner"
                                    val winnerPhoneNumber = document.getString("phone") ?: "N/A"
                                    showWinnerInfoPopup(winnerName, winnerPhoneNumber)
                                } else {
                                    // Handle the case where the user document doesn't exist
                                    //showErrorPopup("Winner's details not found.")
                                }
                            }
                            .addOnFailureListener { e ->
                                // Handle errors while fetching winner's details
                                //showErrorPopup("Error fetching winner's details: ${e.message}")
                            }
                    }, {
                        // Handle the case where the winning user's ID cannot be retrieved
                       // showErrorPopup("Failed to retrieve winning user's ID.")
                    })
                } else if (startTime > currentTimeMillis) {

                    val intent = Intent(this@DisplayItemsActivity, EditItemActivity::class.java)
                      intent.putExtra("itemId", itemId)
                       startActivityForResult(intent, REQUEST_CODE_EDIT_ITEM)

                } else {
                    // Auction is active, allow the user to edit the item
                    val intent = Intent(this@DisplayItemsActivity, EditItemActivity::class.java)
                    intent.putExtra("itemId", itemId)
                    startActivityForResult(intent, REQUEST_CODE_EDIT_ITEM)
                }
            }




            override fun onDeleteClick(position: Int) {
                val clickedItem = adapter.currentList[position]
                val itemId = clickedItem.itemId
                val startTime =
                    clickedItem.startTime // Assuming startTime is a property in your AuctionItem class

                // Get the current time in milliseconds
                val currentTimeMillis = System.currentTimeMillis()

                if (startTime > currentTimeMillis) {
                    // Delete item from Firestore only if the starting time is greater than current time
                    db.collection("Auction").document(itemId)
                        .delete()
                        .addOnSuccessListener {
                            Log.d("FirestoreData", "DocumentSnapshot successfully deleted!")

                            // Show success message
                            showSuccessPopup()

                            // Refresh the page to show updated data
                            refreshData()
                        }
                        .addOnFailureListener { e ->
                            Log.w("FirestoreData", "Error deleting document", e)

                            // Show error message if deletion fails
                            Toast.makeText(
                                this@DisplayItemsActivity,
                                "Error deleting item: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    // Show a message indicating the item cannot be deleted because the starting time has passed
                    showErrorPopup()
                }
            }


        })

        recyclerView.adapter = adapter

        // Query Firestore to get items
        db.collection("Auction")
            .whereEqualTo("userId", uid)
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


        override fun onDestroy() {
        super.onDestroy()
        // Remove the Firestore snapshot listener when the activity is destroyed
        itemsListener.remove()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun getWinningUserId(itemId: String, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        // Query bids for the specific item
        val db = FirebaseFirestore.getInstance()
        db.collection("Bids")
            .whereEqualTo("itemId", itemId)
            .orderBy("bidAmount", Query.Direction.DESCENDING) // Order by bidAmount in descending order
            .limit(1) // Get the highest bid
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val highestBidInfo = documents.first().toObject(BidInfo::class.java)
                    if (highestBidInfo != null) {
                        val winningUserId = highestBidInfo.UserId
                        // Invoke the success callback with the winning user's ID
                        onSuccess(winningUserId)
                    } else {
                        // Handle the case where the highestBidInfo is null
                        onFailure()
                    }
                } else {
                    // Handle the case where there are no bids for the specific item
                    onFailure()
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors here
                onFailure()
            }
    }



    private fun showWinnerInfoPopup(winnerName: String, winnerPhoneNumber: String) {
        val winnerInfoDialogBuilder = AlertDialog.Builder(this)
            .setTitle("Auction Ended")
            .setMessage("Winner: $winnerName\nPhone Number: $winnerPhoneNumber")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        val winnerInfoDialog = winnerInfoDialogBuilder.create()
        winnerInfoDialog.show()
    }



    private fun showSuccessPopup() {
        val successDialogBuilder = AlertDialog.Builder(this)
            .setTitle("Deleted!")
            .setMessage("Item has been deleted successfully")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        val successDialog = successDialogBuilder.create()
        successDialog.show()
    }

    private fun showErrorPopup() {
        val successDialogBuilder = AlertDialog.Builder(this)
            .setTitle("Error!")
            .setMessage("The item cannot be deleted or edited as the auction has started or ended.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        val successDialog = successDialogBuilder.create()
        successDialog.show()
    }


    private fun refreshData() {
        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid
        // Query Firestore to get updated items
        db.collection("Auction")
            .whereEqualTo("userId", uid)
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
                // Handle error if data retrieval fails
            }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid

        if (requestCode == REQUEST_CODE_EDIT_ITEM && resultCode == RESULT_OK) {

            val newItemAdded = data?.getBooleanExtra("newItemAdded", false) ?: false
            if (newItemAdded) {
                // Refresh the data if a new item was added
                refreshData()
            }
            // Handle result from EditItemActivity, for example, refresh the data in your RecyclerView adapter
            // Query the updated data from Firestore and update the RecyclerView
            db.collection("Auction")
                .whereEqualTo("userId", uid)
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
