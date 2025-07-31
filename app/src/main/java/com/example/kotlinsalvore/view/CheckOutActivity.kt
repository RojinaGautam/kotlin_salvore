package com.example.kotlinsalvore.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinsalvore.R
import com.example.kotlinsalvore.model.CartModel
import com.example.kotlinsalvore.repository.CartRepositoryImpl
import com.example.kotlinsalvore.viewmodel.CartViewModel
import androidx.compose.runtime.livedata.observeAsState
import android.widget.Toast // For displaying short messages

data class CartItem(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val image: Int = 0, // Note: Int for drawable resource ID cannot be directly saved to Firebase
    var quantity: Int = 0
)

// New data class for Firebase, if you want to store a URL or name for the image
data class CartItemFirebase(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "", // Store a URL or a unique name to identify the image
    var quantity: Int = 0
)


// New data class to represent an entire order for Firebase
data class Order(
    val orderId: String = "",
    val userId: String = "guest", // Replace with actual user ID if authentication is implemented
    val cartItems: List<CartItemFirebase> = emptyList(), // Use CartItemFirebase here
    val selectedDeliveryOption: String = "",
    val selectedPaymentMethod: String = "",
    val promoCodeApplied: String = "",
    val isPromoApplied: Boolean = false,
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val promoDiscount: Double = 0.0,
    val tax: Double = 0.0,
    val totalAmount: Double = 0.0,
    val orderTimestamp: Long = System.currentTimeMillis()
)

class CheckoutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CheckoutScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen() {
    val context = LocalContext.current
    val activity = context as? Activity
    val scrollState = rememberScrollState()

    // Initialize cart repository and view model
    val cartRepository = remember { 
        CartRepositoryImpl().apply { setContext(context) }
    }
    val cartViewModel = remember { CartViewModel(cartRepository) }
    
    // Observe cart data
    val cart = cartViewModel.cart.observeAsState(initial = CartModel())
    val isLoading = cartViewModel.isLoading.observeAsState(initial = true)
    
    // Load cart when screen is displayed
    LaunchedEffect(Unit) {
        cartViewModel.loadCart()
    }
    
    // Convert cart items to the format expected by the UI
    val cartItems = cart.value.items.map { item ->
        CartItem(
            id = item.productId,
            name = item.productName,
            price = item.productPrice,
            image = R.drawable.image, // Default image
            quantity = item.quantity
        )
    }

    var selectedDeliveryOption by remember { mutableStateOf("delivery") }
    var selectedPaymentMethod by remember { mutableStateOf("card") }
    var promoCode by remember { mutableStateOf("") }
    var isPromoApplied by remember { mutableStateOf(false) }

    val subtotal = cartItems.sumOf { it.price * it.quantity }
    val deliveryFee = if (selectedDeliveryOption == "delivery") 2.99 else 0.0
    val promoDiscount = if (isPromoApplied) subtotal * 0.1 else 0.0
    val tax = (subtotal + deliveryFee - promoDiscount) * 0.08
    val total = subtotal + deliveryFee - promoDiscount + tax

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Checkout",
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
                ),
                actions = {
                    IconButton(
                        onClick = {
                            cartViewModel.clearCart()
                            Toast.makeText(context, "Cart cleared", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear Cart",
                            tint = Color.Black
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total: $${String.format("%.2f", total)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Button(
                            onClick = {
                                if (cartItems.isEmpty()) {
                                    Toast.makeText(context, "Your cart is empty!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                
                                // Clear the cart and show success message
                                cartViewModel.clearCart()
                                Toast.makeText(context, "Order Placed Successfully! Total: $${String.format("%.2f", total)}", Toast.LENGTH_LONG).show()
                                activity?.finish() // Go back after successful order
                            },
                            modifier = Modifier
                                .height(48.dp)
                                .width(140.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Place Order",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // Order Items Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Your Order",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isLoading.value) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFFFF6B35),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else if (cartItems.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Your cart is empty",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        cartItems.forEachIndexed { index, item ->
                            CartItemRow(
                                item = item,
                                onQuantityChange = { newQuantity ->
                                    if (newQuantity <= 0) {
                                        cartViewModel.removeFromCart(item.id)
                                    } else {
                                        cartViewModel.updateQuantity(item.id, newQuantity)
                                    }
                                }
                            )
                            if (index < cartItems.size - 1) {
                                Divider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = Color(0xFFE5E7EB)
                                )
                            }
                        }
                    }
                }
            }

            // Delivery Options Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Delivery Options",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    DeliveryOption(
                        title = "Delivery",
                        subtitle = "25-35 min • $2.99",
                        icon = Icons.Default.LocationOn,
                        isSelected = selectedDeliveryOption == "delivery",
                        onClick = { selectedDeliveryOption = "delivery" }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DeliveryOption(
                        title = "Pickup",
                        subtitle = "15-20 min • Free",
                        icon = Icons.Default.Person,
                        isSelected = selectedDeliveryOption == "pickup",
                        onClick = { selectedDeliveryOption = "pickup" }
                    )
                }
            }

            // Payment Method Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Payment Method",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PaymentOption(
                        title = "Credit/Debit Card",
                        subtitle = "•••• •••• •••• 1234",
                        icon = Icons.Default.Search, // This icon might not be appropriate for "Credit/Debit Card"
                        isSelected = selectedPaymentMethod == "card",
                        onClick = { selectedPaymentMethod = "card" }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    PaymentOption(
                        title = "Cash on Delivery",
                        subtitle = "Pay when you receive",
                        icon = Icons.Default.Person, // This icon might not be appropriate for "Cash on Delivery"
                        isSelected = selectedPaymentMethod == "cash",
                        onClick = { selectedPaymentMethod = "cash" }
                    )
                }
            }

            // Promo Code Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Promo Code",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = promoCode,
                            onValueChange = { promoCode = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Enter promo code") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFF6B35),
                                unfocusedBorderColor = Color(0xFFE5E7EB)
                            )
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = {
                                if (promoCode.lowercase() == "save10") {
                                    isPromoApplied = true
                                    Toast.makeText(context, "Promo code 'SAVE10' applied!", Toast.LENGTH_SHORT).show()
                                } else {
                                    isPromoApplied = false
                                    Toast.makeText(context, "Invalid promo code.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Apply")
                        }
                    }

                    if (isPromoApplied) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "✓ Promo code applied! 10% discount",
                            fontSize = 12.sp,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Order Summary Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Order Summary",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SummaryRow("Subtotal", subtotal)
                    SummaryRow("Delivery Fee", deliveryFee)
                    if (isPromoApplied) {
                        SummaryRow("Promo Discount", -promoDiscount, isDiscount = true)
                    }
                    SummaryRow("Tax", tax)

                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFFE5E7EB)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "$${String.format("%.2f", total)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6B35)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = item.image),
            contentDescription = item.name,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Text(
                text = "$${String.format("%.2f", item.price)}",
                fontSize = 14.sp,
                color = Color(0xFFFF6B35),
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = {
                    if (item.quantity > 1) {
                        onQuantityChange(item.quantity - 1)
                    }
                },
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        Color(0xFFF3F4F6),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.Clear, // Changed to Clear to signify decrease
                    contentDescription = "Decrease",
                    tint = Color(0xFFFF6B35),
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = item.quantity.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(24.dp)
            )

            IconButton(
                onClick = { onQuantityChange(item.quantity + 1) },
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        Color(0xFFFF6B35),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun DeliveryOption(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFFFF6B35) else Color(0xFFE5E7EB),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFF7ED) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = if (isSelected) Color(0xFFFF6B35) else Color.Gray,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            if (isSelected) {
                Icon(
                    Icons.Default.Add, // Consider using Icons.Default.Check or similar for a selection indicator
                    contentDescription = "Selected",
                    tint = Color(0xFFFF6B35),
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            Color(0xFFFF6B35),
                            CircleShape
                        )
                        .padding(2.dp)
                )
            }
        }
    }
}

@Composable
fun PaymentOption(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector, // Consider more appropriate icons for payment methods
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFFFF6B35) else Color(0xFFE5E7EB),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFF7ED) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = if (isSelected) Color(0xFFFF6B35) else Color.Gray,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFFFF6B35)
                )
            )
        }
    }
}

@Composable
fun SummaryRow(
    label: String,
    amount: Double,
    isDiscount: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = if (isDiscount) "-$${String.format("%.2f", amount)}" else "$${String.format("%.2f", amount)}",
            fontSize = 14.sp,
            color = if (isDiscount) Color(0xFF10B981) else Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CheckoutScreenPreview() {
    CheckoutScreen()
}