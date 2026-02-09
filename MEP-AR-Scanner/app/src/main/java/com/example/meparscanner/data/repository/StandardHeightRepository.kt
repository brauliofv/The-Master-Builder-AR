package com.example.meparscanner.data.repository

import com.example.meparscanner.data.dao.StandardHeightDao
import com.example.meparscanner.data.model.StandardHeightEntity

class StandardHeightRepository(private val dao: StandardHeightDao) {
    suspend fun getHeightsForCategory(category: String): List<StandardHeightEntity> {
        return dao.getHeightsByCategory(category)
    }

    suspend fun getAllHeights(): List<StandardHeightEntity> {
        return dao.getAllHeights()
    }
}
