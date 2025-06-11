import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.indradev_resto.model.TableModel
import com.example.indradev_resto.viewmodel.TableViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTablesScreen(tableViewModel: TableViewModel) {
    var loading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf("") }
    val tables by tableViewModel.tables.observeAsState(emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var dialogTable by remember { mutableStateOf<TableModel?>(null) } // null = add new

    // Load tables on enter
    LaunchedEffect(Unit) {
        tableViewModel.getAllTables { success, msg, _ ->
            loading = !success
            message = msg
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                dialogTable = null // Add new
                showDialog = true
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Table")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("List of tables", fontSize = 24.sp)

            Spacer(Modifier.height(16.dp))

            if (loading) {
                Text("Loading tables...")
            } else if (tables!!.isEmpty()) {
                Text("No tables available. $message")
            } else {
                LazyColumn {
                    items(tables!!) { table ->
                        TableItem(
                            table = table,
                            onEdit = {
                                dialogTable = it
                                showDialog = true
                            },
                            onDelete = { toDelete ->
                                tableViewModel.deleteTable(toDelete.tableId) { success, msg ->
                                    message = msg
                                    if (success) {
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

        if (showDialog) {
            TableDialog(
                initialTable = dialogTable,
                onDismiss = { showDialog = false },
                onSave = { table ->
                    if (dialogTable == null) {
                        // Adding new table
                        tableViewModel.addTable(table) { success, msg ->
                            message = msg
                            if (success) {
                                tableViewModel.getAllTables { _, _, _ -> }
                                showDialog = false
                            }
                        }
                    } else {
                        // Updating existing table
                        tableViewModel.updateTable(table) { success, msg ->
                            message = msg
                            if (success) {
                                tableViewModel.getAllTables { _, _, _ -> }
                                showDialog = false
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun TableItem(
    table: TableModel,
    onEdit: (TableModel) -> Unit,
    onDelete: (TableModel) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Table #${table.tableNumber}", fontSize = 18.sp, color = Color.Black)
            Text("Capacity: ${table.capacity}", fontSize = 14.sp, color = Color.Gray)
            Text(
                "Available: ${if (table.isAvailable) "Yes" else "No"}",
                fontSize = 14.sp,
                color = if (table.isAvailable) Color(0xFF4CAF50) else Color.Red
            )
        }

        Row {
            IconButton(onClick = { onEdit(table) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Table")
            }
            IconButton(onClick = { onDelete(table) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Table", tint = Color.Red)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableDialog(
    initialTable: TableModel?,
    onDismiss: () -> Unit,
    onSave: (TableModel) -> Unit
) {
    var tableId by remember { mutableStateOf(initialTable?.tableId ?: "") }
    var tableNumber by remember { mutableStateOf(initialTable?.tableNumber?.toString() ?: "") }
    var capacity by remember { mutableStateOf(initialTable?.capacity?.toString() ?: "") }
    var location by remember { mutableStateOf(initialTable?.location ?: "") }
    var isAvailable by remember { mutableStateOf(initialTable?.isAvailable ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (initialTable == null) "Add New Table" else "Edit Table")
        },
        text = {
            Column {
                if (initialTable == null) {
                    OutlinedTextField(
                        value = tableId,
                        onValueChange = { tableId = it },
                        label = { Text("Table ID (unique)") },
                        singleLine = true,
                        isError = tableId.isBlank()
                    )
                } else {
                    Text("Table ID: $tableId", modifier = Modifier.padding(bottom = 8.dp))
                }

                OutlinedTextField(
                    value = tableNumber,
                    onValueChange = { tableNumber = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Table Number") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Capacity") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    singleLine = true
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isAvailable,
                        onCheckedChange = { isAvailable = it }
                    )
                    Text("Available")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Validate inputs
                    if ((initialTable != null || tableId.isNotBlank())
                        && tableNumber.isNotBlank()
                        && capacity.isNotBlank()
                    ) {
                        val tableModel = TableModel(
                            tableId = if (initialTable == null) tableId else initialTable.tableId,
                            tableNumber = tableNumber.toInt(),
                            capacity = capacity.toInt(),
                            location = location,
                            isAvailable = isAvailable
                        )
                        onSave(tableModel)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
