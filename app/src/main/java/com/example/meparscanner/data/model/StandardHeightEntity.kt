package com.example.meparscanner.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "standard_heights")
data class StandardHeightEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String, // Kitchen, Bathroom, General
    val elementType: String, // Countertop outlets, Fridge, etc.
    val minHeightMeters: Float,
    val maxHeightMeters: Float
)
