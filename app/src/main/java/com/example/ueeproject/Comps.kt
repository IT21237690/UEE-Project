package com.example.comps

import com.google.firebase.firestore.DocumentId

data class Comps(

    var userId: String = "",
    @DocumentId val id: String? = null,
    val title: String = "",
    val sellername: String = "",
    val description: String = "",
    val category: String = "",

)
