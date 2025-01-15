package com.odinaris.opengldemo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.odinaris.opengldemo.databinding.ActivityMainBinding

class MainActivity : FragmentActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mTitles = arrayOf("点绘制路径", "线绘制路径", "模板测试")
    private val mFragments = arrayOf(PathDotFragment(), PathLineFragment(), StencilTestFragment())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewPager.adapter = MyFragmentPagerAdapter()
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = mTitles[position]
        }.attach()
        binding.viewPager.isUserInputEnabled = false
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(mTitles.size - 2))
    }

    inner class MyFragmentPagerAdapter : FragmentStateAdapter(this) {

        override fun getItemCount(): Int {
            return mTitles.size
        }

        override fun createFragment(position: Int): Fragment {
            return mFragments[position]
        }
    }


}