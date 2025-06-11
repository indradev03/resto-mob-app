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
import com.example.indradev_resto.model.TableModel
import com.example.indradev_resto.viewmodel.TableViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(tableViewModel: TableViewModel) {
    val tables by tableViewModel.tables.observeAsState(emptyList())
    val message by tableViewModel.message.observeAsState("")
    var loading by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Load tables on screen launch
    LaunchedEffect(Unit) {
        loading = true
        tableViewModel.getAllTables { success, msg, _ ->
            loading = false
            if (!success) {
                scope.launch {
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
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (tables.isEmpty()) {
                Text("No tables available. $message")
            } else {
                LazyColumn {
                    items(tables) { table ->
                        TableBookingItem(
                            table = table,
                            onBook = { bookedTable ->
                                tableViewModel.bookTable(bookedTable) { success, bookMsg ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(bookMsg)
                                    }
                                    if (success) {
                                        // Refresh tables after booking
                                        tableViewModel.getAllTables { _, _, _ -> }
                                    }
                                }
                            }
                        )
                        Divider()
                    }
                }
            }
        }
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
