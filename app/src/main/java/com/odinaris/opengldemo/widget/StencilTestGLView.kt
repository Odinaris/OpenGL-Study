package com.odinaris.opengldemo.widget

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.odinaris.opengldemo.R
import com.odinaris.opengldemo.utils.createProgram
import com.odinaris.opengldemo.utils.getBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class StencilTestGLView(context: Context) : GLSurfaceView(context), GLSurfaceView.Renderer {

    private var mProgram: Int = -1
    private var mVaoIds = IntArray(2)
    private val mVboIds = IntArray(2)
    private var mVertexCoords1 = floatArrayOf(
        -0.5f, 0.5f, 0.0f,
        0.5f, 0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f
    )
    private var mVertexCoords2 = floatArrayOf(
        -0.5f, 0.5f, 0.0f,
        0.5f, 0.5f, 0.0f,
        0.5f, -0.5f, 0.0f
    )

    init {
        setEGLContextClientVersion(3)
        setEGLConfigChooser(8, 8, 8, 8, 16, 8)
        setRenderer(this)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 绘制双VBO管理的三角形，踩坑参考：https://juejin.cn/post/7149775557398364167
        GLES30.glClearColor(1.0f, 0.5f, 0.3f, 1.0f)
        mProgram = createProgram(context, R.raw.stencil_vert, R.raw.stencil_frag)

        GLES30.glGenVertexArrays(2, mVaoIds, 0)
        GLES30.glGenBuffers(2, mVboIds, 0);

        // 配置第一个三角形的顶点属性 (位置0)
        GLES30.glBindVertexArray(mVaoIds[0])
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVboIds[0])
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER, mVertexCoords1.size * 4,
            getBuffer(mVertexCoords1), GLES30.GL_STATIC_DRAW
        )
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 3 * 4, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, GLES30.GL_NONE)
        GLES30.glBindVertexArray(0)

        // 配置第二个三角形的顶点属性 (位置1)
        GLES30.glBindVertexArray(mVaoIds[1])
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVboIds[1])
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER, mVertexCoords2.size * 4,
            getBuffer(mVertexCoords2), GLES30.GL_STATIC_DRAW
        )
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 3 * 4, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, GLES30.GL_NONE)
        GLES30.glBindVertexArray(0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        GLES30.glUseProgram(mProgram)

        GLES30.glEnable(GLES30.GL_DEPTH_TEST)

        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)

        GLES30.glBindVertexArray(mVaoIds[0])
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVboIds[0])
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, mVertexCoords1.size / 3)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        GLES30.glBindVertexArray(0)

        GLES30.glBindVertexArray(mVaoIds[1])
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVboIds[1])
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, mVertexCoords2.size / 3)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        GLES30.glBindVertexArray(0)
    }

}