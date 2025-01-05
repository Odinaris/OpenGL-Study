package com.odinaris.opengldemo.widget

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import com.odinaris.opengldemo.render.PathRender

class MyGLSurfaceView : GLSurfaceView {

    private var mRenderer: PathRender

    constructor(context: Context) : super(context) {
        setEGLContextClientVersion(3)
        mRenderer = PathRender(context)
        setEGLConfigChooser(8, 8, 8, 8, 16, 8)
        setRenderer(mRenderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x: Float = event.x
        val y: Float = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // do something
            }

            MotionEvent.ACTION_MOVE -> {
                mRenderer.setLocation(x, y)
                requestRender()
            }

            MotionEvent.ACTION_UP -> {
                mRenderer.setLocation(x, y)
                requestRender()
            }
        }
        return true
    }

    companion object {
        private const val TAG = "MyGLSurfaceView"
    }

}
