package com.example.indradev_resto.view

import BookingScreen
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.indradev_resto.R
import com.example.indradev_resto.repository.BookingModelRepoImpl
import com.example.indradev_resto.repository.TableModelRepositoryImpl
import com.example.indradev_resto.repository.UserRepositoryImpl
import com.example.indradev_resto.ui.theme.IndradevRestoTheme
import com.example.indradev_resto.view.pages.*
import com.example.indradev_resto.viewmodel.BookingViewModel
import com.example.indradev_resto.viewmodel.TableViewModel
import com.example.indradev_resto.viewmodel.UserViewModel

class DashboardActivityResto : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IndradevRestoTheme {
                RestoNavigationBody()
            }
        }
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestoNavigationBody() {
    val repository = remember { UserRepositoryImpl() }
    val userViewModel = remember { UserViewModel(repository) }

    val tableRepository = remember { TableModelRepositoryImpl() }
    val tableViewModel = remember { TableViewModel(tableRepository) }

    val bookingRepository = remember { BookingModelRepoImpl() }
    val bookingViewModel = remember { BookingViewModel(bookingRepository) }

    val navItems = listOf(
        BottomNavItem("Dashboard", Icons.Default.Home),
        BottomNavItem("Our Menu", Icons.Default.RestaurantMenu),
        BottomNavItem("Tables", Icons.Default.TableRestaurant),
        BottomNavItem("Profile", Icons.Default.Person)
    )

    var selectedIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current

    Scaffold(
        topBar = { TopNavigationBar { selectedIndex = it } },
        bottomBar = {
            RestoBottomNavigationBar(
                selectedIndex = selectedIndex,
                onItemSelected = { selectedIndex = it }
            )
        }
    ) { innerPadding ->

        // Wrap content inside Surface with elevation for shadow
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            shadowElevation = 8.dp, // add shadow / elevation
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp), // optional rounded corners for better look
            color = Color.White
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp) // optional padding inside shadow container
            ) {
                when (selectedIndex) {
                    0 -> DashboardScreen()
                    1 -> MenuScreen()
                    2 -> BookingScreen(tableViewModel, bookingViewModel)
                    3 -> ProfileScreen(
                        onEditClick = {
                            selectedIndex = 4 // Navigate to EditProfileScreen
                        }
                    )
                    4 -> EditProfileScreen(
                        userViewModel = userViewModel,
                        onBack = {
                            selectedIndex = 3 // Back to ProfileScreen
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun RestoBottomNavigationBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val navItems = listOf(
        BottomNavItem("Dashboard", Icons.Default.Home),
        BottomNavItem("Our Menu", Icons.Default.RestaurantMenu),
        BottomNavItem("Tables", Icons.Default.TableRestaurant),
        BottomNavItem("Profile", Icons.Default.Person)
    )

    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        navItems.forEachIndexed { index, item ->
            val isSelected = selectedIndex == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(index) },
                icon = {
                    Box(
                        modifier = Modifier
                            // Removed background hover effect here
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black,
                                modifier = Modifier.size(30.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                    }
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Black,
                    // You can also remove the indicatorColor to remove any background indicator on selection:
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}





@Composable
fun TopNavigationBar(onNavigateTo: (Int) -> Unit) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(1f)) // Pushes buttons to the right

        // Profile Button
        ImageButton(
            imageRes = R.drawable.user,
            description = "Profile",
            modifier = Modifier.size(48.dp),
            backgroundColor = Color(0xFFF0F0F0)
        ) {
            onNavigateTo(3) // Profile screen
        }

        Spacer(modifier = Modifier.width(12.dp))

        // More Menu Button
        Box {
            ImageButton(
                imageRes = R.drawable.more,
                description = "More",
                modifier = Modifier.size(48.dp),
                backgroundColor = Color(0xFFF0F0F0)
            ) {
                showMenu = true
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Menu") },
                    onClick = {
                        showMenu = false
                        onNavigateTo(1)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Settings") },
                    onClick = { showMenu = false }
                )
                DropdownMenuItem(
                    text = { Text("Help") },
                    onClick = { showMenu = false }
                )
                DropdownMenuItem(
                    text = { Text("Logout") },
                    onClick = {
                        showMenu = false
                        val prefs = context.getSharedPreferences("User", Context.MODE_PRIVATE)
                        prefs.edit().clear().apply()

                        val intent = Intent(context, LoginActivityResto::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}



@Composable
fun ImageButton(
    imageRes: Int,
    description: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    cornerRadius: Dp = 12.dp,
    iconSize: Dp = 30.dp,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = description,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDashboard() {
    IndradevRestoTheme {
        RestoNavigationBody()
    }
}
