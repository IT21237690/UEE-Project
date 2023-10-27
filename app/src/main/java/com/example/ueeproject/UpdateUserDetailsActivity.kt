package com.example.ueeproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.ueeproject.databinding.ActivityUpdateUserDetailsBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UpdateUserDetailsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_user_details)

        val updateName: EditText = findViewById(R.id.updateName)
        val updateEmail: EditText = findViewById(R.id.updateEmail)

        val EditDetails: Button = findViewById(R.id.EditDetails)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        db.collection("users")
            .document(auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { document ->
                if(document != null) {
                    val name = document.getString("name")
                    val email = document.getString("email")

                    updateName.setText(name)
                    updateEmail.setText(email)

                }else{
                    Log.d("EditProfile", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("EditProfile", "get failed with ", exception)
            }

        EditDetails.setOnClickListener {
            val newName = updateName.text.toString().trim()
            val newEmail = updateEmail.text.toString().trim()

            if (newName.isEmpty() || newEmail.isEmpty()){
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!isValidEmail(newEmail)) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            //Update user name
            db.collection("users")
                .document(auth.currentUser!!.uid)
                .update("name", newName)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                    startActivity(
                        Intent(this, Profile::class.java)
                    )
                }
                .addOnFailureListener { exception ->
                    Log.d("EditProfile", "update failed with ", exception)
                }

            // Update the user's email address.
            FirebaseAuth.getInstance().currentUser?.updateEmail(newEmail)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // The user's email address has been updated successfully.
                    Toast.makeText(this, "Your email address has been updated successfully.", Toast.LENGTH_SHORT).show()
                } else {
                    // The user's email address could not be updated.
                    Toast.makeText(this, "An error occurred while updating your email address.", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}