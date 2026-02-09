package com.example.meparscanner.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.meparscanner.data.dao.StandardHeightDao
import com.example.meparscanner.data.dao.InventoryDao
import com.example.meparscanner.data.model.StandardHeightEntity
import com.example.meparscanner.data.model.InventoryItemEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [StandardHeightEntity::class, InventoryItemEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun standardHeightDao(): StandardHeightDao
    abstract fun inventoryDao(): InventoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mep_scanner_database"
                )
                .addCallback(AppDatabaseCallback(CoroutineScope(Dispatchers.IO)))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database.standardHeightDao())
                    }
                }
            }

            suspend fun populateDatabase(dao: StandardHeightDao) {
                // Kitchen
                val kitchenHeights = listOf(
                    StandardHeightEntity(category = "Kitchen", elementType = "Countertop Outlet", minHeightMeters = 1.10f, maxHeightMeters = 1.20f),
                    StandardHeightEntity(category = "Kitchen", elementType = "Fridge", minHeightMeters = 0.60f, maxHeightMeters = 0.60f)
                )
                // Bathroom
                val bathroomHeights = listOf(
                    StandardHeightEntity(category = "Bathroom", elementType = "WC Water Inlet", minHeightMeters = 0.20f, maxHeightMeters = 0.20f),
                    StandardHeightEntity(category = "Bathroom", elementType = "Sink", minHeightMeters = 0.55f, maxHeightMeters = 0.60f)
                )
                // General
                val generalHeights = listOf(
                    StandardHeightEntity(category = "General", elementType = "Light Switch", minHeightMeters = 1.10f, maxHeightMeters = 1.20f),
                    StandardHeightEntity(category = "General", elementType = "General Outlet", minHeightMeters = 0.30f, maxHeightMeters = 0.40f)
                )

                dao.insertAll(kitchenHeights + bathroomHeights + generalHeights)
            }
        }
    }
}
