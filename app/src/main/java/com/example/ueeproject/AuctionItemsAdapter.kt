package com.example.ueeproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AuctionItemsAdapter(private val editItemClickListener: OnItemClickListener) : ListAdapter<AuctionItem, AuctionItemViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuctionItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_auction, parent, false)
        return AuctionItemViewHolder(view, editItemClickListener)
    }

    override fun onBindViewHolder(holder: AuctionItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AuctionItem>() {
            override fun areItemsTheSame(oldItem: AuctionItem, newItem: AuctionItem): Boolean {
                return oldItem.itemName == newItem.itemName
            }

            override fun areContentsTheSame(oldItem: AuctionItem, newItem: AuctionItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickListener {
        fun onEditClick(position: Int)
        fun onDeleteClick(position: Int)
    }
}

class AuctionItemViewHolder(itemView: View, private val itemClickListener: AuctionItemsAdapter.OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
    private val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
    private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
    private val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
    private val startTimeTextView: TextView = itemView.findViewById(R.id.startTimeTextView)
    private val endTimeTextView: TextView = itemView.findViewById(R.id.endTimeTextView)
    private val itemImageView: ImageView = itemView.findViewById(R.id.itemImageView) // Reference to the ImageView
    private val editButton: Button = itemView.findViewById(R.id.editButton)
    private val deleteButton: Button = itemView.findViewById(R.id.deletebutton)
    private val statusTextView: TextView = itemView.findViewById(R.id.statusTextView) // Add a TextView for displaying status

    private val dateFormat = SimpleDateFormat("EEE, MMM dd, yyyy hh:mm a", Locale.getDefault())


    init {
        // Set click listeners for edit and delete buttons
        editButton.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                itemClickListener.onEditClick(position)
            }
        }

        deleteButton.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                itemClickListener.onDeleteClick(position)
            }
        }
    }

    fun bind(item: AuctionItem) {
        itemNameTextView.text = "Item Name: ${item.itemName}"
        descriptionTextView.text = "Description: ${item.description}"
        priceTextView.text = "Price: ${item.price}"

        // Parse and format start and end times
        val dateFormat = SimpleDateFormat("EEE, MMM dd, yyyy hh:mm a", Locale.getDefault())
        val startTime = dateFormat.format(item.startTime)
        val endTime = dateFormat.format(item.endTime)

        startTimeTextView.text = "Start Time: $startTime"
        endTimeTextView.text = "End Time: $endTime"

        // Load the image using Glide
        Glide.with(itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.ic_placeholder_image)
            .error(R.drawable.ic_error_image)
            .into(itemImageView)

        // Get current time in milliseconds
        val currentTimeMillis = System.currentTimeMillis()

        // Compare start and end times with current time
        when {
            item.startTime < currentTimeMillis && item.endTime > currentTimeMillis -> {
                statusTextView.text = "Ongoing"
            }
            item.endTime < currentTimeMillis -> {
                statusTextView.text = "Ended"
            }
            item.startTime > currentTimeMillis && item.endTime > currentTimeMillis -> {
                statusTextView.text = "Upcoming"
            }
        }
    }



}
