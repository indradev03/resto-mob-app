package com.example.indradev_resto.repository

import com.example.indradev_resto.model.BookingModel
import com.google.firebase.database.*
import java.util.*

class BookingModelRepoImpl(
    private val ref: DatabaseReference = FirebaseDatabase.getInstance().reference.child("bookings")
) : BookingModelRepo {

    override fun insertBooking(
        booking: BookingModel,
        onResult: (Boolean, String) -> Unit
    ) {
        val bookingId = UUID.randomUUID().toString()
        val newBooking = booking.copy(bookingId = bookingId)

        ref.child(bookingId).setValue(newBooking)
            .addOnSuccessListener {
                onResult(true, "Booking successful.")
            }
            .addOnFailureListener { e ->
                onResult(false, "Failed: ${e.localizedMessage}")
            }
    }

    override fun getAllBookings(
        onResult: (Boolean, String, List<BookingModel>) -> Unit
    ) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookingList = mutableListOf<BookingModel>()
                for (child in snapshot.children) {
                    val booking = child.getValue(BookingModel::class.java)
                    booking?.let { bookingList.add(it) }
                }
                onResult(true, "Bookings fetched successfully.", bookingList)
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(false, "Error: ${error.message}", emptyList())
            }
        })
    }

    override fun deleteBooking(
        bookingId: String,
        onResult: (Boolean, String) -> Unit
    ) {
        ref.child(bookingId).removeValue()
            .addOnSuccessListener {
                onResult(true, "Booking deleted successfully.")
            }
            .addOnFailureListener { e ->
                onResult(false, "Failed to delete booking: ${e.localizedMessage}")
            }
    }

    override fun getBookingCount(callback: (Boolean, Int) -> Unit) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount.toInt()
                callback(true, count)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, 0)
            }
        })
    }
}
