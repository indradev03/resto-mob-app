package com.example.indradev_resto.view.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


import com.example.indradev_resto.model.OrderModel
import com.example.indradev_resto.viewmodel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen() {
    val viewModel: BookingViewModel = viewModel()

    val orders by viewModel.orders.observeAsState(emptyList())
    val message by viewModel.message.observeAsState("")
    val isLoading by viewModel.isLoading.observeAsState(false)

    LaunchedEffect(Unit) {
        viewModel.getAllBookings()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Admin Orders") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                orders.isEmpty() -> {
                    Text(
                        text = if (message.isNotEmpty()) message else "No orders found.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(orders) { order ->
                            OrderItem(order = order)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItem(order: OrderModel) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order ID: ${order.orderId}", style = MaterialTheme.typography.bodySmall)
            Text("Customer: ${order.customerName}", style = MaterialTheme.typography.bodyLarge)
            Text("Contact: ${order.contactNumber}")
            Text("Table No: ${order.tableNumber}")
            Text("Booking Date: ${order.bookingDate}")
        }
    }
}
