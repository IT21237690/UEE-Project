package com.example.ueeproject


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditSellItem : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var itemId: String

    private lateinit var updatedItemNameEditText: EditText
    private lateinit var updatedDescriptionEditText: EditText
    private lateinit var updatedPriceEditText: EditText

    private lateinit var imageView: ImageView

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_sell_item)

        itemId = intent.getStringExtra("itemId") ?: ""

        updatedItemNameEditText = findViewById(R.id.editItemNameEditText)
        updatedDescriptionEditText = findViewById(R.id.editDescriptionEditText)
        updatedPriceEditText = findViewById(R.id.editPriceEditText)
        imageView = findViewById(R.id.imageView)

        val chooseImageButton: ImageView = findViewById(R.id.imageView)
        chooseImageButton.setOnClickListener {
            chooseImage()
        }

        val updateButton: Button = findViewById(R.id.saveButton)
        updateButton.setOnClickListener {
            updateItemDetails()
        }

        // Retrieve item details from Firestore and populate the UI
        val itemId = intent.getStringExtra("itemId")
        if (itemId != null) {
            db.collection("Sell Items").document(itemId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val imageUrl = document.getString("imageUrl")
                        val itemName = document.getString("itemName")
                        val description = document.getString("description")
                        val price = document.getString("price")

                        // Populate UI elements with retrieved data
                        updatedItemNameEditText.setText(itemName)
                        updatedDescriptionEditText.setText(description)
                        updatedPriceEditText.setText(price)
//                        updatedStartTimeEditText.setText(startTime.toString())
//                        updatedEndTimeEditText.setText(endTime.toString())

                        // Load image into ImageView using Glide
                        Glide.with(this@EditSellItem)
                            .load(imageUrl)
                            .into(imageView)
                    } else {
                        // Handle the case where the document does not exist
                        Toast.makeText(this@EditSellItem, "Item not found!", Toast.LENGTH_SHORT).show()
                        finish() // Close the activity if the item is not found
                    }
                }
                .addOnFailureListener { e ->
                    // Handle errors while retrieving item details
                    Toast.makeText(this@EditSellItem, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity on error
                }
        } else {
            // Handle the case where itemId is null
            Toast.makeText(this@EditSellItem, "Invalid item ID!", Toast.LENGTH_SHORT).show()
            finish() // Close the activity if itemId is null
        }

        // Your code to retrieve item details from Firestore and populate the UI goes here
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            // Load the selected image into ImageView using Glide
            Glide.with(this)
                .load(imageUri)
                .into(imageView)
        }
    }

    private fun updateItemDetails() {
        val updatedItemName = updatedItemNameEditText.text.toString()
        val updatedDescription = updatedDescriptionEditText.text.toString()
        val updatedPrice = updatedPriceEditText.text.toString()


        // Check if the updated item name and description are not empty
        if (updatedItemName.isNotEmpty() && updatedDescription.isNotEmpty()) {
            // Upload image to Firebase Storage if imageUri is not null
            if (imageUri != null) {
                val storageRef = FirebaseStorage.getInstance().reference.child("item_images/${itemId}")
                val uploadTask = storageRef.putFile(imageUri!!)

                uploadTask.addOnSuccessListener { taskSnapshot ->
                    // Image uploaded successfully, get the download URL
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()

                        // Update the Firestore document with the new image URL and other fields
                        db.collection("Sell Items").document(itemId)
                            .update(
                                mapOf(
                                    "itemName" to updatedItemName,
                                    "description" to updatedDescription,
                                    "price" to updatedPrice,

                                    "imageUrl" to imageUrl
                                )
                            )
                            .addOnSuccessListener {
                                // Document updated successfully
                                updatedItemNameEditText.setText(updatedItemName)
                                updatedDescriptionEditText.setText(updatedDescription)
                                updatedPriceEditText.setText(updatedPrice)

                                Toast.makeText(
                                    this,
                                    "Item details updated successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Navigate back to DisplayItemsActivity
                                val intent = Intent(this, SellItemsDisplay::class.java)
                                startActivity(intent)
                                finish() // Close the current activity
                            }
                            .addOnFailureListener { e ->
                                // Handle the error if updating item details fails
                                Toast.makeText(
                                    this,
                                    "Error updating item details: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            } else {
                // Update the Firestore document without changing the image URL
                db.collection("Sell Items").document(itemId)
                    .update(
                        mapOf(
                            "itemName" to updatedItemName,
                            "description" to updatedDescription,
                            "price" to updatedPrice,

                        )
                    )
                    .addOnSuccessListener {
                        // Document updated successfully
                        updatedItemNameEditText.setText(updatedItemName)
                        updatedDescriptionEditText.setText(updatedDescription)
                        updatedPriceEditText.setText(updatedPrice)


                        Toast.makeText(
                            this,
                            "Item details updated successfully!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navigate back to DisplayItemsActivity
                        val intent = Intent(this, SellItemsDisplay::class.java)
                        startActivity(intent)
                        finish() // Close the current activity
                    }
                    .addOnFailureListener { e ->
                        // Handle the error if updating item details fails
                        Toast.makeText(
                            this,
                            "Error updating item details: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        } else {
            Toast.makeText(
                this,
                "Item name and description cannot be empty!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
