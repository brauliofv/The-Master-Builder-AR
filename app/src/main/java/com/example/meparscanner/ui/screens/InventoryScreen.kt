package com.example.meparscanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.meparscanner.data.repository.InventoryRepository

@Composable
fun InventoryScreen(inventoryRepository: InventoryRepository) {
    val inventoryItems by inventoryRepository.allItems.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Project Inventory",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (inventoryItems.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No items tracked yet.")
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(inventoryItems) { item ->
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = item.itemName, style = MaterialTheme.typography.titleMedium)
                            Text(text = "${item.quantity} ${item.unit}", style = MaterialTheme.typography.bodyLarge)
                        }
                        Text(text = "Type: ${item.itemType}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                        Divider(modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }
    }
}
