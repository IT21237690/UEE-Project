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


class SellItemAdapter(private val editItemClickListener: OnItemClickListener) : ListAdapter<SellItem, SellItemViewHolder>(DIFF_CALLBACK) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_sell, parent, false)
        return SellItemViewHolder(view, editItemClickListener)
    }

    override fun onBindViewHolder(holder: SellItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }



    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SellItem>() {
            override fun areItemsTheSame(oldItem: SellItem, newItem: SellItem): Boolean {
                return oldItem.itemName == newItem.itemName
            }

            override fun areContentsTheSame(oldItem: SellItem, newItem: SellItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickListener {
        fun onEditClick(position: Int)
        fun onDeleteClick(position: Int)
    }
}

class SellItemViewHolder(itemView: View, private val itemClickListener: SellItemAdapter.OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
    private val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
    private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
    private val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
    private val itemImageView: ImageView = itemView.findViewById(R.id.itemImageView) // Reference to the ImageView
    private val editButton: Button = itemView.findViewById(R.id.editButton)
    private val deleteButton: Button = itemView.findViewById(R.id.deletebutton)



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

    fun bind(item: SellItem) {
        itemNameTextView.text = "Item Name: ${item.itemName}"
        descriptionTextView.text = "Description: ${item.description}"
        priceTextView.text = "Price: ${item.price}"



        // Load the image using Glide
        Glide.with(itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.ic_placeholder_image)
            .error(R.drawable.ic_error_image)
            .into(itemImageView)

        // Get current time in milliseconds

    }



}
