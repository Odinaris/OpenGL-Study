package com.odinaris.opengldemo.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.odinaris.opengldemo.databinding.ActivityTestBinding

class TestActivity : Activity() {

    private lateinit var binding: ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnPathDot.setOnClickListener { startActivity(Intent(this, PathDotActivity::class.java)) }
        binding.btnPathLine.setOnClickListener { startActivity(Intent(this, PathLineActivity::class.java)) }
    }
}