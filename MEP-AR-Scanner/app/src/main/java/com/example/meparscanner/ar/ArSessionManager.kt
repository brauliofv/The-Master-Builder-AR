package com.example.meparscanner.ar

import android.content.Context
import android.util.Log
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableException

class ArSessionManager(private val context: Context) {

    var session: Session? = null
        private set

    fun onResume() {
        if (session == null) {
            try {
                session = Session(context).apply {
                    val config = config
                    config.depthMode = Config.DepthMode.AUTOMATIC
                    config.focusMode = Config.FocusMode.AUTO
                    config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                    // Enable Plane Finding
                    config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                    configure(config)
                }
            } catch (e: UnavailableException) {
                Log.e("ArSessionManager", "ARCore unavailable", e)
            }
        }

        try {
            session?.resume()
        } catch (e: Exception) {
            Log.e("ArSessionManager", "Failed to resume AR session", e)
        }
    }

    fun onPause() {
        session?.pause()
    }

    fun onDestroy() {
        session?.close()
        session = null
    }
}
