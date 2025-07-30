package com.example.kotlinsalvore.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinsalvore.R
import com.example.kotlinsalvore.repository.UserRepositoryImpl
import com.example.kotlinsalvore.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProfileBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBody() {
    val context = LocalContext.current
    val repo = remember { 
        UserRepositoryImpl().apply { setContext(context) }
    }
    val userViewModel = remember { UserViewModel(repo) }

    // Personal Details State
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // Password State
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Visibility State
    var currentPasswordVisibility by remember { mutableStateOf(false) }
    var newPasswordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }

    // Loading State
    var isUpdatingDetails by remember { mutableStateOf(false) }
    var isUpdatingPassword by remember { mutableStateOf(false) }


    val activity = context as Activity
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Load user data from shared preferences or intent
    LaunchedEffect(Unit) {
        val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
        email = sharedPreferences.getString("email", "") ?: ""
        firstName = sharedPreferences.getString("firstName", "") ?: ""
        lastName = sharedPreferences.getString("lastName", "") ?: ""
        gender = sharedPreferences.getString("gender", "") ?: ""
        address = sharedPreferences.getString("address", "") ?: ""
    }

    // Light red theme colors
    val primaryRed = Color(0xFFF87171)
    val accentRed = Color(0xFFFCA5A5)
    val darkText = Color(0xFF1F2937)
    val lightText = Color(0xFF6B7280)
    val cardBackground = Color.White
    val backgroundColor = Color(0xFFFEF2F2)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile Settings",
                        color = darkText,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { activity.finish() }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = primaryRed
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Picture Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(primaryRed)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.seafood_logo),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(60.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (firstName.isNotEmpty() || lastName.isNotEmpty()) "$firstName $lastName" else "User Name",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkText
                    )

                    Text(
                        text = email,
                        fontSize = 14.sp,
                        color = lightText
                    )
                }
            }

            // Personal Details Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Personal Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkText
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // First Name field
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("First Name", color = lightText) },
                        placeholder = { Text("Enter your first name", color = lightText) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFFEF2F2),
                            focusedIndicatorColor = primaryRed,
                            unfocusedContainerColor = Color(0xFFFEF2F2),
                            unfocusedIndicatorColor = Color(0xFFE5E7EB),
                            focusedTextColor = darkText,
                            unfocusedTextColor = darkText
                        ),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = primaryRed
                            )
                        },
                        enabled = !isUpdatingDetails
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Last Name field
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Last Name", color = lightText) },
                        placeholder = { Text("Enter your last name", color = lightText) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFFEF2F2),
                            focusedIndicatorColor = primaryRed,
                            unfocusedContainerColor = Color(0xFFFEF2F2),
                            unfocusedIndicatorColor = Color(0xFFE5E7EB),
                            focusedTextColor = darkText,
                            unfocusedTextColor = darkText
                        ),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = primaryRed
                            )
                        },
                        enabled = !isUpdatingDetails
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Email", color = lightText) },
                        placeholder = { Text("Enter your email", color = lightText) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFFEF2F2),
                            focusedIndicatorColor = primaryRed,
                            unfocusedContainerColor = Color(0xFFFEF2F2),
                            unfocusedIndicatorColor = Color(0xFFE5E7EB),
                            focusedTextColor = darkText,
                            unfocusedTextColor = darkText
                        ),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = primaryRed
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        enabled = !isUpdatingDetails
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Gender field
                    OutlinedTextField(
                        value = gender,
                        onValueChange = { gender = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Gender", color = lightText) },
                        placeholder = { Text("Enter your gender", color = lightText) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFFEF2F2),
                            focusedIndicatorColor = primaryRed,
                            unfocusedContainerColor = Color(0xFFFEF2F2),
                            unfocusedIndicatorColor = Color(0xFFE5E7EB),
                            focusedTextColor = darkText,
                            unfocusedTextColor = darkText
                        ),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = primaryRed
                            )
                        },
                        enabled = !isUpdatingDetails
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Address field
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Address", color = lightText) },
                        placeholder = { Text("Enter your address", color = lightText) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFFEF2F2),
                            focusedIndicatorColor = primaryRed,
                            unfocusedContainerColor = Color(0xFFFEF2F2),
                            unfocusedIndicatorColor = Color(0xFFE5E7EB),
                            focusedTextColor = darkText,
                            unfocusedTextColor = darkText
                        ),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = primaryRed
                            )
                        },
                        enabled = !isUpdatingDetails
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Update Details Button
                    Button(
                        onClick = {
                            if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || gender.isBlank() || address.isBlank()) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Please fill in all fields")
                                }
                                return@Button
                            }

                            isUpdatingDetails = true

                            // Simulate API call - replace with actual implementation
                            userViewModel.updateUserDetails(firstName, lastName, email, gender, address) { success, message ->
                                isUpdatingDetails = false

                                if (success) {
                                    // Save to shared preferences
                                    val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
                                    val editor = sharedPreferences.edit()
                                    editor.putString("firstName", firstName)
                                    editor.putString("lastName", lastName)
                                    editor.putString("email", email)
                                    editor.putString("gender", gender)
                                    editor.putString("address", address)
                                    editor.apply()

                                    Toast.makeText(context, "Details updated successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(message ?: "Failed to update details")
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryRed,
                            contentColor = Color.White
                        ),
                        enabled = !isUpdatingDetails
                    ) {
                        if (isUpdatingDetails) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            if (isUpdatingDetails) "Updating..." else "Update Details",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Change Password Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Change Password",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkText
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Current Password field
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Current Password", color = lightText) },
                        placeholder = { Text("Enter current password", color = lightText) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFFEF2F2),
                            focusedIndicatorColor = primaryRed,
                            unfocusedContainerColor = Color(0xFFFEF2F2),
                            unfocusedIndicatorColor = Color(0xFFE5E7EB),
                            focusedTextColor = darkText,
                            unfocusedTextColor = darkText
                        ),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = primaryRed
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(
                                    if (currentPasswordVisibility) R.drawable.baseline_visibility_24
                                    else R.drawable.baseline_visibility_off_24
                                ),
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    currentPasswordVisibility = !currentPasswordVisibility
                                },
                                tint = primaryRed
                            )
                        },
                        visualTransformation = if (currentPasswordVisibility) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        enabled = !isUpdatingPassword
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // New Password field
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("New Password", color = lightText) },
                        placeholder = { Text("Enter new password", color = lightText) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFFEF2F2),
                            focusedIndicatorColor = primaryRed,
                            unfocusedContainerColor = Color(0xFFFEF2F2),
                            unfocusedIndicatorColor = Color(0xFFE5E7EB),
                            focusedTextColor = darkText,
                            unfocusedTextColor = darkText
                        ),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = primaryRed
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(
                                    if (newPasswordVisibility) R.drawable.baseline_visibility_24
                                    else R.drawable.baseline_visibility_off_24
                                ),
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    newPasswordVisibility = !newPasswordVisibility
                                },
                                tint = primaryRed
                            )
                        },
                        visualTransformation = if (newPasswordVisibility) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        enabled = !isUpdatingPassword
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Confirm Password field
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Confirm New Password", color = lightText) },
                        placeholder = { Text("Confirm new password", color = lightText) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFFEF2F2),
                            focusedIndicatorColor = primaryRed,
                            unfocusedContainerColor = Color(0xFFFEF2F2),
                            unfocusedIndicatorColor = Color(0xFFE5E7EB),
                            focusedTextColor = darkText,
                            unfocusedTextColor = darkText
                        ),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = primaryRed
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(
                                    if (confirmPasswordVisibility) R.drawable.baseline_visibility_24
                                    else R.drawable.baseline_visibility_off_24
                                ),
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    confirmPasswordVisibility = !confirmPasswordVisibility
                                },
                                tint = primaryRed
                            )
                        },
                        visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        enabled = !isUpdatingPassword
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Update Password Button
                    Button(
                        onClick = {
                            if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Please fill in all password fields")
                                }
                                return@Button
                            }

                            if (newPassword != confirmPassword) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("New passwords do not match")
                                }
                                return@Button
                            }

                            if (newPassword.length < 6) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Password must be at least 6 characters")
                                }
                                return@Button
                            }

                            isUpdatingPassword = true

                            // Simulate API call - replace with actual implementation
                            userViewModel.updatePassword(currentPassword, newPassword) { success, message ->
                                isUpdatingPassword = false

                                if (success) {
                                    // Clear password fields
                                    currentPassword = ""
                                    newPassword = ""
                                    confirmPassword = ""

                                    Toast.makeText(context, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(message ?: "Failed to update password")
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryRed,
                            contentColor = Color.White
                        ),
                        enabled = !isUpdatingPassword
                    ) {
                        if (isUpdatingPassword) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            if (isUpdatingPassword) "Updating..." else "Update Password",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
fun PreviewProfile() {
    ProfileBody()
}