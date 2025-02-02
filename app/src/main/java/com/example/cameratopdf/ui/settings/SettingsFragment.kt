package com.example.cameratopdf.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.example.cameratopdf.R

import com.example.cameratopdf.databinding.FragmentSettingsBinding
import com.google.android.material.tabs.TabLayoutMediator

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var settingsTabsAdapter: SettingsTabsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)?.supportActionBar?.title = getString(R.string.app_settings)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsTabsAdapter = SettingsTabsAdapter(this)
        binding.viewPager.adapter = settingsTabsAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.camera_settings)
                    tab.icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_camera)
                }
                1 -> {
                    tab.text = getString(R.string.other_settings)
                    tab.icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_settings)
                }
            }
        }.attach()
    }
}