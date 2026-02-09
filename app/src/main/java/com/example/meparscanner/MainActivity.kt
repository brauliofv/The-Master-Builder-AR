package com.example.meparscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.meparscanner.ui.theme.MEPARScannerTheme
import com.example.meparscanner.ui.MepApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MEPARScannerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val db = androidx.room.Room.databaseBuilder(
                        context,
                        com.example.meparscanner.data.AppDatabase::class.java, "mep-database"
                    ).build()
                    val inventoryRepo = com.example.meparscanner.data.repository.InventoryRepository(db.inventoryDao())
                    val standardHeightRepo = com.example.meparscanner.data.repository.StandardHeightRepository(db.standardHeightDao())
                    val gson = com.google.gson.Gson()
                    val projectExporter = com.example.meparscanner.domain.export.ProjectExporter(inventoryRepo, standardHeightRepo, gson)

                    MepApp(projectExporter, inventoryRepo)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MEPARScannerTheme {
        Greeting("Android")
    }
}
