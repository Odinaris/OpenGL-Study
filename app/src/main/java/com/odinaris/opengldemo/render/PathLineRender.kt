package com.odinaris.opengldemo.render

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.odinaris.opengldemo.R
import com.odinaris.opengldemo.bean.vec2
import com.odinaris.opengldemo.bean.vec3
import com.odinaris.opengldemo.bean.vec4
import com.odinaris.opengldemo.utils.createProgram
import com.odinaris.opengldemo.utils.getVec2Buffer
import com.odinaris.opengldemo.utils.getVec3Buffer
import com.odinaris.opengldemo.utils.logD
import java.lang.Math.pow
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class PathLineRender(private var context: Context) : GLSurfaceView.Renderer {

    private var mReset = true
    private val mPointVector: ArrayList<vec4> = ArrayList()
    private lateinit var mCurPoint: vec2
    private lateinit var mPrePoint: vec2
    private var mProgram: Int = -1
    private var mWidth: Int = -1
    private var mHeight: Int = -1
    private var mVAO = 0
    private val mVboIds = IntArray(2)
    private val mVertexCoords = Array(TRIANGLE_NUM * 3) { vec3(0f, 0f, 0f) }
    private val mTexCoords = Array(TRIANGLE_NUM * 3) { vec2(0f, 0f) }


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        mProgram = createProgram(context, R.raw.path_line_vert, R.raw.path_line_frag)

        GLES30.glGenBuffers(2, mVboIds, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVboIds[0]);
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER, mVertexCoords.size * 3 * 4,
            getVec3Buffer(mVertexCoords), GLES30.GL_DYNAMIC_DRAW
        );

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVboIds[1]);
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER, mTexCoords.size * 2 * 4,
            getVec2Buffer(mTexCoords), GLES30.GL_DYNAMIC_DRAW
        );

        // Initialize VAO and VBO
        val vaoIds = IntArray(1)
        GLES30.glGenVertexArrays(1, vaoIds, 0)
        mVAO = vaoIds[0]

        GLES30.glBindVertexArray(mVAO)

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVboIds[0])
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 3 * 4, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, GLES30.GL_NONE)

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVboIds[1])
        GLES30.glEnableVertexAttribArray(1)
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 2 * 4, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, GLES30.GL_NONE)

        GLES30.glBindVertexArray(0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        mWidth = width
        mHeight = height
        GLES30.glViewport(0, 0, mWidth, mHeight);
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)

        if (mPointVector.size < 1) return
        GLES30.glUseProgram(mProgram)

        GLES30.glBindVertexArray(mVAO)

        for (i in 0 until mPointVector.size - 1) {
            calculateMesh(
                vec2(mPointVector[i].x, mPointVector[i].y),
                vec2(mPointVector[i].z, mPointVector[i].w)
            )
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVboIds[0])
            GLES30.glBufferSubData(
                GLES30.GL_ARRAY_BUFFER,
                0,
                mVertexCoords.size * 3 * 4,
                getVec3Buffer(mVertexCoords)
            )
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVboIds[1])
            GLES30.glBufferSubData(
                GLES30.GL_ARRAY_BUFFER,
                0,
                mTexCoords.size * 2 * 4,
                getVec2Buffer(mTexCoords)
            )
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, TRIANGLE_NUM * 3)
        }

    }

    private fun calculateMesh(pre: vec2, cur: vec2) {
        // 半径
        val r = EFFECT_RADIUS * mWidth
        val imgSize = vec2(mWidth.toFloat(), mHeight.toFloat())
        var p0 = pre * imgSize
        var p1 = cur * imgSize
        var v0: vec2
        var v1: vec2
        var v2: vec2
        var v3: vec2
        var x0 = p0.x
        var y0 = p0.y
        var x1 = p1.x
        var y1 = p1.y
        if (y0 == y1)  // 1. 两点在同一水平线上
        {
            v0 = vec2(x0, y0 - r) / imgSize
            v1 = vec2(x0, y0 + r) / imgSize
            v2 = vec2(x1, y1 - r) / imgSize
            v3 = vec2(x1, y1 + r) / imgSize
        } else if (x0 == x1) { // 2. 两点在同一垂直线上
            v0 = vec2(x0 - r, y0) / imgSize
            v1 = vec2(x0 + r, y0) / imgSize
            v2 = vec2(x1 - r, y1) / imgSize
            v3 = vec2(x1 + r, y1) / imgSize
        } else {
            val A0: Float = (y1 - y0) * y0 + (x1 - x0) * x0
            val A1: Float = (y0 - y1) * y1 + (x0 - x1) * x1

            // y = a0 * x + c0,  y = a1 * x + c1
            val a0: Float = -(x1 - x0) / (y1 - y0)
            val c0: Float = A0 / (y1 - y0)

            val a1: Float = -(x0 - x1) / (y0 - y1)
            val c1: Float = A1 / (y0 - y1)

            val x0_i = 0f
            val y0_i = a0 * x0_i + c0

            val x1_i = 0f
            val y1_i = a1 * x1_i + c1


            //计算直线与圆的交点
            val v0_v1: vec4 =
                getInsertPointBetweenCircleAndLine(x0, y0, x0_i, y0_i, x0, y0, r)

            v0 = vec2(v0_v1.x, v0_v1.y) / imgSize
            v1 = vec2(v0_v1.z, v0_v1.w) / imgSize

            val v2_v3: vec4 =
                getInsertPointBetweenCircleAndLine(x1, y1, x1_i, y1_i, x1, y1, r)

            v2 = vec2(v2_v3.x, v2_v3.y) / imgSize
            v3 = vec2(v2_v3.z, v2_v3.w) / imgSize
        }

        mTexCoords[0] = v0;
        mTexCoords[1] = v1;
        mTexCoords[2] = v2;
        mTexCoords[3] = v0;
        mTexCoords[4] = v2;
        mTexCoords[5] = v3;
        mTexCoords[6] = v1;
        mTexCoords[7] = v2;
        mTexCoords[8] = v3;

        val index = 9
        val step: Float = (Math.PI / 10).toFloat()

        // 2 个圆，一共 40 个三角形，360 度角平分 20 份
        for (i in 0..19) {
            var x: Float = r * cos(i * step)
            var y: Float = r * sin(i * step)

            var x_: Float = r * cos((i + 1) * step)
            var y_: Float = r * sin((i + 1) * step)

            x += x0
            y += y0
            x_ += x0
            y_ += y0

            mTexCoords[index + 6 * i + 0] = vec2(x, y) / imgSize
            mTexCoords[index + 6 * i + 1] = vec2(x_, y_) / imgSize
            mTexCoords[index + 6 * i + 2] = vec2(x0, y0) / imgSize

            x = r * cos(i * step)
            y = r * sin(i * step)

            x_ = r * cos((i + 1) * step)
            y_ = r * sin((i + 1) * step)

            x += x1
            y += y1
            x_ += x1
            y_ += y1

            mTexCoords[index + 6 * i + 3] = vec2(x, y) / imgSize
            mTexCoords[index + 6 * i + 4] = vec2(x_, y_) / imgSize
            mTexCoords[index + 6 * i + 5] = vec2(x1, y1) / imgSize
        }

        for (i in 0 until TRIANGLE_NUM * 3) {
            mVertexCoords[i] = texCoordToVertexCoord(mTexCoords[i])
        }
    }


    /**
     * 求圆和直线之间的交点
     * 直线方程：y = kx + b
     * 圆的方程：(x - m)² + (x - n)² = r²
     * x1, y1 = 线坐标1, x2, y2 = 线坐标2, m, n = 圆坐标, r = 半径
     */
    private fun getInsertPointBetweenCircleAndLine(
        x1: Float, y1: Float, x2: Float, y2: Float, m: Float, n: Float,
        r: Float
    ): vec4 {
        val kbArr: vec2 = binaryEquationGetKB(x1, y1, x2, y2)
        val k = kbArr.x
        val b = kbArr.y

        val aX = 1 + k * k
        val bX = 2 * k * (b - n) - 2 * m
        val cX = m * m + (b - n) * (b - n) - r * r

        val insertPoints = vec4(0f, 0f, 0f, 0f)
        val xArr: vec2 = quadEquationGetX(aX, bX, cX)
        insertPoints.x = xArr.x
        insertPoints.y = k * xArr.x + b
        insertPoints.z = xArr.y
        insertPoints.w = k * xArr.y + b

        return insertPoints
    }

    /**
     * 一元二次方程求根
     * ax² + bx + c = 0
     */
    private fun quadEquationGetX(a: Float, b: Float, c: Float): vec2 {
        val xArr = vec2(0f, 0f)
        val result: Float = (pow(b.toDouble(), 2.0) - 4 * a * c).toFloat()
        if (result > 0) {
            xArr.x = (-b + sqrt(result)) / (2 * a)
            xArr.y = (-b - sqrt(result)) / (2 * a)
        } else if (result == 0f) {
            xArr.x = (-b / (2 * a))
            xArr.y = xArr.x
        }
        return xArr
    }

    /**
     * 求二元一次方程的系数
     * y1 = k * x1 + b => k = (y1 - b) / x1
     * y2 = k * x2 + b => y2 = ((y1 - b) / x1) * x2 + b
     */
    private fun binaryEquationGetKB(x1: Float, y1: Float, x2: Float, y2: Float): vec2 {
        val k = (y1 - y2) / (x1 - x2)
        val b = (x1 * y2 - x2 * y1) / (x1 - x2)
        return vec2(k, b)
    }

    private fun texCoordToVertexCoord(texCoord: vec2): vec3 {
        return vec3(2 * texCoord.x - 1, 1 - 2 * texCoord.y, 0f)
    }

    fun setLocation(x: Float, y: Float) {
        logD(TAG, "setLocation: x:$x, y:$y mReset:$mReset")
        if (x == -1f) {
            mReset = true
        }
        if (mReset) {
            if (x != -1f) {
                mCurPoint = vec2(x / mWidth, y / mHeight)
                mReset = false
            }
        } else {
            mPrePoint = mCurPoint
            mCurPoint = vec2(x / mWidth, y / mHeight)
            if (mCurPoint == mPrePoint) return
            mPointVector.add(vec4(mPrePoint.x, mPrePoint.y, mCurPoint.x, mCurPoint.y))
        }
    }

    fun reset() {
        mReset = true
    }

    companion object {
        private val TAG = "PathRender"
        private val TRIANGLE_NUM = 43
        private val EFFECT_RADIUS = 0.035f
    }

}