package com.example.indradev_resto.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingModel(
    @PrimaryKey
    val bookingId: String = "",
    val tableId: String = "",
    val tableNumber: Int = 0,
    val customerName: String = "",
    val contactNumber: String = "",
    val bookingDate: String = ""
)

