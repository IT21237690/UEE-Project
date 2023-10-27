package com.example.ueeproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.ueeproject.R
//import com.google.android.material.navigation.NavigationBarView
//import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditCompsActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_comps)

        val titleEditText = findViewById<EditText>(R.id.edit_comp_title_edit_text)
        val descriptionEditText = findViewById<EditText>(R.id.edit_comp_desc_edit_text)
        val sellernameEditText = findViewById<EditText>(R.id.edit_comp_seller_edit_text)
        val categoryEditText = findViewById<EditText>(R.id.edit_comp_category_edit_text)
        //val savedAmountEditText = findViewById<EditText>(R.id.edit_comp_saved_amount_edit_text)


        val compId = intent.getStringExtra("Comps_ID")
        if (compId == null) {
            finish()
            return
        }

        db.collection("comps")
            .document(compId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val comp = documentSnapshot.toObject(Comps::class.java)
                if (comp != null) {
                    titleEditText.setText(comp.title)
                    sellernameEditText.setText(comp.sellername.toString())
                    //savedAmountEditText.setText(goal.savedAmount.toString())
                    descriptionEditText.setText(comp.description)
                    categoryEditText.setText(comp.category)


                }
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
        val updateButton = findViewById<Button>(R.id.update_comps_button)
        updateButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val sellername = sellernameEditText.text.toString()
            //val des = descriptionEditText.text.toString()
            val category = categoryEditText.text.toString()
            //val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            if (title.isEmpty() || description == null || sellername == null || category.isEmpty()) {
                Toast.makeText(this, "Please fill in all the fields to update!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }


            db.collection("comps")
                .document(compId)
                .set(Comps(compId, title, description, sellername, category))
                .addOnSuccessListener {
                    // Handle success
                    //toast message
                    Toast.makeText(this, "Complaint Updated successfully", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                }
        }


        val deleteButton = findViewById<Button>(R.id.delete_comps_button)
        deleteButton.setOnClickListener {
            db.collection("comps")
                .document(compId)
                .delete()
                .addOnSuccessListener {
                    // Handle success
                    //toast message
                    Toast.makeText(this, "Complaint Deleted successfully!", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                }
        }
    }
}
