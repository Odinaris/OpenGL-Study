package com.odinaris.opengldemo.render

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.odinaris.opengldemo.utils.createProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class DrawingRenderer : GLSurfaceView.Renderer {
    private val vertexShaderCode = "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "  gl_PointSize = 10.0;" +
            "}"

    private val fragmentShaderCode = "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}"

    private var mPoints = FloatArray(1024) // 预分配足够大的数组
    private var mHaloPoints = FloatArray(1024)
    private var mPointCount = 0
    private var mHaloPointCount = 0
    private val mHaloColor = floatArrayOf(1.0f, 1.0f, 1.0f, 0.5f) // 50% transparent white
    private val mLineColor = floatArrayOf(1.0f, 1.0f, 1.0f, 0.5f) // 50% transparent white
    private var mProgram = 0
    private var mPositionHandle = 0
    private var mColorHandle = 0
    private var mMVPMatrixHandle = 0
    private val mMVPMatrix = FloatArray(16)
    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)

    private var mVBO = 0
    private var mVAO = 0

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        mProgram = createProgram(vertexShaderCode, fragmentShaderCode)
        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition")
        mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor")
        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)

        // Initialize VAO and VBO
        val vaoIds = IntArray(1)
        GLES30.glGenVertexArrays(1, vaoIds, 0)
        mVAO = vaoIds[0]
        GLES30.glBindVertexArray(mVAO)

        val vboIds = IntArray(1)
        GLES30.glGenBuffers(1, vboIds, 0)
        mVBO = vboIds[0]
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBO)

        GLES30.glVertexAttribPointer(mPositionHandle, 2, GLES30.GL_FLOAT, false, 0, 0)
        GLES30.glEnableVertexAttribArray(mPositionHandle)

        GLES30.glBindVertexArray(0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glUseProgram(mProgram)

        updateVBO()

        GLES30.glBindVertexArray(mVAO)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBO)

        GLES30.glUniform4fv(mColorHandle, 1, mLineColor, 0)
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0)
        GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, mPointCount / 2)

        GLES30.glUniform4fv(mColorHandle, 1, mHaloColor, 0)
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, mHaloPointCount / 2)

        GLES30.glBindVertexArray(0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)
    }

    private fun updateVBO() {
        if (mPointCount > 0 || mHaloPointCount > 0) {
            val buffer = ByteBuffer.allocateDirect((mPointCount + mHaloPointCount) * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
            for (i in 0 until mPointCount) {
                buffer.put(mPoints[i])
            }
            for (i in 0 until mHaloPointCount) {
                buffer.put(mHaloPoints[i])
            }
            buffer.position(0)
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBO)
            GLES30.glBufferData(
                GLES30.GL_ARRAY_BUFFER,
                (mPointCount + mHaloPointCount) * 4,
                buffer,
                GLES30.GL_DYNAMIC_DRAW
            )
        }
    }

    fun addPoint(x: Float, y: Float) {
        if (mPointCount >= mPoints.size || mHaloPointCount >= mHaloPoints.size) {
            // 扩展数组容量
            val newPoints = FloatArray(mPoints.size * 2)
            System.arraycopy(mPoints, 0, newPoints, 0, mPointCount)
            mPoints = newPoints

            val newHaloPoints = FloatArray(mHaloPoints.size * 2)
            System.arraycopy(mHaloPoints, 0, newHaloPoints, 0, mHaloPointCount)
            mHaloPoints = newHaloPoints
        }

        mPoints[mPointCount++] = x
        mPoints[mPointCount++] = y
        mHaloPoints[mHaloPointCount++] = x
        mHaloPoints[mHaloPointCount++] = y
    }
}
