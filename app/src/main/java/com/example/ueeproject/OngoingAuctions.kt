package com.example.ueeproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class OngoingAuctions : AppCompatActivity(), OngoingAuctionsAdapter.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ongoing_auctions)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

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
                // Create and set the adapter for RecyclerView
                val adapter = OngoingAuctionsAdapter(itemsList, this)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreData", "Error getting documents: $exception")
                // Handle error if data retrieval fails
            }
    }

    override fun onItemClick(item: AuctionItem) {
        val itemId = item.itemId
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_bid_amount, null)
        val editTextBidAmount: EditText = dialogView.findViewById(R.id.editTextBidAmount)
        val buttonSubmitBid: Button = dialogView.findViewById(R.id.buttonSubmitBid)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Enter Bid Amount")
            .setCancelable(true)

        val dialog = dialogBuilder.create()
        dialog.show()

        buttonSubmitBid.setOnClickListener {
            val bidAmount = editTextBidAmount.text.toString()
            if (bidAmount.isNotEmpty()) {
                val bidAmountValue = bidAmount.toDouble() // Convert bid amount to a numerical value if needed

                // Save bid amount and item ID to a separate collection in Firestore
                val db = FirebaseFirestore.getInstance()
                val bidData = hashMapOf(
                    "itemId" to itemId,
                    "bidAmount" to bidAmountValue
                )

                db.collection("Bids")
                    .add(bidData)
                    .addOnSuccessListener { documentReference ->
                        Log.d("Firestore", "Bid saved with ID: ${documentReference.id}")
                        // Handle success, e.g., show a success message to the user
                        showSuccessPopup()
                        // Dismiss the input dialog after successful completion of Firestore operation
                        dialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error adding bid", e)
                        // Handle error, e.g., show an error message to the user
                        // Dismiss the input dialog in case of an error
                        dialog.dismiss()
                    }
            } else {
                // Handle empty bid amount (show a message, error, etc.)
                // You might want to display an error message to the user
                editTextBidAmount.error = "Bid amount cannot be empty"
            }
        }
    }

    private fun showSuccessPopup() {
        val successDialogBuilder = AlertDialog.Builder(this)
            .setTitle("Success!")
            .setMessage("Your bid has been successfully placed.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        val successDialog = successDialogBuilder.create()
        successDialog.show()
    }








    companion object {
        private const val REQUEST_CODE_EDIT_ITEM = 1
    }
}
