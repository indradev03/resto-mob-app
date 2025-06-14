package com.example.indradev_resto.model

data class OrderModel(
    val orderId: String = "",            // Unique ID for order
    val table: TableModel = TableModel(), // Full table info
    val userId: String = "",    // Full user info
    val bookingTimestamp: Long = 0L        // When booked (optional)
)
// foreign key maa id matra pass hanne ho