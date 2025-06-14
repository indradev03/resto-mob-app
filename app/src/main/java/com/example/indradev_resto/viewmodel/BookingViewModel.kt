package com.example.indradev_resto.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.indradev_resto.model.BookingModel
import com.example.indradev_resto.model.OrderModel
import com.example.indradev_resto.repository.BookingModelRepoImpl

class BookingViewModel(
    private val repository: BookingModelRepoImpl = BookingModelRepoImpl()) : ViewModel() {


    private val _message = MutableLiveData("")
    val message: LiveData<String> = _message

    private val _orders = MutableLiveData<List<OrderModel>>(emptyList())
    val orders: LiveData<List<OrderModel>> = _orders

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _bookings = MutableLiveData<List<BookingModel>>()
    val bookings: LiveData<List<BookingModel>> = _bookings

    fun insertBooking(booking: BookingModel, onComplete: (Boolean, String) -> Unit) {
        repository.insertBooking(booking) { success, msg ->
            _message.value = msg
            onComplete(success, msg)
        }
    }
    
    fun getAllBookings() {
        _isLoading.value = true
        repository.getAllBookings { success, msg, list ->
            _message.value = msg
            if (success) {
                _orders.value = list.map {
                    OrderModel(
                        orderId = it.bookingId,
                        customerName = it.customerName,
                        contactNumber = it.contactNumber,
                        tableNumber = it.tableNumber,
                        bookingDate = it.bookingDate
                    )
                }
            } else {
                _orders.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    fun deleteBooking(order: OrderModel) {
        _isLoading.value = true
        repository.deleteBooking(order.orderId) { success, msg ->
            _message.value = msg
            if (success) {
                getAllBookings()
            } else {
                _isLoading.value = false
            }
        }
    }


}
