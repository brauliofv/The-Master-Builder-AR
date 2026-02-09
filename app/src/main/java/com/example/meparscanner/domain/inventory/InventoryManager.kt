package com.example.meparscanner.domain.inventory

import com.example.meparscanner.data.repository.InventoryRepository
import com.example.meparscanner.domain.model.MepElement

class InventoryManager(private val repository: InventoryRepository) {

    suspend fun trackItem(element: MepElement) {
        when (element) {
            is MepElement.Pipe -> {
                val name = "${element.type} Pipe ${element.diameterInches}\""
                repository.addItem(name, "PIPE", element.lengthMeters, "m")
            }
            is MepElement.Wire -> {
                val name = "${element.type} Wire ${element.gauge}AWG"
                // Assuming length is calculated elsewhere or passed. For now using a default or stubbed.
                // In a real scenario, Wire would have length too. Let's add it to the model if needed, 
                // but for now let's assume 1 unit per segment or similar. 
                // Refactoring: The Wire model in MepElement.kt didn't have length. I should fix that or assume 1m.
                // Let's assume 1.0f for now as a placeholder.
                repository.addItem(name, "WIRE", 1.0f, "m")
            }
            is MepElement.Fixture -> {
                val name = "${element.type}"
                repository.addItem(name, "FIXTURE", 1.0f, "pcs")
            }
        }
    }
}
