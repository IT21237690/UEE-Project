package com.example.ueeproject

data class BidInfo(
    val itemId: String = "",
    val UserId: String = "",
    val bidAmount: Double = 0.0,
    var isWinner: Boolean = true
)
