package com.example.ueeproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ueeproject.databinding.ActivityOngoingAuctionsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class OngoingAuctions : AppCompatActivity(), OngoingAuctionsAdapter.OnItemClickListener {

    private lateinit var binding: ActivityOngoingAuctionsBinding
    private lateinit var recyclerView: RecyclerView

    private val db = FirebaseFirestore.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userMaleImageView: ImageView
    private lateinit var home: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ongoing_auctions)
        userMaleImageView = findViewById(R.id.user_male)
        home = findViewById(R.id.home)





        binding = ActivityOngoingAuctionsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.AddButton.setOnClickListener {
            startActivity(
                Intent(this, AddToAuction::class.java)
            )
        }




        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        userMaleImageView.setOnClickListener {
            // Navigate to AddItemActivity when user_male ImageView is clicked
            val intent = Intent(this@OngoingAuctions, profileActivity::class.java)
            startActivity(intent)
        }

        home.setOnClickListener {
            // Navigate to AddItemActivity when user_male ImageView is clicked
            val intent = Intent(this@OngoingAuctions, HomeActivity::class.java)
            startActivity(intent)
        }


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
        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid

        // Reference to Firestore database
        val db = FirebaseFirestore.getInstance()

        // Query to get the highest bid for the current item
        db.collection("Bids")
            .whereEqualTo("itemId", itemId)
            .orderBy("bidAmount", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val highestBidAmount = documents.documents[0].get("bidAmount") as Double
                    showBidInputDialog(itemId, uid, highestBidAmount)
                } else {
                    // If no bids exist for the item, show the bid input dialog with bidAmount as 0.0
                    showBidInputDialog(itemId, uid, 0.0)
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error getting highest bid", e)
                // Handle error, e.g., show an error message to the user
            }
    }

    private fun showBidInputDialog(itemId: String, uid: String?, highestBidAmount: Double) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_bid_amount, null)
        val textViewCurrentBid: TextView = dialogView.findViewById(R.id.textViewCurrentBid)
        val editTextBidAmount: EditText = dialogView.findViewById(R.id.editTextBidAmount)
        val buttonSubmitBid: Button = dialogView.findViewById(R.id.buttonSubmitBid)

        // Display the current highest bid
        textViewCurrentBid.text = "Current Highest Bid: $highestBidAmount"

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Enter Bid Amount")
            .setCancelable(true)

        val dialog = dialogBuilder.create()
        dialog.show()

        buttonSubmitBid.setOnClickListener {
            val bidAmount = editTextBidAmount.text.toString()
            if (bidAmount.isNotEmpty()) {
                val bidAmountValue = bidAmount.toDouble()

                // Save bid amount and item ID to the Bids collection in Firestore
                val bidData = hashMapOf(
                    "itemId" to itemId,
                    "UserId" to uid,
                    "bidAmount" to bidAmountValue,
                    "isWinner" to ""
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
