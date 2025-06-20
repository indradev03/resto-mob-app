import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

    var showDialog by remember { mutableStateOf(false) }
    var selectedTable by remember { mutableStateOf<TableModel?>(null) }

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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 0.dp,
                    bottom = 0.dp
                )
        ) {
            Text(
                text = "\uD83D\uDCCB Available Tables",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color(0xFF037715),
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (tables.isEmpty()) {
                Text("No tables available. $message")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(tables) { table ->
                        TableBookingCard(table = table, onBook = {
                            selectedTable = it
                            showDialog = true
                        })
                    }
                }
            }
        }
    }

    if (showDialog && selectedTable != null) {
        BookingDialog(
            selectedTable = selectedTable!!,
            onDismiss = { showDialog = false },
            onConfirm = { name, contact, date ->
                val booking = BookingModel(
                    bookingId = "",
                    tableId = selectedTable!!.tableId,
                    tableNumber = selectedTable!!.tableNumber,
                    customerName = name,
                    contactNumber = contact,
                    bookingDate = date
                )

                bookingViewModel.insertBooking(booking) { success, msg ->
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(msg)
                    }
                    if (success) {
                        tableViewModel.markTableUnavailable(selectedTable!!.tableId) { _, updateMsg ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(updateMsg)
                            }
                            tableViewModel.getAllTables { _, _, _ -> }
                        }
                    }
                    showDialog = false
                }
            }
        )
    }
}

@Composable
fun TableBookingCard(table: TableModel, onBook: (TableModel) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F6F6)) // ✅ White background here
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Table #${table.tableNumber}",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black // ✅ Set text color to black
            )
            Text(
                text = "Capacity: ${table.capacity}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black // ✅ Set text color to black
            )
            Text(
                text = "Status: ${if (table.isAvailable) "Available" else "Booked"}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (table.isAvailable) Color(0xFF4CAF50) else Color.Red
            )
            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                val isBooked = !table.isAvailable

                Button(
                    onClick = {
                        if (!isBooked) onBook(table)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isBooked) Color (0xFF737273) else Color(0xFF0088FF), // 💠 Indigo if available, Gray if booked
                        contentColor = Color.White
                    ),
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text(text = if (isBooked) "Booked" else "Book")
                }
            }


        }

    }
}


@Composable
fun BookingDialog(
    selectedTable: TableModel,
    onDismiss: () -> Unit,
    onConfirm: (name: String, contact: String, date: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Book Table #${selectedTable.tableNumber}")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Customer Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Contact Number") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Booking Date") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, contact, date) },
                enabled = name.isNotBlank() && contact.isNotBlank() && date.isNotBlank()
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
