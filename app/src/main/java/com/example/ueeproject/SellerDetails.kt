package com.example.ueeproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class SellerDetails : AppCompatActivity() {

    private lateinit var itemNameTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var itemImageView: ImageView
    private lateinit var addressTextView: TextView
    private lateinit var feedbackTextView: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var userMaleImageView: ImageView
    private lateinit var home: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        // Retrieve item ID from the intent extra
        val sellerId = intent.getStringExtra("ITEM_ID")

        itemNameTextView = findViewById(R.id.sellerName)
        descriptionTextView = findViewById(R.id.email)
        priceTextView = findViewById(R.id.sellerContactNo)
        addressTextView = findViewById(R.id.sellerAddress)
        feedbackTextView = findViewById(R.id.sellerRating)
        ratingBar = findViewById(R.id.ratingBar)

        userMaleImageView = findViewById(R.id.user_male)
        home = findViewById(R.id.home)

        userMaleImageView.setOnClickListener {
            // Navigate to AddItemActivity when user_male ImageView is clicked
            val intent = Intent(this@SellerDetails, profileActivity::class.java)
            startActivity(intent)
        }

        home.setOnClickListener {
            // Navigate to AddItemActivity when user_male ImageView is clicked
            val intent = Intent(this@SellerDetails, HomeActivity::class.java)
            startActivity(intent)
        }

        val feedbackButton = findViewById<Button>(R.id.feedbackSubmitButton)
        feedbackButton.setOnClickListener {
            val feedbackRating = ratingBar.rating.toLong() // Get the rating from the RatingBar as a Long
            // Save the feedback rating to the database or perform any other action
            saveFeedbackToDatabase(feedbackRating)
        }

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
                    val feedback = document.getLong("feedback")

                    if (sellerName != null) {
                        itemNameTextView.text = sellerName
                    }

                    if (sellerEmail != null) {
                        descriptionTextView.text = sellerEmail
                    }

                    if (sellerPhone != null) {
                        priceTextView.text = sellerPhone
                    }

                    if (sellerAddress != null) {
                        addressTextView.text = sellerAddress
                    }

                    if (feedback != null) {
                        feedbackTextView.text = feedback.toString()
                    }


                } else {
                    // Document does not exist, handle error or show a message
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors related to fetching item details
            }
    }

    private fun saveFeedbackToDatabase(feedback: Long) {
        // Assuming you have the sellerId from the intent extra
        val sellerId = intent.getStringExtra("ITEM_ID")

        // Save the feedback to the seller's document in the Firestore database
        val db = FirebaseFirestore.getInstance()

        // Fetch the current feedback value from the Firestore document
        db.collection("users").document(sellerId!!)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val currentFeedback = documentSnapshot.getDouble("feedback") ?: 0.0
                    val feedbackCount = documentSnapshot.getLong("feedbackCount") ?: 0

                    // Calculate the new average feedback
                    val newFeedback =
                        ((currentFeedback * feedbackCount) + feedback.toDouble()) / (feedbackCount + 1)

                    // Update feedback and feedback count in the document
                    val feedbackData = hashMapOf(
                        "feedback" to newFeedback,
                        "feedbackCount" to (feedbackCount + 1)
                    )

                    db.collection("users").document(sellerId)
                        .update(feedbackData as Map<String, Any>)
                        .addOnSuccessListener {
                            // Feedback saved successfully
                            showSuccessPopup()
                        }
                        .addOnFailureListener {
                            // Handle errors related to saving feedback
                            Toast.makeText(this, "Failed to save feedback.", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            }
    }

    private fun showSuccessPopup() {
        val successDialogBuilder = AlertDialog.Builder(this)
            .setTitle("Done!")
            .setMessage("Thank You for Your Feedback")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        val successDialog = successDialogBuilder.create()
        successDialog.show()
    }
}
