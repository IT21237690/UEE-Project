package com.example.ueeproject

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BiddedItemsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BiddedItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bidded_items)

        recyclerView = findViewById(R.id.biddedItemsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        val db = FirebaseFirestore.getInstance()
        db.collection("Bids")
            .whereEqualTo("UserId", currentUserUid)
            .get()
            .addOnSuccessListener { documents ->
                val bidList = mutableListOf<BidInfo>()
                for (document in documents) {
                    val bidItem = document.toObject(BidInfo::class.java)
                    bidList.add(bidItem)
                }

                // Initialize and set up the adapter for the RecyclerView
                adapter = BiddedItemsAdapter(bidList) { bidInfo ->
                    fetchSellerDetails(bidInfo.itemId)
                }

                recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }

    private fun fetchSellerDetails(itemId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Auction").document(itemId)
            .get()
            .addOnSuccessListener { auctionDocument ->
                val sellerId = auctionDocument.getString("userId")

                // Fetch seller's details from Users collection using sellerId
                if (sellerId != null) {
                    db.collection("users").document(sellerId)
                        .get()
                        .addOnSuccessListener { userDocument ->
                            val sellerName = userDocument.getString("name")
                            val sellerAddress = userDocument.getString("address")
                            val sellerPhoneNumber = userDocument.getString("phone")

                            // Display seller's details in a popup
                            showSellerDetailsPopup(
                                sellerName,
                                sellerAddress,
                                sellerPhoneNumber
                            )
                        }
                }
            }
    }

    private fun showSellerDetailsPopup(sellerName: String?, sellerAddress: String?, sellerPhoneNumber: String?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_seller_details, null)
        val nameTextView: TextView = dialogView.findViewById(R.id.nameTextView)
        val addressTextView: TextView = dialogView.findViewById(R.id.addressTextView)
        val phoneNumberTextView: TextView = dialogView.findViewById(R.id.phoneNumberTextView)

        // Set seller's details in the popup
        nameTextView.text = "Seller Name: $sellerName"
        addressTextView.text = "Address: $sellerAddress"
        phoneNumberTextView.text = "Phone Number: $sellerPhoneNumber"

        // Create and show the popup window
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)

        val dialog = dialogBuilder.create()
        dialog.show()
    }
}
