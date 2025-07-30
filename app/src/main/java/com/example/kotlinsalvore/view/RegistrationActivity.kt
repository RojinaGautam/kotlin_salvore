package com.example.kotlinsalvore.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinsalvore.model.UserModel
import com.example.kotlinsalvore.repository.UserRepositoryImpl
import com.example.kotlinsalvore.viewmodel.UserViewModel

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegBody()
        }
    }
}

@Composable
fun RegBody(innerPaddingValues: PaddingValues = PaddingValues(0.dp)) {
    val context = LocalContext.current
    val repo = remember { 
        UserRepositoryImpl().apply { setContext(context) }
    }
    val userViewModel = remember { UserViewModel(repo) }


    val activity = context as? Activity

    var firstName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // White theme and light red accent
    val primaryRed = Color(0xFFF87171)
    val darkText = Color(0xFF1F2937)
    val lightText = Color(0xFF6B7280)
    val cardBackground = Color.White
    val backgroundColor = Color(0xFFFEF2F2)

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .align(Alignment.Center),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Create Account",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Sign up to get started.",
                        fontSize = 16.sp,
                        color = lightText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    Row {
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            placeholder = { Text("First Name") },
                            modifier = Modifier.weight(1f),
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = primaryRed) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                focusedIndicatorColor = primaryRed,
                                unfocusedContainerColor = Color.White,
                                unfocusedIndicatorColor = Color(0xFFE2E8F0),
                                focusedTextColor = darkText,
                                unfocusedTextColor = darkText
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            placeholder = { Text("Last Name") },
                            modifier = Modifier.weight(1f),
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = primaryRed) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                focusedIndicatorColor = primaryRed,
                                unfocusedContainerColor = Color.White,
                                unfocusedIndicatorColor = Color(0xFFE2E8F0),
                                focusedTextColor = darkText,
                                unfocusedTextColor = darkText
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Enter your email") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = primaryRed) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            focusedIndicatorColor = primaryRed,
                            unfocusedContainerColor = Color.White,
                            unfocusedIndicatorColor = Color(0xFFE2E8F0),
                            focusedTextColor = darkText,
                            unfocusedTextColor = darkText
                        ),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = primaryRed) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            focusedIndicatorColor = primaryRed,
                            unfocusedContainerColor = Color.White,
                            unfocusedIndicatorColor = Color(0xFFE2E8F0),
                            focusedTextColor = darkText,
                            unfocusedTextColor = darkText
                        ),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation(),
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = { Text("Confirm Password") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = primaryRed) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            focusedIndicatorColor = primaryRed,
                            unfocusedContainerColor = Color.White,
                            unfocusedIndicatorColor = Color(0xFFE2E8F0),
                            focusedTextColor = darkText,
                            unfocusedTextColor = darkText
                        ),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation(),
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            when {
                                firstName.isBlank() -> {
                                    Toast.makeText(context, "First name is required", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                lastName.isBlank() -> {
                                    Toast.makeText(context, "Last name is required", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                email.isBlank() -> {
                                    Toast.makeText(context, "Email is required", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                                    Toast.makeText(context, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                password.isBlank() -> {
                                    Toast.makeText(context, "Password is required", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                password.length < 6 -> {
                                    Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                password != confirmPassword -> {
                                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                            }

                            isLoading = true

                            userViewModel.register(email, password) { success, message, userId ->
                                if (success && userId != null) {
                                    val userModel = UserModel(
                                        userId = userId,
                                        email = email,
                                        firstName = firstName,
                                        lastName = lastName,
                                        gender = "Not specified",
                                    )

                                    userViewModel.addUserToDatabase(userId, userModel) { dbSuccess, dbMessage ->
                                        isLoading = false

                                        if (dbSuccess) {
                                            Toast.makeText(context, "Registration successful!", Toast.LENGTH_LONG).show()
                                            val intent = Intent(context, LoginActivity::class.java)
                                            context.startActivity(intent)
                                            activity?.finish()
                                        } else {
                                            Toast.makeText(context, "Registration completed but failed to save user data: $dbMessage", Toast.LENGTH_LONG).show()
                                            val intent = Intent(context, LoginActivity::class.java)
                                            context.startActivity(intent)
                                            activity?.finish()
                                        }
                                    }
                                } else {
                                    isLoading = false
                                    Toast.makeText(context, message ?: "Registration failed", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryRed,
                            contentColor = Color.White
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                        Text(
                            if (isLoading) "Creating Account..." else "Register",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        "Already have an account? Login",
                        modifier = Modifier
                            .clickable(enabled = !isLoading) {
                                val intent = Intent(context, LoginActivity::class.java)
                                context.startActivity(intent)
                                activity?.finish()
                            }
                            .align(Alignment.CenterHorizontally),
                        color = primaryRed,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegPreview() {
    RegBody(innerPaddingValues = PaddingValues(0.dp))
}