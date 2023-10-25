package com.example.comps

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ueeproject.R
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class CreateCompsActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        val titleEditText = findViewById<EditText>(R.id.title_edit_text)
        val descriptionEditText = findViewById<EditText>(R.id.description_edit_text)
        val sellernameEditText = findViewById<EditText>(R.id.sellername_edit_text)
        val categoryEditText = findViewById<EditText>(R.id.category_edit_text)
        //var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

        val createButton = findViewById<Button>(R.id.create_button)
        createButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val sellername = sellernameEditText.text.toString()
            val category = categoryEditText.text.toString()
            //val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (title.isEmpty() || description == null || sellername.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a new goal with the given data
            val comp = hashMapOf(
                //"userId" to userId,
                "title" to title,
                "description" to description,
                "sellername" to sellername,
                "category" to category,
                "created_at" to Calendar.getInstance().timeInMillis
            )

            // Add the goal to the "goals" collection in Firestore
            db.collection("comps")
                .add(comp)
                .addOnSuccessListener {
                    // Display a success message or go back to the previous screen
                    Toast.makeText(this, "Complaint added successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    // Display an error message
                    TODO()
                }
        }
    }
}