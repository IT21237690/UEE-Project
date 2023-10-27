package com.example.ueeproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.ueeproject.databinding.ActivityViewDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ViewDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewDetailsBinding

    private lateinit var showName: TextView
    private lateinit var showEmail: TextView
    private lateinit var showAddress: TextView
    private lateinit var showPhone: TextView

    private val db = Firebase.firestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showName = findViewById(R.id.showName)
        showEmail = findViewById(R.id.showEmail)
        showAddress = findViewById(R.id.showAddress)
        showPhone = findViewById(R.id.showPhone)

        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid

        val docRef = db.collection("users").document(uid!!)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val name = document.data!!["name"].toString()
                    val email = document.data!!["email"].toString()
                    val address = document.data!!["address"].toString()
                    val phone = document.data!!["phone"].toString()

                    showName.text = name
                    showEmail.text = email
                    showAddress.text = address
                    showPhone.text = phone
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }

    }
}