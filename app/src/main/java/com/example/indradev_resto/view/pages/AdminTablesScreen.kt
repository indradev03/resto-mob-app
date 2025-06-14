import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
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
    var dialogTable by remember { mutableStateOf<TableModel?>(null) }

    LaunchedEffect(Unit) {
        tableViewModel.getAllTables { success, msg, _ ->
            loading = !success
            message = msg
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                dialogTable = null
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
            Text("🪑 Manage Tables", fontSize = 24.sp, style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(16.dp))

            if (loading) {
                CircularProgressIndicator()
            } else if (tables.isEmpty()) {
                Text("No tables available. $message")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(tables) { table ->
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
                    }
                }
            }
        }

        if (showDialog) {
            TableDialog(
                initialTable = dialogTable,
                onDismiss = { showDialog = false },
                onSave = { table ->
                    val operation = if (dialogTable == null)
                        tableViewModel::addTable
                    else
                        tableViewModel::updateTable

                    operation(table) { success, msg ->
                        message = msg
                        if (success) {
                            tableViewModel.getAllTables { _, _, _ -> }
                            showDialog = false
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Table #${table.tableNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Capacity: ${table.capacity}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Available: ${if (table.isAvailable) "Yes" else "No"}",
                    color = if (table.isAvailable) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                if (table.location.isNotBlank()) {
                    Text(
                        text = "Location: ${table.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = { onEdit(table) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Table",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = { onDelete(table) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Table",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
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

    var showErrors by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    showErrors = true
                    if ((initialTable != null || tableId.isNotBlank()) &&
                        tableNumber.isNotBlank() &&
                        capacity.isNotBlank()
                    ) {
                        onSave(
                            TableModel(
                                tableId = if (initialTable == null) tableId else initialTable.tableId,
                                tableNumber = tableNumber.toInt(),
                                capacity = capacity.toInt(),
                                location = location,
                                isAvailable = isAvailable
                            )
                        )
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
        },
        title = {
            Text(
                text = if (initialTable == null) "Add New Table" else "Edit Table",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (initialTable == null) {
                    OutlinedTextField(
                        value = tableId,
                        onValueChange = { tableId = it },
                        label = { Text("Table ID (unique)") },
                        singleLine = true,
                        isError = showErrors && tableId.isBlank(),
                        supportingText = {
                            if (showErrors && tableId.isBlank())
                                Text("Table ID is required", color = MaterialTheme.colorScheme.error)
                        }
                    )
                } else {
                    Text("Table ID: $tableId", style = MaterialTheme.typography.bodyMedium)
                }

                OutlinedTextField(
                    value = tableNumber,
                    onValueChange = { tableNumber = it.filter(Char::isDigit) },
                    label = { Text("Table Number") },
                    singleLine = true,
                    isError = showErrors && tableNumber.isBlank(),
                    supportingText = {
                        if (showErrors && tableNumber.isBlank())
                            Text("Table Number is required", color = MaterialTheme.colorScheme.error)
                    }
                )

                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it.filter(Char::isDigit) },
                    label = { Text("Capacity") },
                    singleLine = true,
                    isError = showErrors && capacity.isBlank(),
                    supportingText = {
                        if (showErrors && capacity.isBlank())
                            Text("Capacity is required", color = MaterialTheme.colorScheme.error)
                    }
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (optional)") },
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Available", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = isAvailable,
                        onCheckedChange = { isAvailable = it },
                        thumbContent = {
                            if (isAvailable) Icon(Icons.Default.Check, contentDescription = null)
                        }
                    )
                }
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

