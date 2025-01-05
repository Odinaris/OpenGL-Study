package com.odinaris.opengldemo.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.odinaris.opengldemo.R

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnPathDot = findViewById<android.widget.Button>(R.id.btn_path_dot)
        btnPathDot.setOnClickListener { startActivity(Intent(this, PathDotActivity::class.java)) }

        val btnPathLine = findViewById<android.widget.Button>(R.id.btn_path_line)
        btnPathLine.setOnClickListener { startActivity(Intent(this, PathLineActivity::class.java)) }
    }
}