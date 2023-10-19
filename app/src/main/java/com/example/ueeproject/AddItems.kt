package com.example.ueeproject

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID


class  AddItems : AppCompatActivity() {


    private val REQUEST_CODE_DISPLAY_ITEMS = 1
    private lateinit var imageView: ImageView
    private lateinit var itemNameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var saveButton: Button
    private val db = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference
    private var imageUri: Uri? = null
    val calendar = Calendar.getInstance()




    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            imageUri = data?.data
            imageView.setImageURI(imageUri)
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_additem)


        imageView = findViewById(R.id.imageView2)
        itemNameEditText = findViewById(R.id.addItemName)
        descriptionEditText = findViewById(R.id.addItemDescription)
        priceEditText = findViewById(R.id.addItemPrice)
        saveButton = findViewById(R.id.buttonAdd)

        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getContent.launch(intent)
        }

        saveButton.setOnClickListener {
            try {
                val itemName = itemNameEditText.text.toString()
                val description = descriptionEditText.text.toString()
                val price = priceEditText.text.toString()



                if (itemName.isNotBlank() && description.isNotBlank() && price.isNotBlank()  && imageUri != null) {
                    // Convert start and end times to timestamps (Long values)


                    // Validate if the conversion was successful

                        // Call your uploadImageToStorage function with startTime and endTime as Long values
                        uploadImageToStorage(itemName, description, price)

                        // Start DisplayItemsActivity after uploading data
                        val intent = Intent()
                        intent.putExtra("newItemAdded", true)
                        setResult(Activity.RESULT_OK, intent)
                        finish()

                } else {
                    showToast("Please fill out all fields and select an image.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception (e.g., show an error message) to prevent the app from crashing
            }
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun uploadImageToStorage(
        itemName: String,
        description: String,
        price: String,

    ) {
        val imageFileName = "${UUID.randomUUID()}.jpg"
        val itemId = UUID.randomUUID().toString() // Generate a unique ID
        val imagesRef = storageRef.child("images/$imageFileName")

        imagesRef.putFile(imageUri!!)
            .addOnSuccessListener {
                imagesRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    saveTextToFirestore(
                        itemName,
                        description,
                        price,
                        imageUrl
                    )
                }
            }
            .addOnFailureListener { e ->
                println("Error uploading image: $e")
                // Handle upload failure, show an error message or toast
            }
    }


    private fun saveTextToFirestore(
        itemName: String,
        description: String,
        price: String,
        imageUrl: String
    ) {
        // Generate a unique itemId
        val itemId = UUID.randomUUID().toString()

        val data = hashMapOf(
            "itemId" to itemId,
            "itemName" to itemName,
            "description" to description,
            "price" to price,
            "imageUrl" to imageUrl
        )

        // Use the generated itemId as the document ID
        db.collection("Sell Items").document(itemId)
            .set(data)
            .addOnSuccessListener {
                // Clear input fields after successful save
                itemNameEditText.text.clear()
                descriptionEditText.text.clear()
                priceEditText.text.clear()
                imageView.setImageResource(R.drawable.ic_placeholder_image) // Set a placeholder image after upload

                println("Document added with ID: $itemId")
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }


}