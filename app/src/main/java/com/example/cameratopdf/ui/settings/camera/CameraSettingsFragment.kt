package com.example.cameratopdf.ui.settings.camera

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.cameratopdf.databinding.FragmentCameraSettingsBinding
import kotlinx.coroutines.launch

class CameraSettingsFragment : Fragment() {
    private lateinit var binding: FragmentCameraSettingsBinding
    private val viewModel: CameraSettingsViewModel by viewModels()
    private lateinit var appContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraSettingsBinding.inflate(inflater, container, false)
        appContext = requireContext().applicationContext

        lifecycleScope.launch {
            viewModel.loadSettings(appContext)
            viewModel.photosPerDocument.value?.let {
                binding.photosPerDocumentValue.text = it.toString()
                binding.photosPerDocumentSlider.value = it.toFloat()
            }
            viewModel.timeBetweenPhotos.value?.let {
                binding.secondsBetweenPhotosValue.text = it.toInt().toString()
                binding.timeBetweenPhotosSlider.value = it
            }
            viewModel.makeSoundBeforePhoto.value?.let {
                binding.soundBeforePhotoSwitch.isChecked = it
            }
            viewModel.makeSoundAfterPhoto.value?.let {
                binding.soundAfterPhotoSwitch.isChecked = it
            }
            viewModel.makeSoundAfterAllPhotos.value?.let {
                binding.soundAfterAllPhotosSwitch.isChecked = it
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.photosPerDocumentSlider.addOnChangeListener {_, value, _ ->
            lifecycleScope.launch {
                binding.photosPerDocumentValue.text = value.toInt().toString()
                viewModel.setPhotosPerDocument(appContext, value.toInt())
            }
        }
        binding.timeBetweenPhotosSlider.addOnChangeListener {_, value, _ ->
            lifecycleScope.launch {
                binding.secondsBetweenPhotosValue.text = value.toString()
                viewModel.setTimeBetweenPhotos(appContext, value)
            }
        }
        binding.soundBeforePhotoSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                viewModel.setMakeSoundBeforePhoto(appContext, isChecked)
            }
        }
        binding.soundAfterPhotoSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                viewModel.setMakeSoundAfterPhoto(appContext, isChecked)
            }
        }
        binding.soundAfterAllPhotosSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                viewModel.setMakeSoundAfterAllPhotos(appContext, isChecked)
            }
        }
    }
}