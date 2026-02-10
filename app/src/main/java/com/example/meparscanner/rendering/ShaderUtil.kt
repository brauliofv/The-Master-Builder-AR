package com.example.meparscanner.rendering

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Utility class for loading and compiling OpenGL shaders.
 */
object ShaderUtil {
    private const val TAG = "ShaderUtil"

    /**
     * Load a raw shader file from resources and compile it.
     */
    fun loadGLShader(tag: String?, context: Context, type: Int, resId: Int): Int {
        val code = readRawTextFile(context, resId)
        var shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, code)
        GLES20.glCompileShader(shader)

        // Get the compilation status.
        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader))
            GLES20.glDeleteShader(shader)
            shader = 0
        }
        if (shader == 0) {
            throw RuntimeException("Error creating shader.")
        }
        return shader
    }
    
    /**
     * Load shader from string code.
     */
    fun loadGLShader(tag: String?, type: Int, code: String): Int {
        var shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, code)
        GLES20.glCompileShader(shader)

        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

        if (compileStatus[0] == 0) {
            Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader))
            GLES20.glDeleteShader(shader)
            shader = 0
        }
        if (shader == 0) {
            throw RuntimeException("Error creating shader.")
        }
        return shader
    }

    private fun readRawTextFile(context: Context, resId: Int): String {
        val inputStream = context.resources.openRawResource(resId)
        val inputStreamReader = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        var nextLine: String?
        val body = StringBuilder()
        try {
            while (bufferedReader.readLine().also { nextLine = it } != null) {
                body.append(nextLine)
                body.append('\n')
            }
        } catch (e: IOException) {
            return ""
        }
        return body.toString()
    }

    fun checkGLError(tag: String, label: String) {
        var error: Int
        while (GLES20.glGetError().also { error = it } != GLES20.GL_NO_ERROR) {
            Log.e(TAG, "$tag: glError $label: $error")
            throw RuntimeException("$tag: glError $label: $error")
        }
    }
}
