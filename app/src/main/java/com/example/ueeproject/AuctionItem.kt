package com.example.ueeproject

import java.io.Serializable

data class AuctionItem(
    var itemName: String = "",
    var description: String = "",
    var price: String = "",
    val startTime: Long = 0,
    val endTime: Long = 0,
    val imageUrl: String = "",
    val itemId : String = "",
    val UserId: String = ""

): Serializable
