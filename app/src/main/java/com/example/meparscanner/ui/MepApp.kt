package com.example.meparscanner.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.meparscanner.ui.screens.ArScreen
import com.example.meparscanner.ui.screens.HomeScreen
import com.example.meparscanner.ui.screens.InventoryScreen

import com.example.meparscanner.domain.export.ProjectExporter
import com.example.meparscanner.ui.screens.FeedbackScreen

@Composable
fun MepApp(projectExporter: ProjectExporter) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onNavigateToAr = { navController.navigate("ar") },
                onNavigateToInventory = { navController.navigate("inventory") },
                onNavigateToFeedback = { navController.navigate("feedback") }
            )
        }
        composable("ar") {
            ArScreen()
        }
        composable("inventory") {
            InventoryScreen()
        }
        composable("feedback") {
            FeedbackScreen(projectExporter)
        }
    }
}
