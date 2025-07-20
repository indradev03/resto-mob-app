package com.example.indradev_resto.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.indradev_resto.repository.BookingModelRepoImpl
import com.example.indradev_resto.repository.TableModelRepositoryImpl
import com.example.indradev_resto.repository.UserRepositoryImpl
import com.google.firebase.database.FirebaseDatabase

class AdminHomeViewModel : ViewModel() {

    // Initialize repositories here (without 'remember' - remember is for Compose only)
    private val userRepo = UserRepositoryImpl()
    private val tableRepo = TableModelRepositoryImpl(
        FirebaseDatabase.getInstance().reference.child("tables")
    )
    private val bookingRepo = BookingModelRepoImpl()

    var userCount by mutableStateOf(0)
        private set

    var tableCount by mutableStateOf(0)
        private set

    var bookingCount by mutableStateOf(0)
        private set

    init {
        fetchCounts()
    }

    private fun fetchCounts() {
        userRepo.getUserCount { success, count ->
            if (success) userCount = count
        }

        tableRepo.getTableCount { success, count ->
            if (success) tableCount = count
        }

        bookingRepo.getBookingCount { success, count ->
            if (success) bookingCount = count
        }
    }
}
