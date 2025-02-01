package com.example.cameratopdf.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cameratopdf.databinding.FragmentOtherSettingsBinding

class OtherSettingsFragment : Fragment() {
    private lateinit var binding: FragmentOtherSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtherSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
}