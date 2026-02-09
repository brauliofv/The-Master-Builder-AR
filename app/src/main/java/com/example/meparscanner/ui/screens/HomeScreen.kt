package com.example.meparscanner.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onNavigateToAr: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToFeedback: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onNavigateToAr, modifier = Modifier.padding(8.dp)) {
            Text("Start AR Scan")
        }
        Button(onClick = onNavigateToInventory, modifier = Modifier.padding(8.dp)) {
            Text("View Inventory")
        }
        Button(onClick = onNavigateToFeedback, modifier = Modifier.padding(8.dp)) {
            Text("Feedback & Export")
        }
    }
}
