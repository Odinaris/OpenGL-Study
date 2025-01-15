package com.odinaris.opengldemo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.odinaris.opengldemo.databinding.FragmentStencilTestBinding
import com.odinaris.opengldemo.widget.StencilTestGLView


class StencilTestFragment : Fragment() {

    private lateinit var binding: FragmentStencilTestBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStencilTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.addView(StencilTestGLView(requireContext()))
    }


}