package com.example.ueeproject


import java.io.Serializable

data class SellItem(
    var itemName: String = "",
    var description: String = "",
    var price: String = "",
    val imageUrl: String = "",
    val itemId : String = "",
    val sellerId : String = "",
    var isChecked: Boolean = false,
    val Status : String = "Un Verified"
): Serializable
{
}