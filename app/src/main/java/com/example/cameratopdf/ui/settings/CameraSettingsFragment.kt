package com.example.cameratopdf.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cameratopdf.databinding.FragmentCameraSettingsBinding

class CameraSettingsFragment : Fragment() {
    private lateinit var binding: FragmentCameraSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
}