package com.example.ueeproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
        itemTextView.text = "Item ID: ${bidInfo.itemId}"
        bidAmountTextView.text = "Bid Amount: ${bidInfo.bidAmount}"
        resultTextView.text = if (bidInfo.isWinner) "You Won!" else "You Lost"

        // Handle item click
        itemView.setOnClickListener {
            itemClickListener.invoke(bidInfo)
        }
    }
}


