package com.example.ueeproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class ItemDetailsActivity : AppCompatActivity() {

    private lateinit var itemNameTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var itemImageView: ImageView
    private lateinit var sellerNameTextView: TextView
    private lateinit var sellerEmailTextView: TextView
    private lateinit var sellerphoneTextView: TextView
    private lateinit var userMaleImageView: ImageView
    private lateinit var home: ImageView

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_details)

        // Retrieve item ID from the intent extra
        val itemId = intent.getStringExtra("ITEM_ID")

        itemNameTextView = findViewById(R.id.itemNameTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        priceTextView = findViewById(R.id.priceTextView)
        itemImageView = findViewById(R.id.itemImageView)
        sellerNameTextView = findViewById(R.id.sellerNameTextView)
        sellerEmailTextView = findViewById(R.id.sellerEmailTextView)
        sellerphoneTextView = findViewById(R.id.sellerphoneTextView)
        userMaleImageView = findViewById(R.id.user_male)
        home = findViewById(R.id.home)

        userMaleImageView.setOnClickListener {
            // Navigate to AddItemActivity when user_male ImageView is clicked
            val intent = Intent(this@ItemDetailsActivity, profileActivity::class.java)
            startActivity(intent)
        }

        home.setOnClickListener {
            // Navigate to AddItemActivity when user_male ImageView is clicked
            val intent = Intent(this@ItemDetailsActivity, HomeActivity::class.java)
            startActivity(intent)
        }

        // Sample code (assuming you have a Firestore collection named "AddItems")
        val db = FirebaseFirestore.getInstance()
        // Fetch item details based on item ID
        db.collection("Sell Items").document(itemId!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Document exists, fetch item details and sellerId
                    val itemName = document.getString("itemName")
                    val itemDescription = document.getString("description")
                    val itemPrice = document.getString("price")
                    val imageUrl = document.getString("imageUrl")
                    val sellerId = document.getString("sellerId")

                    // Update UI with fetched item details
                    itemNameTextView.text = "Item Name: $itemName"
                    descriptionTextView.text = "Description: $itemDescription"
                    priceTextView.text = "Price: $itemPrice"

                    // Load the image using Glide
                    Glide.with(this)
                        .load(imageUrl) // Assuming "imageUrl" is the field name in your Firestore document
                        .placeholder(R.drawable.ic_placeholder_image) // Placeholder image while loading
                        .error(R.drawable.ic_error_image) // Error image if Glide fails to load the image
                        .into(itemImageView)

                    // Fetch and display seller details based on sellerId from AddItems document
                    db.collection("users").document(sellerId!!)
                        .get()
                        .addOnSuccessListener { sellerDocument ->
                            if (sellerDocument != null && sellerDocument.exists()) {
                                // Seller document exists, fetch seller details and update UI
                                val sellerName = sellerDocument.getString("name")
                                val sellerEmail = sellerDocument.getString("email")
                                val sellerPhone = sellerDocument.getString("phone")

                                // Update UI with fetched seller details
                                sellerNameTextView.text = "Seller Name: $sellerName"
                                sellerEmailTextView.text = "Seller Email: $sellerEmail"
                                sellerphoneTextView.text = "Contact Number: $sellerPhone"
                            } else {
                                // Seller document does not exist, handle error or show a message
                                sellerNameTextView.text = "Seller Name: Not Available"
                                sellerEmailTextView.text = "Seller Email: Not Available"
                                Log.e(
                                    "SellerDetails",
                                    "Seller document does not exist for sellerId: $sellerId"
                                )
                            }
                        }
                        .addOnFailureListener { sellerException ->
                            // Handle errors related to fetching seller details
                            sellerNameTextView.text = "Seller Name: Not Available"
                            sellerEmailTextView.text = "Seller Email: Not Available"
                            Log.e(
                                "SellerDetails",
                                "Error fetching seller details: $sellerException"
                            )
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
