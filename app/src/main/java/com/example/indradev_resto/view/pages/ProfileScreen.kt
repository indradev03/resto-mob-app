package com.example.indradev_resto.view.pages

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.indradev_resto.R
import com.example.indradev_resto.model.UserModel
import com.example.indradev_resto.repository.UserRepositoryImpl
import com.example.indradev_resto.view.LoginActivityResto
import com.example.indradev_resto.viewmodel.UserViewModel

@Composable
fun ProfileScreen(
    onEditClick: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { UserRepositoryImpl() }
    val userViewModel = remember { UserViewModel(repository) }

    var userModel by remember { mutableStateOf<UserModel?>(null) }
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!isLoaded) {
            val userId = userViewModel.getCurrentUser()?.uid
            if (userId != null) {
                userViewModel.getUserFromDatabase(userId) { success, message, user ->
                    if (success && user != null) {
                        userModel = user
                    } else {
                        Toast.makeText(context, "Failed to load profile", Toast.LENGTH_SHORT).show()
                    }
                    isLoaded = true
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.lunch4),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(120.dp)
                .padding(top = 16.dp)
                .background(Color(0xFFCCCCCC), CircleShape) // manual background color
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = userModel?.firstName ?: "Loading...",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF222222) // dark text color
        )

        Text(
            text = userModel?.email ?: "",
            fontSize = 16.sp,
            color = Color(0xFF666666) // medium gray for email text
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onEditClick() },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF007AFF), // blue button background
                contentColor = Color.White // white text on button
            )
        ) {
            Text("Edit Profile")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                userViewModel.logout { success, message ->
                    if (success) {
                        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, LoginActivityResto::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF007AFF) // blue text color for outlined button
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
        ) {
            Text("Log Out")
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "App Version 1.0.0",
            fontSize = 12.sp,
            color = Color(0xFFAAAAAA) // light gray text for version
        )
    }
}
