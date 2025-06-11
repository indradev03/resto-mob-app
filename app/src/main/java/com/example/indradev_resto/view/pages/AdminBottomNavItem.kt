package com.example.indradev_resto.view.pages.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.ui.graphics.vector.ImageVector

enum class AdminBottomNavItem(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Filled.Home),         // Home added back
    USERS("Users", Icons.Filled.People),
    ORDERS("Orders", Icons.Filled.List),
    TABLES("Tables", Icons.Filled.TableChart)
}
