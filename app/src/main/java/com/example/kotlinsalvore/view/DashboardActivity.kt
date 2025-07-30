package com.example.kotlinsalvore.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinsalvore.R

// Define colors
val primaryRed = Color(0xFFD32F2F)
val darkText = Color(0xFF3E2723)
val lightText = Color(0xFFBF360C)
val cardBackground = Color(0xFFFFF3E0)
val whiteBackground = Color(0xFFFFFFFF)
val blackText = Color(0xFF000000)
val grayText = Color(0xFF616161)
val lightGrayBackground = Color(0xFFF5F5F5)

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody() {
    val context = LocalContext.current
    val activity = context as? Activity

    val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
    val userName: String = sharedPreferences.getString("username", "Guest").toString()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Salvore Restaurant",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = blackText
                    )
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "Cart Clicked!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Shopping Cart",
                            tint = blackText
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = whiteBackground,
                    titleContentColor = blackText
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = whiteBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Home Icon
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = {
                            Toast.makeText(context, "Home Clicked!", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.Home, contentDescription = "Home", tint = primaryRed)
                        }
                        Text("Home", style = MaterialTheme.typography.labelSmall, color = primaryRed)
                    }

                    // Menu Icon
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = {
                            val intent = Intent(context, MenuActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = grayText)
                        }
                        Text("Menu", style = MaterialTheme.typography.labelSmall, color = grayText)
                    }
                    
                    // Product Management Icon
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = {
                            val intent = Intent(context, ProductManagementActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Manage Products", tint = grayText)
                        }
                        Text("Manage", style = MaterialTheme.typography.labelSmall, color = grayText)
                    }

                    // Profile Icon
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = {
                            val intent = Intent(context, ProfileActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Icon(Icons.Default.Person, contentDescription = "Profile", tint = grayText)
                        }
                        Text("Profile", style = MaterialTheme.typography.labelSmall, color = grayText)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(whiteBackground)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Welcome back, $userName!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Normal,
                color = blackText
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = whiteBackground)
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.image),
                        contentDescription = "Featured Seafood Platter",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Featured Seafood Platter",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = blackText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enjoy a curated selection of our freshest catches, expertly prepared and served with our signature sauces.",
                            fontSize = 14.sp,
                            color = grayText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Available today only",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = primaryRed
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    DashboardBody()
}
