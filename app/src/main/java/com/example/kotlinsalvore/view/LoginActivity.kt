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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginBody()
        }
    }
}

@Composable
fun LoginBody() {
    val context = LocalContext.current
    val repo = remember { 
        UserRepositoryImpl().apply { setContext(context) }
    }
    val userViewModel = remember { UserViewModel(repo) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }


    val activity = context as Activity

    val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    val localEmail: String = sharedPreferences.getString("email", "").toString()
    val localPassword: String = sharedPreferences.getString("password", "").toString()

    if (email.isEmpty() && localEmail.isNotEmpty()) {
        email = localEmail
    }
    if (password.isEmpty() && localPassword.isNotEmpty()) {
        password = localPassword
        rememberMe = true
    }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Light red theme colors
    val primaryRed = Color(0xFFF87171)
    val accentRed = Color(0xFFFCA5A5)
    val darkText = Color(0xFF1F2937)
    val lightText = Color(0xFF6B7280)
    val cardBackground = Color.White
    val backgroundColor = Color(0xFFFEF2F2)

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
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
                    // Logo section
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(primaryRed)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.seafood_logo),
                            contentDescription = "Salvore Restaurant Logo",
                            modifier = Modifier.size(80.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Welcome to Salvore",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkText,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Sign in to your account",
                        fontSize = 16.sp,
                        color = lightText,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Email field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Enter your email",
                                color = lightText
                            )
                        },
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
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Enter your password",
                                color = lightText
                            )
                        },
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
                                    if (passwordVisibility) R.drawable.baseline_visibility_24
                                    else R.drawable.baseline_visibility_off_24
                                ),
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    passwordVisibility = !passwordVisibility
                                },
                                tint = primaryRed
                            )
                        },
                        visualTransformation = if (passwordVisibility) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        enabled = !isLoading
                    )

                    // Remember me and forgot password row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                colors = CheckboxDefaults.colors(
                                    checkedColor = primaryRed,
                                    checkmarkColor = Color.White
                                ),
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                enabled = !isLoading
                            )
                            Text(
                                "Remember me",
                                color = darkText,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            "Forgot Password?",
                            modifier = Modifier.clickable(enabled = !isLoading) {
                                val intent = Intent(context, ForgetPasswordActivity::class.java)
                                context.startActivity(intent)
                            },
                            color = primaryRed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sign in button
                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Please fill in all fields")
                                }
                                return@Button
                            }

                            isLoading = true

                            userViewModel.login(email, password) { success, message ->
                                isLoading = false

                                if (success) {
                                    if (rememberMe) {
                                        editor.putString("email", email)
                                        editor.putString("password", password)
                                        editor.apply()
                                    } else {
                                        editor.remove("email")
                                        editor.remove("password")
                                        editor.apply()
                                    }

                                    val intent = Intent(context, DashboardActivity::class.java)
                                    intent.putExtra("email", email)
                                    intent.putExtra("password", password)
                                    context.startActivity(intent)
                                    activity.finish()

                                    Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(message ?: "Login failed. Please try again.")
                                    }
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
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.padding(end = 8.dp))
                        }
                        Text(
                            if (isLoading) "Signing in..." else "Sign In",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sign up link
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Don't have an account? ",
                            color = lightText,
                            fontSize = 14.sp
                        )
                        Text(
                            "Sign up",
                            modifier = Modifier.clickable(enabled = !isLoading) {
                                val intent = Intent(context, RegistrationActivity::class.java)
                                context.startActivity(intent)
                                activity.finish()
                            },
                            color = primaryRed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewLogin() {
    LoginBody()
}