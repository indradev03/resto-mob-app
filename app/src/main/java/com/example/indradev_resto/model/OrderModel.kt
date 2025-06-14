package com.example.indradev_resto.model

data class OrderModel(
    val orderId: String,        // bookingId
    val customerName: String,
    val contactNumber: String,
    val tableNumber: Int,
    val bookingDate: String
)
