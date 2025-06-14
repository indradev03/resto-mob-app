// BookingRepository.kt
package com.example.indradev_resto.repository

import com.example.indradev_resto.model.BookingModel

interface BookingModelRepo{
    fun insertBooking(
        booking: BookingModel,
        onResult: (Boolean, String) -> Unit
    )

    fun getAllBookings(
        onResult: (Boolean, String, List<BookingModel>) -> Unit
    )

    fun deleteBooking(bookingId: String, onResult: (Boolean, String) -> Unit)


}
