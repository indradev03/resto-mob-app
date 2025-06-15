package com.example.indradev_resto.view.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.indradev_resto.R

@Composable
fun MenuScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        TopNavigationBar()
        ProductCategoryList()
    }
}

@Composable
fun TopNavigationBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF6200EE))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(" Our Menu", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

data class Product(val name: String, val description: String, val price: String, val imageRes: Int)

@Composable
fun ProductCategoryList() {
    val specialDishes = listOf(
        Product("Chef's Delight", "House special with mixed ingredients", "$12", R.drawable.breakfast1),
        Product("Signature Platter", "Best of everything", "$15", R.drawable.breakfast3),
        Product("Special Combo", "Combo meal with drinks", "$14", R.drawable.lunch4),
        Product("Pineapple", "Tart pineapple", "$6", R.drawable.breakfast6),
        Product("Watermelon", "Refreshing watermelon", "$8", R.drawable.lunch4),
        Product("Blueberry", "Small blueberries", "$9", R.drawable.lunch5),
        Product("Kiwi", "Tart kiwi fruit", "$10", R.drawable.dinner6)
    )
    val appetizers = listOf(
        Product("Apple", "Fresh red apple", "$2", R.drawable.breakfast1),
        Product("Banana", "Sweet yellow banana", "$1", R.drawable.breakfast2),
        Product("Pineapple", "Tart pineapple", "$6", R.drawable.breakfast6),
        Product("Watermelon", "Refreshing watermelon", "$8", R.drawable.lunch4),
        Product("Blueberry", "Small blueberries", "$9", R.drawable.lunch5),
        Product("Kiwi", "Tart kiwi fruit", "$10", R.drawable.dinner6)
    )
    val halfMeals = listOf(
        Product("Orange", "Juicy orange fruit", "$3", R.drawable.breakfast3),
        Product("Grapes", "Bunch of grapes", "$4", R.drawable.breakfast4),
        Product("Pineapple", "Tart pineapple", "$6", R.drawable.breakfast6),
        Product("Watermelon", "Refreshing watermelon", "$8", R.drawable.lunch4),
        Product("Blueberry", "Small blueberries", "$9", R.drawable.lunch5),
        Product("Kiwi", "Tart kiwi fruit", "$10", R.drawable.dinner6)
    )
    val mainCourses = listOf(
        Product("Mango", "Tropical mango", "$5", R.drawable.breakfast5),
        Product("Pineapple", "Tart pineapple", "$6", R.drawable.breakfast6),
        Product("Watermelon", "Refreshing watermelon", "$8", R.drawable.lunch4),
        Product("Blueberry", "Small blueberries", "$9", R.drawable.lunch5),
        Product("Kiwi", "Tart kiwi fruit", "$10", R.drawable.dinner6)
    )
    val dinners = listOf(
        Product("Strawberry", "Sweet strawberries", "$7", R.drawable.lunch3),
        Product("Watermelon", "Refreshing watermelon", "$8", R.drawable.lunch4),
        Product("Blueberry", "Small blueberries", "$9", R.drawable.lunch5),
        Product("Kiwi", "Tart kiwi fruit", "$10", R.drawable.dinner6)
    )

    val selectedProduct = remember { mutableStateOf<Product?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
    ) {
        SpecialDishesSection(specialDishes) { selectedProduct.value = it }
        ProductSection("Appetizers", appetizers) { selectedProduct.value = it }
        ProductSection("Half Meals", halfMeals) { selectedProduct.value = it }
        ProductSection("Main Courses", mainCourses) { selectedProduct.value = it }
        ProductSection("Dinners", dinners) { selectedProduct.value = it }
    }

    selectedProduct.value?.let {
        ProductDetailDialog(product = it, onDismiss = { selectedProduct.value = null })
    }
}

@Composable
fun SpecialDishesSection(products: List<Product>, onProductClick: (Product) -> Unit) {
    Text(
        text = "🔥 Special Dishes",
        fontSize = 22.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFFD32F2F),
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        items(products) { product ->
            SpecialProductItem(product, onClick = { onProductClick(product) })
        }
    }
}

@Composable
fun SpecialProductItem(product: Product, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(200.dp)
            .background(Color(0xFFFFF3E0), shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = product.imageRes),
            contentDescription = product.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(product.name, color = Color.Black, fontWeight = FontWeight.SemiBold)
        Text(product.description, color = Color.Gray)
        Text(product.price, color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProductSection(title: String, products: List<Product>, onProductClick: (Product) -> Unit) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black, // set heading color to black
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        items(products) { product ->
            ProductItem(
                product = product,
                onClick = { onProductClick(product) },
                modifier = Modifier.width(180.dp)
            )
        }
    }
}

@Composable
fun ProductItem(product: Product, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = product.imageRes),
            contentDescription = product.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(product.name, color = Color.Black, fontWeight = FontWeight.SemiBold)
        Text(product.description, color = Color.Gray)
        Text(product.price, color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProductDetailDialog(product: Product, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        title = {
            Text(text = product.name, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = product.description)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Price: ${product.price}", fontWeight = FontWeight.Bold)
            }
        }
    )
}
