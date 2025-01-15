package com.odinaris.opengldemo.widget

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import com.odinaris.opengldemo.render.PathLineRender

class PathLineGLView : GLSurfaceView {

    private var mRenderer: PathLineRender

    constructor(context: Context) : super(context) {
        setEGLContextClientVersion(3)
        mRenderer = PathLineRender(context)
        setEGLConfigChooser(8, 8, 8, 8, 16, 8)
        holder.setFormat(PixelFormat.TRANSLUCENT)
        setZOrderOnTop(true)
        setRenderer(mRenderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var x = event.x
        var y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mRenderer.setLocation(x, y)
                mRenderer.setLocation(x + 1f, y + 1f)
                requestRender()
            }

            MotionEvent.ACTION_MOVE -> {
                mRenderer.setLocation(x, y)
                requestRender()
            }

            MotionEvent.ACTION_UP -> {
                mRenderer.reset()
                requestRender()
            }
        }
        return true
    }

    companion object {
        private const val TAG = "MyGLSurfaceView"
    }

}
