package com.example.ueeproject

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class SellerDetails : AppCompatActivity() {

    private lateinit var itemNameTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var itemImageView: ImageView
    private lateinit var  AddressTextVIew : TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_details)

        // Retrieve item ID from the intent extra
        val sellerId = intent.getStringExtra("ITEM_ID")

        itemNameTextView = findViewById(R.id.itemNameTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        priceTextView = findViewById(R.id.priceTextView)
        AddressTextVIew = findViewById(R.id.AddressTextVIew)

        // Sample code (assuming you have a Firestore collection named "AddItems")
        val db = FirebaseFirestore.getInstance()
        // Fetch item details based on item ID
        db.collection("users").document(sellerId!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Document exists, fetch item details and sellerId
                    val sellerName = document.getString("name")
                    val sellerEmail = document.getString("email")
                    val sellerPhone = document.getString("phone")
                    val sellerAddress = document.getString("address")


                    // Update UI with fetched item details
                    itemNameTextView.text = "Seller Name: $sellerName"
                    descriptionTextView.text = "Email: $sellerEmail"
                    priceTextView.text = "Phone Number: $sellerPhone"
                    AddressTextVIew.text = "Address: $sellerAddress"



                    // Fetch and display seller details based on sellerId from AddItems document
                    db.collection("users").document(sellerId!!)
                        .get()
                        .addOnSuccessListener { sellerDocument ->
                             sellerDocument != null && sellerDocument.exists()
                                // Seller document exists, fetch seller details and update UI
//                                val sellerName = sellerDocument.getString("name")
//                                val sellerEmail = sellerDocument.getString("email")
//                                val sellerPhone = sellerDocument.getString("phone")
//                                val sellerAddress = sellerDocument.getString("address")



                        }
                        .addOnFailureListener { sellerException ->
                            // Handle errors related to fetching seller details


                        }
                } else {
                    // Document does not exist, handle error or show a message
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors related to fetching item details
            }
    }
}
