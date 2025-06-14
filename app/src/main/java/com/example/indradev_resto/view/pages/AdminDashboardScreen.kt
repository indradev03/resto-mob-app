package com.example.indradev_resto.view.pages


import AdminTablesScreen
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.indradev_resto.repository.TableModelRepositoryImpl
import com.example.indradev_resto.view.pages.components.AdminBottomNavItem
import com.example.indradev_resto.viewmodel.TableViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(onLogout: () -> Unit = {}) {

    val tablerepository = TableModelRepositoryImpl()
    val tableViewModel = TableViewModel(tablerepository)
    var selectedItem by remember { mutableStateOf(AdminBottomNavItem.HOME) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },

                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                AdminBottomNavItem.values().forEach { item ->
                    NavigationBarItem(
                        selected = selectedItem == item,
                        onClick = { selectedItem = item },
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                tint = if (selectedItem == item) MaterialTheme.colorScheme.primary else Color.Black
                            )
                        },
                        label = { Text(item.label) },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White) // Set screen background white

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
