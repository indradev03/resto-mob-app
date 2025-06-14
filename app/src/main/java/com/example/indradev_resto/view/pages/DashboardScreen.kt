import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.indradev_resto.R
import androidx.compose.ui.graphics.Color

@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
    val firstName = sharedPreferences.getString("firstName", "Guest")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "👋 Good Morning, $firstName!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6D4C41)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Welcome to IndraDev Resto",
                fontSize = 20.sp,
                color = Color(0xFF8D6E63)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(id = R.drawable.banner),
                contentDescription = "Restaurant",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "🎉 Special Offers",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFD84315)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Image(
                painter = painterResource(id = R.drawable.lunch3),
                contentDescription = "Special Offer",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "🔥 20% off on your first booking! Use code: WELCOME20",
                fontSize = 16.sp,
                color = Color(0xFF5D4037)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "🧑‍🍳 Meet Our Chefs",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF6A1B9A)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Image(
                painter = painterResource(id = R.drawable.lunch4),
                contentDescription = "Chef",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Text(
                text = "Our chefs bring years of culinary excellence to every dish. 👨‍🍳👩‍🍳",
                fontSize = 16.sp,
                color = Color(0xFF4E342E),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "📷 Gallery Highlights",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF00796B)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Dish 1",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f)
                        .height(170.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Image(
                    painter = painterResource(id = R.drawable.facebook),
                    contentDescription = "Dish 2",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f)
                        .height(170.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
