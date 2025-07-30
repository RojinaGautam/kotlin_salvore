package com.example.kotlinsalvore.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinsalvore.R
import com.example.kotlinsalvore.model.ProductModel
import com.example.kotlinsalvore.repository.ProductRepositoryImpl
import com.example.kotlinsalvore.viewmodel.ProductViewModel

data class MenuItem(
    val id: String,
    val name: String,
    val price: String,
    val description: String,
    val imageResId: Int
)

class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MenuScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen() {
    val context = LocalContext.current
    val activity = context as? Activity


    val productRepository = remember { ProductRepositoryImpl() }
    val context2 = LocalContext.current
    val productViewModel = remember { ProductViewModel(productRepository) }
    
    val allProducts = productViewModel.allProducts.observeAsState(initial = emptyList())
    val isLoading = productViewModel.isLoading.observeAsState(initial = true)
    
    // Load products when the screen is first displayed
    LaunchedEffect(Unit) {
        productViewModel.getAllProduct()
    }
    
    // Refresh products when returning to this screen
    LaunchedEffect(Unit) {
        productViewModel.getAllProduct()
    }
    
    // Fallback menu items if no products in database
    val fallbackMenuItems = listOf(
        MenuItem("1", "Grilled Salmon", "$18.99", "Fresh salmon fillet, grilled to perfection, served with lemon butter sauce.", R.drawable.grilledsalmon),
        MenuItem("2", "Lobster Bisque", "$12.50", "Creamy soup with chunks of lobster, seasoned with herbs and spices.", R.drawable.lobsterbisque),
        MenuItem("3", "Shrimp Scampi", "$16.75", "Sautéed shrimp in garlic butter sauce, served over linguine.", R.drawable.shrimp),
        MenuItem("4", "Crab Cakes", "$14.25", "Pan-seared crab cakes with a tangy remoulade sauce.", R.drawable.crab),
        MenuItem("5", "Oysters on the Half Shell", "$22.00", "Fresh oysters served with mignonette sauce and lemon wedges.", R.drawable.oyster)
    )
    
    // Use database products if available, otherwise use fallback
    val menuItems = if (allProducts.value.isNotEmpty()) {
        allProducts.value.mapNotNull { product ->
            product?.let {
                MenuItem(
                    id = it.productId,
                    name = it.productName,
                    price = "$${it.productPrice}",
                    description = it.productDesc,
                    imageResId = R.drawable.grilledsalmon // Default image, can be enhanced with actual image URLs
                )
            }
        }
    } else {
        fallbackMenuItems
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Menu",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val intent = Intent(context, AddproductActivity::class.java)
                    context.startActivity(intent)
                },
                containerColor = Color(0xFFFF6B35),
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Item", fontWeight = FontWeight.SemiBold)
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                val items = listOf("Home", "Menu", "Profile")
                val icons = listOf(Icons.Default.Home, Icons.Default.Menu, Icons.Default.Person)
                val selectedItem = 1 // Menu is selected

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                icons[index],
                                contentDescription = item,
                                tint = if (index == selectedItem) Color.Black else Color.Gray
                            )
                        },
                        label = {
                            Text(
                                item,
                                color = if (index == selectedItem) Color.Black else Color.Gray,
                                fontSize = 12.sp
                            )
                        },
                        selected = index == selectedItem,
                        onClick = {
                            when (item) {
                                "Home" -> {
                                    val intent = Intent(context, SplashActivity::class.java)
                                    context.startActivity(intent)
                                    activity?.finish()
                                }
                                "Menu" -> {
                                    // Already on Menu screen
                                }
                                "Profile" -> {
                                    val intent = Intent(context, DashboardActivity::class.java)
                                    context.startActivity(intent)
                                    activity?.finish()
                                }
                            }
                        }
                    )
                }
            }
        }
            ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Seafood",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            
            if (isLoading.value) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFFF6B35),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            items(menuItems) { item ->
                MenuItemCard(
                    item = item,
                    onClick = {
                        val intent = Intent(context, ProductDetailsActivity::class.java).apply {
                            putExtra("ITEM_ID", item.id)
                            putExtra("ITEM_NAME", item.name)
                            putExtra("ITEM_PRICE", item.price)
                            putExtra("ITEM_DESCRIPTION", item.description)
                            putExtra("ITEM_IMAGE", item.imageResId)
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun MenuItemCard(item: MenuItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.price,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 3
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = item.name,
                modifier = Modifier
                    .size(96.dp)
                    .background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    MenuScreen()
}
