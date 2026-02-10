package com.example.meparscanner.ar

import android.content.Context
import android.util.Log
import com.google.ar.core.Config
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableException

/**
 * Manages the ARCore session lifecycle.
 * Handles session creation, configuration, and cleanup.
 */
class ArSessionManager(private val context: Context) {

    companion object {
        private const val TAG = "ArSessionManager"
    }

    var session: Session? = null
        private set

    var isSessionCreated = false
        private set

    fun createSession(): Boolean {
        if (session != null) {
            return true
        }
        
        return try {
            Log.d(TAG, "Attempting to create ARCore session...")
            session = Session(context).apply {
                val config = config
                config.depthMode = Config.DepthMode.AUTOMATIC
                config.focusMode = Config.FocusMode.AUTO
                config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                
                // --- Augmented Image Configuration ---
                val aid = AugmentedImageDatabase(this)
                try {
                    val inputStream = context.assets.open("qr_marker.png")
                    val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                    // Add image with 15cm (0.15m) width as discussed
                    aid.addImage("origin_marker", bitmap, 0.15f)
                    config.augmentedImageDatabase = aid
                    Log.d(TAG, "Added QR marker to AugmentedImageDatabase")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to load QR marker from assets", e)
                }
                
                configure(config)
            }
            isSessionCreated = true
            Log.d(TAG, "ARCore session created successfully")
            true
        } catch (e: UnavailableException) {
            Log.e(TAG, "ARCore unavailable: ${e.message}", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create AR session: ${e.message}", e)
            false
        }
    }

    fun onResume() {
        try {
            session?.resume()
            Log.d(TAG, "AR session resumed")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to resume AR session", e)
        }
    }

    fun onPause() {
        session?.pause()
        Log.d(TAG, "AR session paused")
    }

    fun onDestroy() {
        session?.close()
        session = null
        isSessionCreated = false
        Log.d(TAG, "AR session destroyed")
    }
}
