package com.example.ueeproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ueeproject.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.Registerbutton.setOnClickListener {

            val name = binding.nameEt.text.toString()
            val email =  binding.emailEt.text.toString()
            val phone = binding.phoneEt.text.toString()
            val address = binding.addressEt.text.toString()
            val password = binding.passET.text.toString()
            val confirmPassword = binding.confirmPassEt.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && address.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){

                if (password == confirmPassword){

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{task ->

                        if (task.isSuccessful) {
                            val userID = FirebaseAuth.getInstance().currentUser!!.uid
                            val userMap = hashMapOf(
                                "User_ID" to userID,
                                "name" to name,
                                "email" to email,
                                "phone" to phone,
                                "address" to address,
                                "password" to password,
                                "timestamp" to FieldValue.serverTimestamp()
                            )

                            db.collection("users").document(userID).set(userMap).addOnSuccessListener {
                                Toast.makeText(this, "Successfully Added!", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this, SignInActivity::class.java)
                                startActivity(intent)

                            }.addOnFailureListener {
                                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                            }
                        } else{

                            Toast.makeText(this, task.exception.toString() , Toast.LENGTH_SHORT).show()

                        }
                    }
                }else{

                    Toast.makeText(this, "Password is not matching" , Toast.LENGTH_SHORT).show()

                }
            }else{

                Toast.makeText(this, "Empty Fields Are not Allowed" , Toast.LENGTH_SHORT).show()

            }
        }

    }
}