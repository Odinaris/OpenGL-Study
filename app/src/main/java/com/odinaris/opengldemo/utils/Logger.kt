package com.odinaris.opengldemo.utils

import android.util.Log

const val TAG_PREFIX : String = "DDY"

fun logD(tag : String, msg : String) {
    Log.d(TAG_PREFIX + tag, msg)
}

fun logE(tag : String, msg : String) {
    Log.e(TAG_PREFIX + tag, msg)
}