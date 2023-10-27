package com.example.ueeproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.ueeproject.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var showName: TextView

    private val db = Firebase.firestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showName = findViewById(R.id.homeUserNameHeading)

        binding.profileButton.setOnClickListener {
            startActivity(
                Intent(this, profileActivity::class.java)
            )
        }

        binding.homeButton.setOnClickListener {
            startActivity(
                Intent(this, HomeActivity::class.java)
            )
        }

        binding.textView6.setOnClickListener {
            startActivity(
                Intent(this, DisplaysellingItemsToBuyers::class.java)
            )
        }

        binding.sellersiconlayout.setOnClickListener {
            startActivity(
                Intent(this, DisplayAllSellers::class.java)
            )
        }

        binding.auctioniconlayout.setOnClickListener {
            startActivity(
                Intent(this, OngoingAuctions::class.java)
            )
        }

        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid

        val docRef = db.collection("users").document(uid!!)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val name = document.data!!["name"].toString()

                    showName.text = name
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }


    }
}