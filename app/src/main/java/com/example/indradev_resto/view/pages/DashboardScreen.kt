package com.example.indradev_resto.view.pages

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)

    val firstName = sharedPreferences.getString("firstName", "Guest")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Good Morning, $firstName", fontSize = 18.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Welcome to the Dashboard!", fontSize = 16.sp, color = Color.Gray)
    }
}
