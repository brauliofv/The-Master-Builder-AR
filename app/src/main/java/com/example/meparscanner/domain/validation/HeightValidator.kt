package com.example.meparscanner.domain.validation

import com.example.meparscanner.data.model.StandardHeightEntity
import com.example.meparscanner.data.repository.StandardHeightRepository
import com.google.ar.core.Pose
import kotlin.math.abs

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val message: String, val deviation: Float) : ValidationResult()
}

class HeightValidator(private val repository: StandardHeightRepository) {

    /**
     * @param currentHeightMeters The detected height of the object from the floor.
     * @param category The room category (Kitchen, Bathroom, etc.)
     * @param elementType The specific element being placed (e.g., "Countertop Outlet")
     */
    suspend fun validate(
        currentHeightMeters: Float,
        category: String,
        elementType: String
    ): ValidationResult {
        val standards = repository.getHeightsForCategory(category)
        val standard = standards.find { it.elementType == elementType }
            ?: return ValidationResult.Valid // No standard found, assume valid or handle as needed

        return when {
            currentHeightMeters < standard.minHeightMeters -> {
                ValidationResult.Invalid(
                    "Too low. Standard: ${standard.minHeightMeters}m - ${standard.maxHeightMeters}m",
                    standard.minHeightMeters - currentHeightMeters
                )
            }
            currentHeightMeters > standard.maxHeightMeters -> {
                ValidationResult.Invalid(
                    "Too high. Standard: ${standard.minHeightMeters}m - ${standard.maxHeightMeters}m",
                    currentHeightMeters - standard.maxHeightMeters
                )
            }
            else -> ValidationResult.Valid
        }
    }
}
