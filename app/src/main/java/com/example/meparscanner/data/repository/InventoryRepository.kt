package com.example.meparscanner.data.repository

import com.example.meparscanner.data.dao.InventoryDao
import com.example.meparscanner.data.model.InventoryItemEntity
import kotlinx.coroutines.flow.Flow

class InventoryRepository(private val dao: InventoryDao) {

    val allItems: Flow<List<InventoryItemEntity>> = dao.getAllInventoryItems()

    suspend fun addItem(name: String, type: String, quantity: Float, unit: String) {
        val existingItem = dao.getItemByName(name)
        if (existingItem != null) {
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)
            dao.updateItem(updatedItem)
        } else {
            val newItem = InventoryItemEntity(
                itemName = name,
                itemType = type,
                quantity = quantity,
                unit = unit
            )
            dao.insertItem(newItem)
        }
    }

    suspend fun updateItem(item: InventoryItemEntity) {
        dao.updateItem(item)
    }

    suspend fun clearAll() {
        dao.clearInventory()
    }
}
