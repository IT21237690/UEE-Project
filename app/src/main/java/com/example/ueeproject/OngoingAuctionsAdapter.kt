package com.example.ueeproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class OngoingAuctionsAdapter(private val items: List<AuctionItem>, private val listener: OnItemClickListener) : RecyclerView.Adapter<OngoingAuctionsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OngoingAuctionsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_ongoing_auction, parent, false)
        return OngoingAuctionsViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: OngoingAuctionsViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface OnItemClickListener {
        fun onItemClick(item: AuctionItem)
    }
}

class OngoingAuctionsViewHolder(itemView: View, private val listener: OngoingAuctionsAdapter.OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
    private val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
    private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
    private val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
    private val startTimeTextView: TextView = itemView.findViewById(R.id.startTimeTextView)
    private val endTimeTextView: TextView = itemView.findViewById(R.id.endTimeTextView)
    private val itemImageView: ImageView = itemView.findViewById(R.id.itemImageView)

    private val dateFormat = SimpleDateFormat("EEE, MMM dd, yyyy hh:mm a", Locale.getDefault())

    fun bind(item: AuctionItem) {
        itemNameTextView.text = "Item Name: ${item.itemName}"
        descriptionTextView.text = "Description: ${item.description}"
        priceTextView.text = "Price: ${item.price}"

        val startTime = dateFormat.format(item.startTime)
        val endTime = dateFormat.format(item.endTime)

        startTimeTextView.text = "Start Time: $startTime"
        endTimeTextView.text = "End Time: $endTime"

        Glide.with(itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.ic_placeholder_image)
            .error(R.drawable.ic_error_image)
            .into(itemImageView)

        itemView.setOnClickListener {
            listener.onItemClick(item)
        }
    }
}


