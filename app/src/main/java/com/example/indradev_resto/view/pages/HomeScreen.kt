package com.example.indradev_resto.view.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.indradev_resto.R
import com.example.indradev_resto.viewmodel.AdminHomeViewModel

@Composable
fun AdminHomeScreen(viewModel: AdminHomeViewModel = viewModel()) {
    val userCount = viewModel.userCount
    val tableCount = viewModel.tableCount
    val bookingCount = viewModel.bookingCount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "👋 Welcome to Admin Dashboard",
            style = MaterialTheme.typography.headlineSmall
        )

        // Count Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CountCard(title = "Users", count = userCount, color = Color(0xFF4CAF50))
            CountCard(title = "Tables", count = tableCount, color = Color(0xFFFF9800))
            CountCard(title = "Bookings", count = bookingCount, color = Color(0xFF2196F3))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Banner Image (put your image in res/drawable and use painterResource)
        Image(
            painter = painterResource(id = R.drawable.gallery1),
            contentDescription = "Special Offers Banner",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 8.dp),
            contentScale = ContentScale.Crop,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Special Offers Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFfce4ec)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🔥 Special Offers",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFD81B60)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Get exclusive discounts on bookings and tables this month! Hurry up and grab the best deals.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF880E4F),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun CountCard(title: String, count: Int, color: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(100.dp)
            .height(100.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = title, color = Color.White)
            Text(
                text = count.toString(),
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
