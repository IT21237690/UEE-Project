package com.example.ueeproject

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ueeproject.R
import com.google.firebase.firestore.FirebaseFirestore

class VerificationAdapter : RecyclerView.Adapter<VerificationAdapter.ViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private var items: List<Comps> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_verification_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        //holder.titleTextView.text = item.title
        holder.titleTextView.text = "${item.title}"
        holder.verifyButton.setOnClickListener {
            // Update the verification status in Firestore when the "Verify" button is clicked
            updateVerificationStatus(item.id, "verified")
        }
        holder.unverifyButton.setOnClickListener {
            // Update the verification status in Firestore when the "Unverify" button is clicked
            updateVerificationStatus(item.id, "unverified")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(newItems: List<Comps>) {
        items = newItems
        notifyDataSetChanged()
    }

    private fun updateVerificationStatus(itemId: String?, Status: String) {
        // Update the 'status' field in Firestore for the specified item
        Log.d("Firestore", "Updating item $itemId with status $Status")
        if (itemId != null) {
            firestore.collection("Sell Items").document(itemId)
                .update("Status", Status)
                .addOnSuccessListener {
                    // Handle success
                }
                .addOnFailureListener {
                    // Handle failure
                }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val verifyButton: Button = itemView.findViewById(R.id.verifyButton)
        val unverifyButton: Button = itemView.findViewById(R.id.unverifyButton)
    }
}
