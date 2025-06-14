// BookingViewModel.kt
package com.example.indradev_resto.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.indradev_resto.model.BookingModel
import com.example.indradev_resto.model.OrderModel
import com.example.indradev_resto.repository.BookingModelRepo

class BookingViewModel(private val repository: BookingModelRepo) : ViewModel() {

    private val _message = MutableLiveData("")
    val message: LiveData<String> = _message

    private val _bookings = MutableLiveData<List<BookingModel>>()
    val bookings: LiveData<List<BookingModel>> = _bookings

    fun insertBooking(booking: BookingModel, onComplete: (Boolean, String) -> Unit) {
        repository.insertBooking(booking) { success, msg ->
            _message.value = msg
            onComplete(success, msg)
        }
    }

    private val _orders = MutableLiveData<List<OrderModel>>()
    val orders: LiveData<List<OrderModel>> = _orders

    fun getAllBookings() {
        repository.getAllBookings { success, msg, list ->
            _message.value = msg
            if (success) {
                _bookings.value = list
                // Convert to OrderModel
                val orderList = list.map {
                    OrderModel(
                        orderId = it.bookingId,
                        customerName = it.customerName,
                        contactNumber = it.contactNumber,
                        tableNumber = it.tableNumber,
                        bookingDate = it.bookingDate
                    )
                }
                _orders.value = orderList
            }
        }
    }
}
