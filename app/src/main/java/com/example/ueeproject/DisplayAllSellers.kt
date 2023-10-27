package com.example.ueeproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class DisplayAllSellers : AppCompatActivity(), sellersadapter.OnItemClickListener {
    private lateinit var userMaleImageView: ImageView
    private lateinit var home: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_sellers)

        userMaleImageView = findViewById(R.id.user_male)
        home = findViewById(R.id.home)

        // Initialize RecyclerView and its adapter
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        userMaleImageView.setOnClickListener {
            // Navigate to AddItemActivity when user_male ImageView is clicked
            val intent = Intent(this@DisplayAllSellers, profileActivity::class.java)
            startActivity(intent)
        }

        home.setOnClickListener {
            // Navigate to AddItemActivity when user_male ImageView is clicked
            val intent = Intent(this@DisplayAllSellers, HomeActivity::class.java)
            startActivity(intent)
        }

        // Fetch data from Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                val itemList = mutableListOf<Seller>()
                for (document in documents) {
                    val item = document.toObject(Seller::class.java)
                    itemList.add(item)
                }

                // Initialize and set up the adapter
                val adapter = sellersadapter(itemList, this)
                recyclerView.adapter = adapter

                // Search functionality
                val searchEditText: EditText = findViewById(R.id.searchEditText)
                searchEditText.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        adapter.filter(s.toString())
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        // Not used, but need to override
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        // Not used, but need to override
                    }
                })
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }

    override fun onItemClick(item: Seller) {
        // Handle item click by opening SellerDetailsActivity
        val intent = Intent(this, SellerDetails::class.java)
        intent.putExtra("ITEM_ID", item.User_ID) // Assuming your Seller class has a property named 'User_ID' for item ID
        startActivity(intent)
    }
}
