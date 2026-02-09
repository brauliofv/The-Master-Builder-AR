package com.example.meparscanner.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemName: String,
    val itemType: String, // PIPE, WIRE, FIXTURE
    val quantity: Float, // Length for pipes/wires, count for fixtures
    val unit: String, // "m", "pcs"
    val description: String = ""
)
