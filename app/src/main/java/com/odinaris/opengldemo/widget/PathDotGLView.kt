package com.odinaris.opengldemo.widget

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.odinaris.opengldemo.render.PathDotRenderer

class PathDotGLView(context: Context) : GLSurfaceView(context) {

    private var mRenderer: PathDotRenderer? = null

    init {
        setEGLContextClientVersion(3)
        mRenderer = PathDotRenderer()
        setRenderer(mRenderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = -((event.x / width.toFloat()) * 2 - 1) // 取反x轴坐标
        val y = -((event.y / height.toFloat()) * 2 - 1)
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                mRenderer!!.addPoint(x, y)
                requestRender()
            }
        }
        return true
    }


}