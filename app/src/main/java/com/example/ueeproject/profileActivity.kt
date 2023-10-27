package com.example.ueeproject

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        binding.viewDetailsLayout.setOnClickListener {
            startActivity(
                Intent(this, ViewDetailsActivity::class.java)
            )
        }
        binding.updateUserDetailsSection.setOnClickListener {
            startActivity(
                Intent(this, UpdateUserDetailsActivity::class.java)
            )
        }

        binding.auctionIconlayout.setOnClickListener {
            startActivity(
                Intent(this, DisplayItemsActivity::class.java)
            )
        }


        binding.homeSection.setOnClickListener {
            startActivity(
                Intent(this, HomeActivity::class.java)
            )
        }

        showName = findViewById(R.id.profileNameHeading)
        showEmail = findViewById(R.id.profileEmailHeading)



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