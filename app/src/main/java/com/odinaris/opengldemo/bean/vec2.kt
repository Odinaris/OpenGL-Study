package com.odinaris.opengldemo.bean

data class vec2(var x: Float, var y: Float) {
    fun add(vec: vec2): vec2 {
        return vec2(x + vec.x, y + vec.y)
    }

    fun sub(vec: vec2): vec2 {
        return vec2(x - vec.x, y - vec.y)
    }

    operator fun times(vec: vec2): vec2 {
        return vec2(x * vec.x, y * vec.y)
    }

    operator fun div(vec: vec2): vec2 {
        return vec2(x / vec.x, y / vec.y)
    }

}
