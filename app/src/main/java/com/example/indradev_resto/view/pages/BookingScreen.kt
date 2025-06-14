import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.indradev_resto.model.BookingModel
import com.example.indradev_resto.model.TableModel
import com.example.indradev_resto.viewmodel.BookingViewModel
import com.example.indradev_resto.viewmodel.TableViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    tableViewModel: TableViewModel,
    bookingViewModel: BookingViewModel
) {
    val tables by tableViewModel.tables.observeAsState(emptyList())
    val message by tableViewModel.message.observeAsState("")
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(true) }

    // Dialog state
    var showDialog by remember { mutableStateOf(false) }
    var selectedTable by remember { mutableStateOf<TableModel?>(null) }

    // Load tables initially
    LaunchedEffect(Unit) {
        loading = true
        tableViewModel.getAllTables { success, msg, _ ->
            loading = false
            if (!success) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Error: $msg")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Available Tables", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (loading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (tables.isEmpty()) {
                Text("No tables available. $message")
            } else {
                LazyColumn {
                    items(tables) { table ->
                        TableBookingItem(table = table, onBook = {
                            selectedTable = it
                            showDialog = true
                        })
                        Divider()
                    }
                }
            }
        }
    }

    // Booking dialog
    if (showDialog && selectedTable != null) {
        var name by remember { mutableStateOf("") }
        var contact by remember { mutableStateOf("") }
        var date by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Book Table #${selectedTable!!.tableNumber}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Customer Name") }
                    )
                    OutlinedTextField(
                        value = contact,
                        onValueChange = { contact = it },
                        label = { Text("Contact Number") }
                    )
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Booking Date") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val booking = BookingModel(
                            bookingId = "", // Firestore will assign
                            tableId = selectedTable!!.tableId,
                            tableNumber = selectedTable!!.tableNumber,
                            customerName = name,
                            contactNumber = contact,
                            bookingDate = date
                        )
                        // Insert booking
                        bookingViewModel.insertBooking(booking) { success, msg ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(msg)
                            }
                            if (success) {
                                // Mark the table unavailable after booking success
                                tableViewModel.markTableUnavailable(selectedTable!!.tableId) { updateSuccess, updateMsg ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(updateMsg)
                                    }
                                    // Refresh the tables list after update
                                    tableViewModel.getAllTables { _, _, _ -> }
                                }
                            }
                            showDialog = false
                        }
                    },
                    enabled = name.isNotBlank() && contact.isNotBlank() && date.isNotBlank()
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TableBookingItem(table: TableModel, onBook: (TableModel) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Table #${table.tableNumber}", style = MaterialTheme.typography.titleMedium)
            Text("Capacity: ${table.capacity}", style = MaterialTheme.typography.bodyMedium)
            Text(
                "Available: ${if (table.isAvailable) "Yes" else "No"}",
                color = if (table.isAvailable) Color(0xFF4CAF50) else Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Button(
            enabled = table.isAvailable,
            onClick = { onBook(table) }
        ) {
            Text("Book")
        }
    }
}
