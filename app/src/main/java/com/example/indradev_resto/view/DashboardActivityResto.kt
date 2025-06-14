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
import com.example.indradev_resto.model.BookingModel
import com.example.indradev_resto.repository.BookingModelRepoImpl
import com.example.indradev_resto.repository.TableModelRepositoryImpl
import com.example.indradev_resto.repository.UserRepositoryImpl
import com.example.indradev_resto.view.pages.*
import com.example.indradev_resto.viewmodel.BookingViewModel
import com.example.indradev_resto.viewmodel.TableViewModel
import com.example.indradev_resto.viewmodel.UserViewModel
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.Tables

class DashboardActivityResto : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RestoNavigationBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestoNavigationBody() {
    val repository = remember { UserRepositoryImpl() }
    val userViewModel = remember { UserViewModel(repository) }

    val tableRepository = remember { TableModelRepositoryImpl() }
    val tableViewModel = remember { TableViewModel(tableRepository) }

    val BookingRepository = remember { BookingModelRepoImpl() }
    val BookingViewModel = remember { BookingViewModel(BookingRepository) }

    data class BottomNavItem(val label: String, val icon: ImageVector)
    val navItems = listOf(
        BottomNavItem("Dashboard", Icons.Default.Home),
        BottomNavItem("Our Menu", Icons.Default.List),
        BottomNavItem("Tables", Icons.Default.Star),
        BottomNavItem("Profile", Icons.Default.Person)
    )

    val context = LocalContext.current
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { TopNavigationBar { selectedIndex = it } },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
            ) {
                NavigationBar(
                    containerColor = Color.White
                ) {
                    navItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedIndex == index,
                            onClick = { selectedIndex = index },
                            icon = {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(34.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = item.label,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            },
                            alwaysShowLabel = false
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            when (selectedIndex) {
                0 -> DashboardScreen()
                1 -> MenuScreen()
                2 -> BookingScreen(
                    tableViewModel,
                    BookingViewModel
                )
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

@Composable
fun TopNavigationBar(onNavigateTo: (Int) -> Unit) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
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

        // Search Field
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search...") },
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF0F0F0),
                unfocusedContainerColor = Color(0xFFF0F0F0),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

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
    iconSize: Dp = 25.dp,
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
    RestoNavigationBody()
}