package com.example.indradev_resto.view.pages

import AdminTablesScreen
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.indradev_resto.repository.TableModelRepositoryImpl
import com.example.indradev_resto.view.pages.components.AdminBottomNavItem
import com.example.indradev_resto.view.pages.components.AdminOrdersScreen
import com.example.indradev_resto.view.pages.components.AdminUsersScreen
import com.example.indradev_resto.viewmodel.TableViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(onLogout: () -> Unit = {}) {

    val tablerepository = TableModelRepositoryImpl()
    val tableViewModel = TableViewModel(tablerepository)
    // Default to HOME
    var selectedItem by remember { mutableStateOf(AdminBottomNavItem.HOME) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                AdminBottomNavItem.values().forEach { item ->
                    NavigationBarItem(
                        selected = selectedItem == item,
                        onClick = { selectedItem = item },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (selectedItem) {
                AdminBottomNavItem.HOME -> AdminHomeScreen()
                AdminBottomNavItem.USERS -> AdminUsersScreen()
                AdminBottomNavItem.ORDERS -> AdminOrdersScreen()
                AdminBottomNavItem.TABLES -> AdminTablesScreen(tableViewModel)
            }
        }
    }
}