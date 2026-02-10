package com.example.meparscanner.rendering

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.google.ar.core.Plane
import com.google.ar.core.Pose
import com.google.ar.core.TrackingState
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import java.util.ArrayList

/**
 * Renders the detected AR planes.
 */
class PlaneRenderer {
    companion object {
        private const val TAG = "PlaneRenderer"

        // Shader code
        private const val VERTEX_SHADER = """
            uniform mat4 u_ModelViewProjection;
            uniform mat4 u_ModelView;
            attribute vec3 a_Position;
            varying vec3 v_ViewPosition;
            void main() {
                v_ViewPosition = (u_ModelView * vec4(a_Position, 1.0)).xyz;
                gl_Position = u_ModelViewProjection * vec4(a_Position, 1.0);
            }
        """

        private const val FRAGMENT_SHADER = """
            precision mediump float;
            varying vec3 v_ViewPosition;
            uniform vec4 u_GridColor;
            
            void main() {
                // Simple grid logic based on position can be added here
                // For MVP, just a semi-transparent color
                float alpha = 1.0 - smoothstep(0.0, 5.0, length(v_ViewPosition)); 
                gl_FragColor = u_GridColor * alpha; 
            }
        """
        
        // Dot shader for vertices (optional, keeping it simple for now)
        
        private const val BYTES_PER_FLOAT = 4
        private const val BYTES_PER_SHORT = 2
        private const val COORDS_PER_VERTEX = 3
    }

    private var planeProgram = 0
    private var modelViewProjectionUniform = 0
    private var modelViewUniform = 0
    private var gridColorUniform = 0
    private var positionAttribute = 0

    private var vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(1000 * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder()).asFloatBuffer()
    private var serverIndexBuffer: ShortBuffer = ByteBuffer.allocateDirect(1000 * BYTES_PER_SHORT)
        .order(ByteOrder.nativeOrder()).asShortBuffer()
        
    private val modelMatrix = FloatArray(16)
    private val modelViewMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)

    fun createOnGlThread(context: Context) {
        val vertexShader = ShaderUtil.loadGLShader(TAG, GLES20.GL_VERTEX_SHADER, VERTEX_SHADER)
        val fragmentShader = ShaderUtil.loadGLShader(TAG, GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)

        planeProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(planeProgram, vertexShader)
        GLES20.glAttachShader(planeProgram, fragmentShader)
        GLES20.glLinkProgram(planeProgram)
        GLES20.glUseProgram(planeProgram)

        modelViewProjectionUniform = GLES20.glGetUniformLocation(planeProgram, "u_ModelViewProjection")
        modelViewUniform = GLES20.glGetUniformLocation(planeProgram, "u_ModelView")
        gridColorUniform = GLES20.glGetUniformLocation(planeProgram, "u_GridColor")
        positionAttribute = GLES20.glGetAttribLocation(planeProgram, "a_Position")
    }

    /**
     * Draws the collection of tracked planes.
     * @param allPlanes List of planes to render.
     * @param cameraPose The pose of the camera.
     * @param cameraProjection The projection matrix of the camera.
     */
    fun drawPlanes(allPlanes: Collection<Plane>, cameraDisplayMatrix: FloatArray, cameraProjection: FloatArray) {
        if (allPlanes.isEmpty()) return

        GLES20.glUseProgram(planeProgram)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glDepthMask(false) // Don't write to depth buffer for transparent planes

        for (plane in allPlanes) {
            if (plane.trackingState != TrackingState.TRACKING || plane.subsumedBy != null) {
                continue
            }
            
            updatePlaneMesh(plane)
            
            // Compute ModelViewProjection matrix
            plane.centerPose.toMatrix(modelMatrix, 0)
            
            // Calculate ModelView
            Matrix.multiplyMM(modelViewMatrix, 0, cameraDisplayMatrix, 0, modelMatrix, 0)
            // Calculate ModelViewProjection
            Matrix.multiplyMM(modelViewProjectionMatrix, 0, cameraProjection, 0, modelViewMatrix, 0)

            // Set uniforms
            GLES20.glUniformMatrix4fv(modelViewProjectionUniform, 1, false, modelViewProjectionMatrix, 0)
            GLES20.glUniformMatrix4fv(modelViewUniform, 1, false, modelViewMatrix, 0)
            
            // Set Color based on plane type (Horizontal = Greenish, Vertical = Blueish)
            // But specific color logic is simplified here to White
             GLES20.glUniform4f(gridColorUniform, 1.0f, 1.0f, 1.0f, 0.3f)

            // Draw
            GLES20.glVertexAttribPointer(positionAttribute, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertexBuffer)
            GLES20.glEnableVertexAttribArray(positionAttribute)
            
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, serverIndexBuffer.limit(), GLES20.GL_UNSIGNED_SHORT, serverIndexBuffer)
            
            GLES20.glDisableVertexAttribArray(positionAttribute)
        }

        GLES20.glDepthMask(true)
        GLES20.glDisable(GLES20.GL_BLEND)
    }

    private fun updatePlaneMesh(plane: Plane) {
        // Simplified generic mesh update:
        // ARCore planes are polygons. We need to tessellate them into triangles.
        // For MVP, we can treat the plane as a simple quad if strictly rectangular (not true for ARCore)
        // Or properly use the polygon data.
        
        val polygon = plane.polygon
        if (polygon == null || polygon.limit() < 6) return 

        // ARCore returns X, Z points relative to plane center.
        // We need X, Y, Z. Since it's a plane, Y is 0 in plane-space.
        
        val vertexCount = polygon.limit() / 2
        
        // Ensure buffers are large enough
        if (vertexBuffer.capacity() < vertexCount * COORDS_PER_VERTEX * BYTES_PER_FLOAT) {
             val size = vertexCount * COORDS_PER_VERTEX * BYTES_PER_FLOAT * 2
             vertexBuffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder()).asFloatBuffer()
        }
        if (serverIndexBuffer.capacity() < (vertexCount - 2) * 3 * BYTES_PER_SHORT) {
             val size = (vertexCount - 2) * 3 * BYTES_PER_SHORT * 2
             serverIndexBuffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder()).asShortBuffer()
        }
        
        vertexBuffer.clear()
        serverIndexBuffer.clear()

        // Fill vertices
        polygon.rewind()
        while(polygon.hasRemaining()) {
            val x = polygon.get()
            val z = polygon.get()
            vertexBuffer.put(x)
            vertexBuffer.put(0.0f) // Y is 0
            vertexBuffer.put(z)
        }
        vertexBuffer.flip()

        // Tesselate polygon (Fan triangulation standard for convex polygons)
        // ARCore polygons are usually simple & convex enough or concave but behave well with triangle fan center?
        // Actually best to do simple triangle fan from 0.
        // Index buffer: 0, 1, 2; 0, 2, 3; ...
        
        val numTriangles = vertexCount - 2
        for (i in 0 until numTriangles) {
            serverIndexBuffer.put(0.toShort())
            serverIndexBuffer.put((i + 1).toShort())
            serverIndexBuffer.put((i + 2).toShort())
        }
        serverIndexBuffer.flip()
    }
}
