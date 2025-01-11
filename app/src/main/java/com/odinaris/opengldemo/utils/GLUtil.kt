package com.odinaris.opengldemo.utils

import android.content.Context
import android.graphics.PointF
import android.opengl.GLES20
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

const val TAG = "GLUtil"

fun createProgram(context: Context, vertexAssetPath: String, fragmentAssetPath: String): Int {
    val vertexSource = loadShaderFromAsset(context, vertexAssetPath)
    val fragmentSource = loadShaderFromAsset(context, fragmentAssetPath)
    return createProgram(vertexSource, fragmentSource)
}

fun createProgram(vertexSourceCode: String, fragmentSourceCode: String): Int {
    val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSourceCode)
    if (vertexShader == 0) {
        return 0
    }
    val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSourceCode)
    if (fragmentShader == 0) {
        return 0
    }
    var program = GLES20.glCreateProgram()
    if (program != 0) {
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] != GLES20.GL_TRUE) {
            logE(TAG, "Could not link program: ")
            logE(TAG, GLES20.glGetProgramInfoLog(program))
            GLES20.glDeleteProgram(program)
            program = 0
        }
    }
    return program
}

fun createProgram(context: Context, vertexShaderResId: Int, fragmentShaderResId: Int): Int {
    val vertexShaderSource = context.resources.openRawResource(vertexShaderResId).bufferedReader().use { it.readText() }
    val fragmentShaderSource = context.resources.openRawResource(fragmentShaderResId).bufferedReader().use { it.readText() }
    return createProgram(vertexShaderSource, fragmentShaderSource)
}

private fun loadShaderFromAsset(context: Context, fileName: String): String {
    val stringBuilder = StringBuilder()
    try {
        val inputStream: InputStream = context.assets.open(fileName)
        val inputStreamReader = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            stringBuilder.append(line).append("\n")
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return stringBuilder.toString()
}

private fun loadShader(shaderType: Int, source: String): Int {
    var shader = GLES20.glCreateShader(shaderType)
    if (shader != 0) {
        GLES20.glShaderSource(shader, source)
        GLES20.glCompileShader(shader)
        val compiled = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            logE(TAG, "Could not compile shader $shaderType:")
            logE(TAG, GLES20.glGetShaderInfoLog(shader))
            GLES20.glDeleteShader(shader)
            shader = 0
        }
    }
    return shader
}

/**
 * 将屏幕坐标转换为GL坐标
 */
fun getGLPoint(width: Int, height: Int, screenPoint : PointF): PointF {
    return PointF((width - screenPoint.x) / width / 2f, (screenPoint.y - height) / height / 2f)
}



