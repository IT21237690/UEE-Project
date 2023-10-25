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

class buyerviewadapter(private val items: List<SellItem>, private val listener: OnItemClickListener) : RecyclerView.Adapter<buyerviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): buyerviewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.buyer_items, parent, false)
        return buyerviewViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: buyerviewViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface OnItemClickListener {
        fun onItemClick(item: SellItem)
    }
}


class buyerviewViewHolder(itemView: View, private val listener: buyerviewadapter.OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
    private val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
    private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
    private val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
    private val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)

    private val itemImageView: ImageView = itemView.findViewById(R.id.itemImageView)


    fun bind(item: SellItem) {
        itemNameTextView.text = "Item Name: ${item.itemName}"
        descriptionTextView.text = "Description: ${item.description}"
        priceTextView.text = "Price: ${item.price}"
        statusTextView.text = item.Status



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