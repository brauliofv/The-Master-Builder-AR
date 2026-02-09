package com.example.meparscanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.meparscanner.data.repository.InventoryRepository
import kotlinx.coroutines.launch

@Composable
fun ArScreen(inventoryRepository: InventoryRepository) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val categories = listOf("Electrical ⚡", "Water 💧")
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // AR View Placeholder
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("AR View (Camera Feed)")
        }

        // Category Selector (Top)
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 32.dp, start = 16.dp, end = 16.dp)
        ) {
            categories.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        // HUD Overlay & Simulation Button
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Simulated Placement Button
            Button(
                onClick = {
                    scope.launch {
                        if (selectedTabIndex == 0) {
                            inventoryRepository.addItem("Outlet Box (Switch)", "ELECTRICAL", 1f, "pcs")
                        } else {
                            inventoryRepository.addItem("PVC Pipe 1/2\"", "WATER", 1.5f, "m")
                        }
                    }
                },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text("Simulate Placement")
            }

            // Category-specific Warning
            val warningText = if (selectedTabIndex == 0) {
                "⚠ Warning: Outlet too high (1.5m). Standard: 1.10m - 1.20m"
            } else {
                "⚠ Warning: Hot water pipe (Red) detected too close to Cold (Blue)"
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.Red.copy(alpha = 0.8f)
                )
            ) {
                Text(
                    text = warningText,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
