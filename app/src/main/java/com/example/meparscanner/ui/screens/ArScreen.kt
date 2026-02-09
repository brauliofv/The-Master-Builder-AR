package com.example.meparscanner.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ArScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        // AR View Placeholder
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("AR View (Camera Feed)")
        }

        // HUD Overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            // Example Validation Warning
            androidx.compose.material3.Card(
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = androidx.compose.ui.graphics.Color.Red.copy(alpha = 0.8f)
                )
            ) {
                Text(
                    text = "⚠ Warning: Outlet too high (1.5m). Standard: 1.10m - 1.20m",
                    color = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
