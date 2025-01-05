package com.odinaris.opengldemo.activity

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.odinaris.opengldemo.render.DrawingRenderer

class PathDotActivity : AppCompatActivity() {

    private lateinit var mGLSurfaceView: GLSurfaceView
    private var mRenderer: DrawingRenderer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGLSurfaceView = GLSurfaceView(this)
        mGLSurfaceView.setEGLContextClientVersion(3)
        mRenderer = DrawingRenderer()
        mGLSurfaceView.setRenderer(mRenderer)
        setContentView(mGLSurfaceView)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = -((event.x / mGLSurfaceView.width.toFloat()) * 2 - 1) // 取反x轴坐标
        val y = -((event.y / mGLSurfaceView.height.toFloat()) * 2 - 1)
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                mRenderer!!.addPoint(x, y)
                mGLSurfaceView.requestRender()
            }
        }
        return true
    }
}