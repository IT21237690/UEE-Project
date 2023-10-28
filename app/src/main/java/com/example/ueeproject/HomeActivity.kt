package com.example.ueeproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ueeproject.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var showName: TextView
    private lateinit var recyclerview: RecyclerView
    private lateinit var userList: ArrayList<User>

    private var db = Firebase.firestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showName = findViewById(R.id.homeUserNameHeading)
        recyclerview = findViewById(R.id.recyclerView)
        recyclerview.layoutManager = LinearLayoutManager(this)

        userList = arrayListOf()

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


        binding.complainViewCard.setOnClickListener {
            startActivity(
                Intent(this, CurrentCompsActivity::class.java)
            )
        }

        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid

        db.collection("Sell Items").get()
            .addOnSuccessListener {

                if(!it.isEmpty) {
                    for (data in it.documents) {
                        val user: User? = data.toObject(User::class.java)
                        if(user != null){
                            userList.add(user)
                        }
                    }
                    recyclerview.adapter = MyAdapter(userList)
                }

            }
            .addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }

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