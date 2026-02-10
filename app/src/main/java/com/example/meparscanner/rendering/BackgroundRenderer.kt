package com.example.meparscanner.rendering

import android.opengl.GLES11Ext
import android.opengl.GLES20
import com.google.ar.core.Coordinates2d
import com.google.ar.core.Frame
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Renders the camera background using OpenGL.
 * This is the foundation for displaying the AR camera feed.
 */
class BackgroundRenderer {

    companion object {
        private const val COORDS_PER_VERTEX = 2
        private const val TEXCOORDS_PER_VERTEX = 2
        private const val FLOAT_SIZE = 4

        private val QUAD_COORDS = floatArrayOf(
            -1.0f, -1.0f,
            -1.0f, +1.0f,
            +1.0f, -1.0f,
            +1.0f, +1.0f
        )

        private const val VERTEX_SHADER = """
            attribute vec4 a_Position;
            attribute vec2 a_TexCoord;
            varying vec2 v_TexCoord;
            void main() {
                gl_Position = a_Position;
                v_TexCoord = a_TexCoord;
            }
        """

        private const val FRAGMENT_SHADER = """
            #extension GL_OES_EGL_image_external : require
            precision mediump float;
            varying vec2 v_TexCoord;
            uniform samplerExternalOES sTexture;
            void main() {
                gl_FragColor = texture2D(sTexture, v_TexCoord);
            }
        """
    }

    private var quadCoords: FloatBuffer
    private var quadTexCoords: FloatBuffer

    private var quadProgram = 0
    private var quadPositionParam = 0
    private var quadTexCoordParam = 0
    var cameraTextureId = -1
        private set

    init {
        quadCoords = ByteBuffer.allocateDirect(QUAD_COORDS.size * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(QUAD_COORDS)
                position(0)
            }

        quadTexCoords = ByteBuffer.allocateDirect(QUAD_COORDS.size * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
    }

    fun createOnGlThread() {
        // Generate texture for camera
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        cameraTextureId = textures[0]

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, cameraTextureId)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        // Create shader program
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)

        quadProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(quadProgram, vertexShader)
        GLES20.glAttachShader(quadProgram, fragmentShader)
        GLES20.glLinkProgram(quadProgram)
        GLES20.glUseProgram(quadProgram)

        quadPositionParam = GLES20.glGetAttribLocation(quadProgram, "a_Position")
        quadTexCoordParam = GLES20.glGetAttribLocation(quadProgram, "a_TexCoord")
    }

    fun draw(frame: Frame) {
        if (frame.hasDisplayGeometryChanged()) {
            frame.transformCoordinates2d(
                Coordinates2d.OPENGL_NORMALIZED_DEVICE_COORDINATES,
                quadCoords,
                Coordinates2d.TEXTURE_NORMALIZED,
                quadTexCoords
            )
        }

        if (frame.timestamp == 0L) {
            return
        }

        // Disable depth test for background
        GLES20.glDisable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthMask(false)

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, cameraTextureId)
        GLES20.glUseProgram(quadProgram)

        // Set vertices
        GLES20.glVertexAttribPointer(
            quadPositionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, quadCoords
        )
        GLES20.glVertexAttribPointer(
            quadTexCoordParam, TEXCOORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, quadTexCoords
        )

        GLES20.glEnableVertexAttribArray(quadPositionParam)
        GLES20.glEnableVertexAttribArray(quadTexCoordParam)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(quadPositionParam)
        GLES20.glDisableVertexAttribArray(quadTexCoordParam)

        // Restore depth test
        GLES20.glDepthMask(true)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }
}
