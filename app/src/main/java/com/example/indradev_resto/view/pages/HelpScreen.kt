import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HelpScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                text = "Help & Support",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                Text(
                    text = "📚 Help Center",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                HelpSection(
                    title = "📅 Booking a Table",
                    points = listOf(
                        "Go to the 'Tables' section from the main menu.",
                        "Browse the list of available tables.",
                        "Tap the 'Book' button next to the table you want.",
                        "Fill in your details like name, contact, and booking date.",
                        "Confirm your booking and wait for the confirmation message."
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                HelpSection(
                    title = "👤 Managing Your Profile",
                    points = listOf(
                        "Tap the profile icon on the top-right corner.",
                        "Update your personal information anytime.",
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                HelpSection(
                    title = "⭐ App Features",
                    points = listOf(
                        "Use the menu icon (⋮) to navigate quickly to Menu, Help, or Logout.",
                        "Keep the app updated to enjoy the latest features and improvements."
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                HelpSection(
                    title = "⚠️ Troubleshooting",
                    points = listOf(
                        "If the app crashes or behaves unexpectedly, try restarting it.",
                        "Check your internet connection.",
                        "Clear app cache from device settings if issues persist."
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    tonalElevation = 2.dp,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "📞 Contact Support",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Email: support@indradevresto.com",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Phone: +1-800-555-RESTO",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Available Monday to Friday, 9 AM to 6 PM.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "We’re always here to help you enjoy the best dining experience! 😊",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun HelpSection(title: String, points: List<String>) {
    Surface(
        tonalElevation = 4.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            points.forEach { point ->
                Row(
                    modifier = Modifier.padding(bottom = 6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = point,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
