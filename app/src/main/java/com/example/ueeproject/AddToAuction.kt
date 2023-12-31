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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID


class  AddToAuction : AppCompatActivity() {

    private val REQUEST_CODE_DISPLAY_ITEMS = 1
    private lateinit var imageView: ImageView
    private lateinit var itemNameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var startTimeEditText: EditText
    private lateinit var endTimeEditText: EditText
    private lateinit var saveButton: Button
    private val db = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference
    private var imageUri: Uri? = null
    val calendar = Calendar.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var userMaleImageView: ImageView
    private lateinit var home: ImageView




    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            imageUri = data?.data
            imageView.setImageURI(imageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addtoauction)


        imageView = findViewById(R.id.imageView)
        itemNameEditText = findViewById(R.id.editItemNameEditText)
        descriptionEditText = findViewById(R.id.editDescriptionEditText)
        priceEditText = findViewById(R.id.editPriceEditText)
        startTimeEditText = findViewById(R.id.editTextStartTime)
        endTimeEditText = findViewById(R.id.editTextEndTime)
        saveButton = findViewById(R.id.saveButton)

        userMaleImageView = findViewById(R.id.user_male)
        home = findViewById(R.id.home)

        userMaleImageView.setOnClickListener {
            // Navigate to AddItemActivity when user_male ImageView is clicked
            val intent = Intent(this@AddToAuction, profileActivity::class.java)
            startActivity(intent)
        }

        home.setOnClickListener {
            // Navigate to AddItemActivity when user_male ImageView is clicked
            val intent = Intent(this@AddToAuction, HomeActivity::class.java)
            startActivity(intent)
        }

        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid

        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getContent.launch(intent)
        }

        val startTimeDatePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                // Set the selected date to the calendar instance
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Show time picker dialog after selecting the date
                showTimePickerDialog(calendar, startTimeEditText)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        val endTimeDatePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                // Set the selected date to the calendar instance
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Show time picker dialog after selecting the date
                showTimePickerDialog(calendar, endTimeEditText)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

// Set click listeners for the start and end time EditText fields
        startTimeEditText.setOnClickListener {
            startTimeDatePickerDialog.show()
        }

        endTimeEditText.setOnClickListener {
            endTimeDatePickerDialog.show()
        }




        saveButton.setOnClickListener {
            try {
                val itemName = itemNameEditText.text.toString()
                val description = descriptionEditText.text.toString()
                val price = priceEditText.text.toString()
                val userId = uid

                // Check if start and end times are not blank
                val startTimeText = startTimeEditText.text.toString()
                val endTimeText = endTimeEditText.text.toString()

                if (itemName.isNotBlank() && description.isNotBlank() && price.isNotBlank() && startTimeText.isNotBlank() && endTimeText.isNotBlank() && imageUri != null) {
                    // Get current time as timestamp
                    val currentTime = System.currentTimeMillis()

                    // Convert start and end times to timestamps (Long values)
                    val startTime = convertToTimestamp(startTimeText)
                    val endTime = convertToTimestamp(endTimeText)

                    // Validate if the conversion was successful and times are not before current time
                    if (startTime != null && endTime != null && startTime >= currentTime && endTime >= currentTime) {
                        // Call your uploadImageToStorage function with startTime and endTime as Long values
                        if (userId != null) {
                            uploadImageToStorage(itemName, description, price, startTime, endTime,userId)
                        }

                        showSuccessPopup()

                        // Start DisplayItemsActivity after uploading data
                        val intent = Intent()
                        intent.putExtra("newItemAdded", true)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else {
                        showErrorPopup()
                    }
                } else {
                    showToast("Please fill out all fields and select an image.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception (e.g., show an error message) to prevent the app from crashing
            }
        }


    }

    private fun showErrorPopup() {
        val successDialogBuilder = AlertDialog.Builder(this)
            .setTitle("Error!")
            .setMessage("Please add Future time")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        val successDialog = successDialogBuilder.create()
        successDialog.show()
    }

    private fun showSuccessPopup() {
        val successDialogBuilder = AlertDialog.Builder(this)
            .setTitle("Success!")
            .setMessage("Added Successfully.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        val successDialog = successDialogBuilder.create()
        successDialog.show()
    }

    private fun convertToTimestamp(dateTimeString: String): Long? {
        return try {
            val inputDateFormat = SimpleDateFormat("EEE, MMM dd, yyyy hh:mm a", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

            val parsedDate = inputDateFormat.parse(dateTimeString)
            val formattedDate = outputDateFormat.format(parsedDate!!)

            // Parse the formatted date into a Long timestamp
            outputDateFormat.parse(formattedDate)?.time
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showTimePickerDialog(calendar: Calendar, editText: EditText) {
        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                // Format the selected date and time and set it to the EditText
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                val selectedDateTime = SimpleDateFormat("EEE, MMM dd, yyyy hh:mm a", Locale.getDefault())
                    .format(calendar.time)

                editText.setText(selectedDateTime)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )

        timePickerDialog.show()
    }





    private fun uploadImageToStorage(
        itemName: String,
        description: String,
        price: String,
        startTime: Long,
        endTime: Long,
        userId:String,
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
                        startTime,
                        endTime,
                        imageUrl,
                        userId
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
        startTime: Long,
        endTime: Long,
        imageUrl: String,
        userId :String
    ) {
        // Generate a unique itemId
        val itemId = UUID.randomUUID().toString()

        val data = hashMapOf(
            "itemId" to itemId,
            "itemName" to itemName,
            "description" to description,
            "price" to price,
            "startTime" to startTime,
            "endTime" to endTime,
            "imageUrl" to imageUrl,
            "userId" to userId
        )

        // Use the generated itemId as the document ID
        db.collection("Auction").document(itemId)
            .set(data)
            .addOnSuccessListener {
                // Clear input fields after successful save
                itemNameEditText.text.clear()
                descriptionEditText.text.clear()
                priceEditText.text.clear()
                startTimeEditText.text.clear()
                endTimeEditText.text.clear()
                imageView.setImageResource(R.drawable.ic_placeholder_image) // Set a placeholder image after upload

                println("Document added with ID: $itemId")
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }

/*
*input is not valid if
*..itemName is empty
* ..description is empty
* ..price is empty
* .. start time and end tie is empty
* ..item name contains less than 5 charcters
* ..description contains less than 20 characters
* ..price contains less than 3 digits
* ..start and end date and time contain less than 14 digits
* ..start and end date and time contain more than 14 digits
*/
    companion object {
        fun validateAuctionInput(
            itemName: String,
            description: String,
            price: String,
            startTime: String,
            endTime: String,

            ): Boolean {
            if (itemName.isEmpty() || description.isEmpty() || price.isEmpty() || startTime.isEmpty()||endTime.isEmpty()) {
                return false
            }
            if (itemName.count { it.isLetter() } < 5) {
                return false
            }
            if (description.count { it.isLetter() } < 20) {
                return false
            }
            if (price.count { it.isDigit() } < 4) {
                return false
            }
            if (startTime.count { it.isDigit() } < 14 ||endTime.count { it.isDigit() } < 14 ) {
                return false
            }
            return true
        }
    }


}