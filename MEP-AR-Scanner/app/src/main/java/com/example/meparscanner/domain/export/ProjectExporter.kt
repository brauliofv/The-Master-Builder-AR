package com.example.meparscanner.domain.export

import com.example.meparscanner.data.repository.InventoryRepository
import com.example.meparscanner.data.repository.StandardHeightRepository
import com.example.meparscanner.domain.model.MepElement
import com.google.gson.Gson
import kotlinx.coroutines.flow.first

data class ProjectExportData(
    val inventory: List<InventoryItem>,
    val standardsUsed: List<StandardHeight>,
    val metadata: ProjectMetadata
)

data class InventoryItem(val name: String, val quantity: Float, val unit: String)
data class StandardHeight(val category: String, val element: String, val range: String)
data class ProjectMetadata(val timestamp: Long, val appVersion: String)

class ProjectExporter(
    private val inventoryRepository: InventoryRepository,
    private val standardHeightRepository: StandardHeightRepository,
    private val gson: Gson
) {

    suspend fun exportProjectToJson(): String {
        // Collect Inventory
        val inventoryEntities = inventoryRepository.allItems.first()
        val inventoryItems = inventoryEntities.map { 
            InventoryItem(it.itemName, it.quantity, it.unit) 
        }

        // Collect Standards (For context in the report)
        // In a real app we might filter by used categories, here we dump all for the LLM to know the rules.
        val kitchenStandards = standardHeightRepository.getHeightsForCategory("Kitchen")
        val bathroomStandards = standardHeightRepository.getHeightsForCategory("Bathroom")
        val generalStandards = standardHeightRepository.getHeightsForCategory("General")
        
        val allStandards = (kitchenStandards + bathroomStandards + generalStandards).map {
            StandardHeight(it.category, it.elementType, "${it.minHeightMeters}-${it.maxHeightMeters}m")
        }

        val exportData = ProjectExportData(
            inventory = inventoryItems,
            standardsUsed = allStandards,
            metadata = ProjectMetadata(System.currentTimeMillis(), "1.0")
        )

        return gson.toJson(exportData)
    }
}
