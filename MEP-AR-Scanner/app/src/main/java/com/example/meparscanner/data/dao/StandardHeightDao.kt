package com.example.meparscanner.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.meparscanner.data.model.StandardHeightEntity

@Dao
interface StandardHeightDao {
    @Query("SELECT * FROM standard_heights WHERE category = :category")
    suspend fun getHeightsByCategory(category: String): List<StandardHeightEntity>

    @Query("SELECT * FROM standard_heights")
    suspend fun getAllHeights(): List<StandardHeightEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(heights: List<StandardHeightEntity>)
}
