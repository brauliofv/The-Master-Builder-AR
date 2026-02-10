package com.example.meparscanner.ar

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.meparscanner.rendering.BackgroundRenderer
import com.example.meparscanner.rendering.PlaneRenderer
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Custom GLSurfaceView that renders the ARCore camera feed.
 * This is the core component for displaying AR content.
 */
class ArCameraView(context: Context) : GLSurfaceView(context), GLSurfaceView.Renderer {

    var onOriginDetected: (() -> Unit)? = null
    private var originFound = false

    companion object {
        private const val TAG = "ArCameraView"
    }

    private var arSession: Session? = null
    private var backgroundRenderer: BackgroundRenderer? = null
    private var planeRenderer: PlaneRenderer? = null
    private var viewportWidth = 0
    private var viewportHeight = 0

    init {
        preserveEGLContextOnPause = true
        setEGLContextClientVersion(2)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        setRenderer(this)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    fun setSession(session: Session?) {
        arSession = session
        // If session is set after surface creation, sync immediately
        if (session != null && backgroundRenderer != null) {
            session.setCameraTextureName(backgroundRenderer!!.cameraTextureId)
            if (viewportWidth > 0 && viewportHeight > 0) {
                session.setDisplayGeometry(0, viewportWidth, viewportHeight)
            }
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)

        backgroundRenderer = BackgroundRenderer().apply {
            createOnGlThread()
        }
        
        planeRenderer = PlaneRenderer().apply {
             createOnGlThread(context)
        }

        // Sync with session if already available
        arSession?.let {
            it.setCameraTextureName(backgroundRenderer!!.cameraTextureId)
        }
        Log.d(TAG, "Surface created, texture ID: ${backgroundRenderer?.cameraTextureId}")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        viewportWidth = width
        viewportHeight = height
        GLES20.glViewport(0, 0, width, height)
        
        // Always sync geometry if session exists
        arSession?.setDisplayGeometry(0, width, height)
        Log.d(TAG, "Surface changed: ${width}x${height}")
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        val session = arSession ?: return
        val renderer = backgroundRenderer ?: return
        val planeRenderer = planeRenderer ?: return

        try {
            val frame = session.update()
            
            // Draw Background
            renderer.draw(frame)
            
            // Draw Planes
            val camera = frame.camera
            
            // Get projection matrix.
            val projmtx = FloatArray(16)
            camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f)

            // Get camera matrix and draw.
            val viewmtx = FloatArray(16)
            camera.getViewMatrix(viewmtx, 0)
            
            val allPlanes = session.getAllTrackables(com.google.ar.core.Plane::class.java)
            planeRenderer.drawPlanes(allPlanes, viewmtx, projmtx)

            // --- Augmented Image Tracking ---
            if (!originFound) {
                val updatedImages = frame.getUpdatedTrackables(AugmentedImage::class.java)
                for (image in updatedImages) {
                    if (image.name == "origin_marker" && image.trackingState == TrackingState.TRACKING) {
                        Log.d(TAG, "Origin marker detected!")
                        originFound = true
                        onOriginDetected?.invoke()
                        break
                    }
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error during frame update", e)
        }
    }

    override fun onPause() {
        super.onPause()
        arSession?.pause()
    }

    override fun onResume() {
        super.onResume()
        try {
            arSession?.resume()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to resume session", e)
        }
    }
}
