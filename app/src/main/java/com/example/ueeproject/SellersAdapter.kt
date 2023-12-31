package com.example.ueeproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class sellersadapter(private val originalItems: List<Seller>, private val listener: OnItemClickListener) : RecyclerView.Adapter<sellersviewViewHolder>() {

    private var filteredItems: List<Seller> = originalItems.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): sellersviewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.sellers, parent, false)
        return sellersviewViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: sellersviewViewHolder, position: Int) {
        val item = filteredItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return filteredItems.size
    }

    interface OnItemClickListener {
        fun onItemClick(item: Seller)
    }

    fun filter(query: String) {
        filteredItems = originalItems.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.email.contains(query, ignoreCase = true) ||
                    it.phone.contains(query, ignoreCase = true) ||
                    it.address.contains(query, ignoreCase = true)
        }
        notifyDataSetChanged()
    }
}



class sellersviewViewHolder(itemView: View, private val listener: sellersadapter.OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
    private val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
    private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
    private val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
    private val  AddressTextVIew : TextView = itemView.findViewById(R.id.AddressTextVIew)





    fun bind(item: Seller) {
        itemNameTextView.text = "Name: ${item.name}"
        descriptionTextView.text = "Email: ${item.email}"
        priceTextView.text = "Phone: ${item.phone}"
        AddressTextVIew.text = "Address: ${item.address}"




        itemView.setOnClickListener {
            listener.onItemClick(item)
        }
    }
}