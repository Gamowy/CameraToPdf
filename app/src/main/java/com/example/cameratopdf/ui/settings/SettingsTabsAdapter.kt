package com.example.cameratopdf.ui.settings

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cameratopdf.ui.settings.camera.CameraSettingsFragment
import com.example.cameratopdf.ui.settings.other.OtherSettingsFragment

class SettingsTabsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CameraSettingsFragment()
            1 -> OtherSettingsFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}