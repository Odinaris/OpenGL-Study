package com.odinaris.opengldemo.utils

import com.odinaris.opengldemo.bean.vec2
import com.odinaris.opengldemo.bean.vec3
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

fun getBuffer(array: FloatArray) : FloatBuffer {
    val buffer = ByteBuffer.allocateDirect(array.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
    buffer.put(array)
    buffer.position(0)
    return buffer
}

fun getVec2Buffer(array: Array<vec2>) : FloatBuffer {
    val buffer = ByteBuffer.allocateDirect(array.size * 4 * 2)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
    for (i in array) {
        buffer.put(i.x)
        buffer.put(i.y)
    }
    buffer.position(0)
    return buffer
}

fun getVec3Buffer(array: Array<vec3>) : FloatBuffer {
    val buffer = ByteBuffer.allocateDirect(array.size * 4 * 3)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
    for (i in array) {
        buffer.put(i.x)
        buffer.put(i.y)
        buffer.put(i.z)
    }
    buffer.position(0)
    return buffer
}

fun getBuffer(array: IntArray) : IntBuffer {
    val buffer = ByteBuffer.allocateDirect(array.size * 4)
            .order(ByteOrder.nativeOrder())
            .asIntBuffer()
    buffer.put(array)
    buffer.position(0)
    return buffer
}