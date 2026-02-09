package com.example.meparscanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.meparscanner.domain.export.ProjectExporter
import kotlinx.coroutines.launch

@Composable
fun FeedbackScreen(projectExporter: ProjectExporter) {
    var exportResult by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Architectural Audit",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                scope.launch {
                    val json = projectExporter.exportProjectToJson()
                    exportResult = "JSON Export Generated:\n\n$json"
                }
            }) {
                Text("Generate JSON")
            }

            Button(onClick = {
                scope.launch {
                    // Mock PDF Generation
                    exportResult = "PDF Report Generated.\n(Sent to /Documents/Reports/Project_Audit.pdf)"
                }
            }) {
                Text("Generate PDF")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = exportResult.ifEmpty { "Ready to export project data..." },
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
