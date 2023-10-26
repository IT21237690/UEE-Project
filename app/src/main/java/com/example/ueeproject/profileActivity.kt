package com.example.ueeproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.ueeproject.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class profileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private lateinit var showName: TextView
    private lateinit var showEmail: TextView

    private val db = Firebase.firestore
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showName = findViewById(R.id.profileNameHeading)
        showEmail = findViewById(R.id.profileEmailHeading)


        binding.auctionIconlayout.setOnClickListener {
            startActivity(
                Intent(this, DisplayItemsActivity::class.java)
            )
        }

        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid

        val docRef = db.collection("users").document(uid!!)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val name = document.data!!["name"].toString()
                    val email = document.data!!["email"].toString()

                    showName.text = name
                    showEmail.text = email
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
    }
}