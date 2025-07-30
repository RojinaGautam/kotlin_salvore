package com.example.kotlinsalvore.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplashBody()
        }
    }
}

@Composable
fun SplashBody() {
    val context = LocalContext.current
    val activity = context as Activity

    // Define colors for the white theme with light red accent
    val primaryWhite = Color(0xFFFFFFFF) // Pure white for background
    val lightRed = Color(0xFFF87171)     // Light red for accent
    val darkText = Color(0xFF1F2937)     // Dark text for title
    val mediumGrayText = Color(0xFF6B7280) // Medium gray for subtitle

    LaunchedEffect(Unit) {
        delay(3000) // 3-second delay

        // Always navigate to LoginActivity after delay
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        activity.finish()
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(primaryWhite),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Salvore Seafood",
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = darkText
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Taste the ocean's bounty",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = mediumGrayText
            )

            Spacer(modifier = Modifier.height(50.dp))

            CircularProgressIndicator(
                color = lightRed
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSplash() {
    SplashBody()
}
