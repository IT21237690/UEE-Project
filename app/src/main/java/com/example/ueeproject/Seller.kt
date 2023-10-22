package com.example.ueeproject


import java.io.Serializable

data class Seller(
    var name: String = "",
    var email: String = "",
    var User_ID: String = "",
    val password: String = "",
    val phone : String = "",
    val address : String = "",
): Serializable