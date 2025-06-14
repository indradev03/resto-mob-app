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

    LaunchedEffect(true) {
        viewModel.fetchAllUsers { success, message ->
            if (!success) {
                println("Error fetching users: $message")
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Manage Users", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(allUsers) { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Name: ${user.firstName} ${user.lastName}")
                        Text("Email: ${user.email}")
                        Text("Gender: ${user.selectedGender}")
                        Text("DOB: ${user.selectedDate}")
                        Text("Role: ${user.selectedOptionText}")
                    }
                }
            }
        }
    }
}
