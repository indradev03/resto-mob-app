package com.example.indradev_resto

import com.example.indradev_resto.model.BookingModel
import com.example.indradev_resto.repository.BookingModelRepoImpl
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BookingModelRepoImplTest {

    @Mock
    private lateinit var mockRef: DatabaseReference

    @Mock
    private lateinit var mockBookingRef: DatabaseReference

    @Mock
    private lateinit var mockTask: Task<Void>

    private lateinit var repository: BookingModelRepoImpl

    @Before
    fun setUp() {
        `when`(mockRef.child(any())).thenReturn(mockBookingRef)
        repository = BookingModelRepoImpl(mockRef)
    }

    @Test
    fun `insertBooking success`() {
        val booking = BookingModel(
            bookingId = "",
            tableId = "T1",
            tableNumber = 1,
            customerName = "John Doe",
            contactNumber = "1234567890",
            bookingDate = "2025-07-20"
        )
        val callback = mock<(Boolean, String) -> Unit>()

        `when`(mockBookingRef.setValue(any())).thenReturn(mockTask)

        doAnswer { invocation ->
            val listener = invocation.getArgument<OnSuccessListener<Void>>(0)
            listener.onSuccess(null)
            mockTask
        }.`when`(mockTask).addOnSuccessListener(any())

        repository.insertBooking(booking, callback)

        verify(callback).invoke(true, "Booking successful.")
    }

    @Test
    fun `deleteBooking success`() {
        val bookingId = "booking123"
        val callback = mock<(Boolean, String) -> Unit>()

        `when`(mockRef.child(bookingId)).thenReturn(mockBookingRef)
        `when`(mockBookingRef.removeValue()).thenReturn(mockTask)

        doAnswer { invocation ->
            val listener = invocation.getArgument<OnSuccessListener<Void>>(0)
            listener.onSuccess(null)
            mockTask
        }.`when`(mockTask).addOnSuccessListener(any())

        repository.deleteBooking(bookingId, callback)

        verify(callback).invoke(true, "Booking deleted successfully.")
    }
}
