package com.example.ueeproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class BiddedItemsAdapter(
    private val bidList: List<BidInfo>,
    private val itemClickListener: (BidInfo) -> Unit
) : RecyclerView.Adapter<BiddedItemsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiddedItemsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.bidded_item_row, parent, false)
        return BiddedItemsViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: BiddedItemsViewHolder, position: Int) {
        val bidInfo = bidList[position]
        holder.bind(bidInfo)
    }

    override fun getItemCount(): Int {
        return bidList.size
    }
}



class BiddedItemsViewHolder(
    itemView: View,
    private val itemClickListener: (BidInfo) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val itemTextView: TextView = itemView.findViewById(R.id.itemTextView)
    private val bidAmountTextView: TextView = itemView.findViewById(R.id.bidAmountTextView)
    private val resultTextView: TextView = itemView.findViewById(R.id.resultTextView)

    fun bind(bidInfo: BidInfo) {
        // Bind data to views
        itemTextView.text = "Item : ${bidInfo.itemId}"
        bidAmountTextView.text = "Bid Amount: ${bidInfo.bidAmount}"

        // Check if the bid is the winning bid
        val db = FirebaseFirestore.getInstance()
        db.collection("Bids")
            .whereEqualTo("itemId", bidInfo.itemId) // Query bids for the same item
            .orderBy(
                "bidAmount",
                Query.Direction.DESCENDING
            ) // Order by bidAmount in descending order
            .limit(1) // Get the highest bid
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val highestBidInfo = documents.first().toObject(BidInfo::class.java)
                    if (highestBidInfo != null && highestBidInfo.UserId == bidInfo.UserId) {
                        // The current bid is the highest bid, mark it as the winner
                        bidInfo.isWinner = true
                        resultTextView.text = "You Won!"
                        // Enable item click
                        itemView.setOnClickListener {
                            itemClickListener.invoke(bidInfo)
                        }
                    } else {
                        // The current bid is not the winning bid
                        bidInfo.isWinner = false
                        resultTextView.text = "You Lost"
                        // Disable item click
                        itemView.setOnClickListener {
                            // Show message indicating the user has lost the bet
                            showLostBetMessage(itemView.context)
                        }
                    }
                } else {
                    // There are no other bids for this item, mark it as the winner
                    bidInfo.isWinner = true
                    resultTextView.text = "You Won!"
                    // Enable item click
                    itemView.setOnClickListener {
                        itemClickListener.invoke(bidInfo)
                    }
                }
            }
    }

    private fun showLostBetMessage(context: Context) {
        val successDialogBuilder = AlertDialog.Builder(context)
            .setTitle("Sorry!")
            .setMessage("You Lost The Bet")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        val successDialog = successDialogBuilder.create()
        successDialog.show()
    }
}





