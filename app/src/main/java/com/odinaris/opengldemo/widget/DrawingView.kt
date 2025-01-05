package com.odinaris.opengldemo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.sqrt

class DrawingView : View {
    private var paint: Paint? = null
    private var currentPath: Path? = null
    private var paths: MutableList<Path>? = null
    private var startX = 0f
    private var startY = 0f
    private var drawing = false

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint!!.color = Color.BLUE
        paint!!.style = Paint.Style.STROKE
        paint!!.strokeWidth = 5f

        paths = ArrayList()
        currentPath = Path()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (path in paths!!) {
            canvas.drawPath(path, paint!!)
        }
        if (drawing) {
            canvas.drawPath(currentPath!!, paint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = x
                startY = y
                currentPath!!.moveTo(x, y)
                drawing = true
            }

            MotionEvent.ACTION_MOVE -> {
                currentPath!!.lineTo(x, y)
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                drawing = false
                connectToNearestClosedPath(startX, startY, x, y)
                currentPath!!.close()
                paths!!.add(Path(currentPath))
                currentPath!!.reset()
                invalidate()
            }
        }
        return true
    }

    private fun connectToNearestClosedPath(startX: Float, startY: Float, endX: Float, endY: Float) {
        val nearestStartPath = findNearestPath(startX, startY)
        val nearestEndPath = findNearestPath(endX, endY)

        if (nearestStartPath != null) {
            val nearestPoint = getNearestPointOnPath(nearestStartPath, startX, startY)
            currentPath!!.moveTo(nearestPoint[0], nearestPoint[1])
        } else {
            currentPath!!.moveTo(startX, startY)
        }

        if (nearestEndPath != null && nearestEndPath != nearestStartPath) {
            val nearestPoint = getNearestPointOnPath(nearestEndPath, endX, endY)
            currentPath!!.lineTo(nearestPoint[0], nearestPoint[1])
        } else {
            currentPath!!.lineTo(endX, endY)
        }
    }

    private fun findNearestPath(x: Float, y: Float): Path? {
        var nearestPath: Path? = null
        var minDistance = Float.MAX_VALUE

        for (path in paths!!) {
            val distance = getMinDistanceFromPath(path, x, y)
            if (distance < minDistance) {
                minDistance = distance
                nearestPath = path
            }
        }

        return nearestPath
    }

    private fun getMinDistanceFromPath(path: Path, x: Float, y: Float): Float {
        val measure = PathMeasure(path, false)
        val length = measure.length
        val position = FloatArray(2)
        var minDistance = Float.MAX_VALUE

        var t = 0f
        while (t <= length) {
            measure.getPosTan(t, position, null)
            val dx = position[0] - x
            val dy = position[1] - y
            val distance = sqrt((dx * dx + dy * dy).toDouble()).toFloat()
            if (distance < minDistance) {
                minDistance = distance
            }
            t += 1f
        }

        return minDistance
    }

    private fun getNearestPointOnPath(path: Path, x: Float, y: Float): FloatArray {
        val measure = PathMeasure(path, false)
        val length = measure.length
        val position = FloatArray(2)
        var minDistance = Float.MAX_VALUE
        val nearestPoint = FloatArray(2)

        var t = 0f
        while (t <= length) {
            measure.getPosTan(t, position, null)
            val dx = position[0] - x
            val dy = position[1] - y
            val distance = sqrt((dx * dx + dy * dy).toDouble()).toFloat()
            if (distance < minDistance) {
                minDistance = distance
                nearestPoint[0] = position[0]
                nearestPoint[1] = position[1]
            }
            t += 1f
        }

        return nearestPoint
    }
}