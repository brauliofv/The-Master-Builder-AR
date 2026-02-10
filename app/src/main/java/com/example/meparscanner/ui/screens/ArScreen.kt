package com.example.meparscanner.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.meparscanner.ar.ArCameraView
import com.example.meparscanner.ar.ArSessionManager
import com.example.meparscanner.data.repository.InventoryRepository
import kotlinx.coroutines.launch

@Composable
fun ArScreen(inventoryRepository: InventoryRepository) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    // Permission state
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    // Request permission on first composition if not granted
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // AR Session Manager
    val arSessionManager = remember { ArSessionManager(context) }
    
    // AR Camera View reference
    var arCameraView by remember { mutableStateOf<ArCameraView?>(null) }

    // UI State
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val categories = listOf("Electrical ⚡", "Water 💧")
    var arSessionReady by remember { mutableStateOf(false) }
    var originDetected by remember { mutableStateOf(false) }

    // Use updated state to ensure the lifecycle observer always has the latest values
    val currentHasPermission by rememberUpdatedState(hasCameraPermission)
    val currentArCameraView by rememberUpdatedState(arCameraView)

    // Lifecycle management - reacts to permission or view changes
    DisposableEffect(lifecycleOwner, hasCameraPermission, arCameraView) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (currentHasPermission) {
                    if (arSessionManager.createSession()) {
                        currentArCameraView?.setSession(arSessionManager.session)
                        arSessionManager.onResume()
                        arSessionReady = true
                    }
                }
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                arSessionManager.onPause()
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        // If we already have permission and the view is ready, and we are resumed, 
        // try to initialize immediately (handles the case where permission is granted while on screen)
        if (hasCameraPermission && arCameraView != null && 
            lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            if (arSessionManager.createSession()) {
                arCameraView?.setSession(arSessionManager.session)
                arSessionManager.onResume()
                arSessionReady = true
            }
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            arSessionManager.onDestroy()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            // AR Camera View
            AndroidView(
                factory = { ctx ->
                    ArCameraView(ctx).also { view ->
                        arCameraView = view
                        view.onOriginDetected = {
                            originDetected = true
                        }
                        if (arSessionManager.isSessionCreated) {
                            view.setSession(arSessionManager.session)
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Category Selector (Top)
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp),
                containerColor = Color.Black.copy(alpha = 0.6f),
                contentColor = Color.White
            ) {
                categories.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // HUD Overlay & Simulation Button
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Status indicator
                if (arSessionReady) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (originDetected) Color.Blue.copy(alpha = 0.8f) else Color.Green.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = if (originDetected) "📍 Punto de Origen Confirmado (0,0,0)" else "✓ AR Lista - Busca el Marcador QR",
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Simulated Placement Button
                Button(
                    onClick = {
                        scope.launch {
                            if (selectedTabIndex == 0) {
                                inventoryRepository.addItem("Outlet Box (Switch)", "ELECTRICAL", 1f, "pcs")
                            } else {
                                inventoryRepository.addItem("PVC Pipe 1/2\"", "WATER", 1.5f, "m")
                            }
                        }
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text("Simulate Placement")
                }
            }
        }
 else {
            // Permission not granted UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "📷 Camera Permission Required",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "This app needs camera access for AR functionality.",
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }
                ) {
                    Text("Grant Permission")
                }
            }
        }
    }
}
