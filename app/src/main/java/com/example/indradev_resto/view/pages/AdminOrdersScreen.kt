import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                TextButton(
                    onClick = {
                        viewModel.deleteBooking(orderToDelete!!)
                        orderToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { orderToDelete = null }) {
                    Text("Cancel")
                }
            },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this order?") }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Bookings") })
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
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order ID: ${order.orderId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { onDeleteRequest(order) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete order",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            Text("Customer: ${order.customerName}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Contact: ${order.contactNumber}", style = MaterialTheme.typography.bodyMedium)
            Text("Table No: ${order.tableNumber}", style = MaterialTheme.typography.bodyMedium)
            Text("Booking Date: ${order.bookingDate}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
