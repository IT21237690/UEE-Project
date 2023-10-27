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

    private lateinit var binding: ActivityUpdateUserDetailsBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUpdateUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        val updateName: EditText = findViewById(R.id.updateName)
        val updateEmail: EditText = findViewById(R.id.updateEmail)
        val updateAddress: EditText = findViewById(R.id.updateAddress)
        val updatePhone: EditText = findViewById(R.id.updatePhone)
        val updatePassword: EditText = findViewById(R.id.updatePassword)

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
                    val address = document.getString("address")
                    val phone = document.getString("phone")
                    val password = document.getString("password")

                    updateName.setText(name)
                    updateEmail.setText(email)
                    updateAddress.setText(address)
                    updatePhone.setText(phone)
                    updatePassword.setText(password)

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
            val newAddress = updateAddress.text.toString().trim()
            val newPhone = updatePhone.text.toString().trim()
            val newPassword = updatePassword.text.toString().trim()

            if (newName.isEmpty() || newEmail.isEmpty() || newAddress.isEmpty() || newPhone.isEmpty()){
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

            // update userEmail
            val user = auth.currentUser
            val credential = EmailAuthProvider.getCredential(user?.email!!, newPassword)

            user?.reauthenticate(credential)?.addOnSuccessListener {
                user.updateEmail(newEmail).addOnSuccessListener {
                    // Update the user's email in Firestore
                    db.collection("users")
                        .document(auth.currentUser!!.uid)
                        .update("email", newEmail)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, Profile::class.java))
                        }
                        .addOnFailureListener { exception ->
                            Log.d("EditProfile", "update failed with ", exception)
                            // handle exception here
                        }
                }.addOnFailureListener { exception ->
                    Log.d("EditProfile", "update failed with ", exception)
                    // handle exception here
                }
            }?.addOnFailureListener { exception ->
                Log.d("EditProfile", "reauthentication failed with ", exception)
                // handle exception here
            }

            //Update user address
            db.collection("users")
                .document(auth.currentUser!!.uid)
                .update("address", newAddress)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                    startActivity(
                        Intent(this, Profile::class.java)
                    )
                }
                .addOnFailureListener { exception ->
                    Log.d("EditProfile", "update failed with ", exception)
                }

            //update user Phone
            db.collection("users")
                .document(auth.currentUser!!.uid)
                .update("phone", newPhone)
                .addOnSuccessListener {

                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                    startActivity(
                        Intent(this, Profile::class.java)
                    )

                }
                .addOnFailureListener { exception ->
                    Log.d("EditProfile", "update failed with ", exception)
                    // handle exception here
                }



        }

    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}