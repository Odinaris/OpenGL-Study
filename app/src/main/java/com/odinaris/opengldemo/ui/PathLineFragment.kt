package com.odinaris.opengldemo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.odinaris.opengldemo.databinding.FragmentPathDotBinding
import com.odinaris.opengldemo.widget.PathLineGLView


class PathLineFragment : Fragment() {

    private lateinit var binding: FragmentPathDotBinding
    private lateinit var mGLView: PathLineGLView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPathDotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mGLView = PathLineGLView(requireContext())
        binding.root.addView(mGLView)
    }

    companion object {
        const val TAG = "PathLineFragment"
    }

}