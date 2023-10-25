package com.example.comps

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.google.android.material.navigation.NavigationBarView
//import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CurrentCompsActivity : AppCompatActivity() {

    private lateinit var complistview: ListView
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_comps)

        val createButton = findViewById<Button>(R.id.go_to_create_button)
        createButton.setOnClickListener {
            startActivity(Intent(this, CreateCompsActivity::class.java))
        }


        complistview = findViewById(R.id.comp_list_view)
        db = FirebaseFirestore.getInstance()

        //auth
        //val userId = FirebaseAuth.getInstance().currentUser?.uid
        //auth

        // Get all current goals from Firestore
        db.collection("comps")
            //.whereEqualTo("compId", compId)
            .get()
            .addOnSuccessListener { documents ->
                // Convert each Firestore document to a Goal object
                val comps = documents.map { it.toObject(Comps::class.java) }

                // Display the list of goals in the ListView
                val adapter = ArrayAdapter(this, R.layout.custom_list_item, comps.map { it.title })
                complistview.adapter = adapter
                //Log.d("ListViewClick", "Item clicked at position")

                // Set a click listener on each item in the list to go to the edit goal page
                complistview.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, view, position, id ->
                        val selectedComp = comps[position]
                        val intent = Intent(this, EditCompsActivity::class.java)
                        intent.putExtra("Comps_ID", selectedComp.id)
                        startActivity(intent)
                        Log.d("ListViewClick", "Item clicked at psition: $id")
                    }

                // Add styling to each row of the ListView
                for (i in 0 until complistview.childCount) {
                    val listItem = complistview.getChildAt(i)
                    val shape = GradientDrawable()
                    shape.shape = GradientDrawable.RECTANGLE
                    shape.cornerRadius = 15F
                    shape.setColor(getColor(R.color.goal_row_color))
                    listItem.background = shape
                }
            }

            .addOnFailureListener { exception ->
                // Handle errors
            }
    }
}