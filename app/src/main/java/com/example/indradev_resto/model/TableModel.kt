package com.example.indradev_resto.model

data class TableModel(
    val tableId: String = "",                // Unique ID for the table
    val tableNumber: Int = 0,                // Number displayed on the table
    val capacity: Int = 0,                   // Number of guests it fits
    val location: String = "",               // Optional: "Indoor", "Outdoor", "Window-side", etc.
    var isAvailable: Boolean = true,         // Is table currently available
    val imageUrl: String? = null             // Optional: Image of table layout give TabelModelrepository

)