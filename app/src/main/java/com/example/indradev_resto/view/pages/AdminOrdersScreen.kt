package com.example.indradev_resto.view.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
    var orderToDelete by remember { mutableStateOf<OrderModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getAllBookings()
    }

    if (orderToDelete != null) {
        AlertDialog(
            onDismissRequest = { orderToDelete = null },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteBooking(orderToDelete!!)
                    orderToDelete = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { orderToDelete = null }) {
                    Text("Cancel")
                }
            },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this booking?") }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📋 All Bookings") },
                modifier = Modifier.padding(top = 0.dp)
            )
        },
        // Disable system window insets padding to remove extra top space
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
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
                        text = if (message.isNotEmpty()) message else "No bookings available.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(orders) { order ->
                            OrderItem(
                                order = order,
                                onDeleteRequest = { orderToDelete = it }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItem(order: OrderModel, onDeleteRequest: (OrderModel) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🆔 #${order.orderId}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { onDeleteRequest(order) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Booking",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp)

            InfoRow(icon = Icons.Default.Person, label = "Customer", value = order.customerName)
            InfoRow(icon = Icons.Default.Phone, label = "Contact", value = order.contactNumber)
            InfoRow(icon = Icons.Default.TableBar, label = "Table No", value = order.tableNumber.toString())
            InfoRow(icon = null, label = "Booking Date", value = order.bookingDate)
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector?, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
