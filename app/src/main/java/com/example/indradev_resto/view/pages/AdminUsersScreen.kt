package com.example.indradev_resto.view.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.indradev_resto.model.UserModel
import com.example.indradev_resto.repository.UserRepositoryImpl
import com.example.indradev_resto.viewmodel.UserViewModel

@Composable
fun AdminUsersScreen() {
    val viewModel: UserViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return UserViewModel(UserRepositoryImpl()) as T
        }
    })

    val allUsers by viewModel.allUsers.observeAsState(emptyList())
    val currentUserId = viewModel.getCurrentUser()?.uid

    var showDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<UserModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchAllUsers {}
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            text = "👥 Manage Users",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(allUsers) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {

                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${user.firstName} ${user.lastName}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = user.selectedOptionText,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("📧 ${user.email}")
                        Text("🚻 ${user.selectedGender}")
                        Text("🎂 ${user.selectedDate}")

                        Spacer(modifier = Modifier.height(8.dp))

                        if (user.userId != currentUserId) {
                            OutlinedButton(
                                onClick = {
                                    selectedUser = user
                                    showDialog = true
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Red
                                ),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete")
                            }
                        } else {
                            Text(
                                text = "✅ Currently Logged In",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.align(Alignment.End),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDialog && selectedUser != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete User") },
            text = {
                Text("Are you sure you want to permanently delete ${selectedUser!!.email}?")
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    viewModel.deleteAccount(selectedUser!!.userId) { success, message ->
                        if (success) {
                            viewModel.fetchAllUsers {}
                        }
                        println(message)
                    }
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
