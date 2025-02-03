package com.example.cameratopdf.ui.preview

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cameratopdf.R
import com.example.cameratopdf.databinding.FragmentImagesPreviewBinding
import com.example.cameratopdf.models.CapturedImage
import com.example.cameratopdf.network.PdfApiClient
import com.example.cameratopdf.ui.settings.other.OtherSettingsViewModel
import com.example.cameratopdf.ui.settings.other.OtherSettingsViewModel.Companion.otherSettings
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ImagesPreviewFragment : Fragment(), ImagePreviewSelectedListener {
    private lateinit var binding: FragmentImagesPreviewBinding
    private lateinit var appContext: Context
    private var images = mutableListOf<CapturedImage>()
    private var selectedImages = mutableListOf<CapturedImage>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImagesPreviewBinding.inflate(inflater, container, false)
        appContext = requireContext().applicationContext
        (activity as AppCompatActivity?)?.supportActionBar?.title = getString(R.string.preview_images)
        getImagesFromCache()
        binding.imagesPreviewRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ImagePreviewAdapter(images, this@ImagesPreviewFragment)
        }

        // Send images to server
        binding.sendButton.setOnClickListener {
            sendImages(appContext)
        }

        return binding.root
    }

    private fun getImagesFromCache() {
        val cacheDir = requireContext().cacheDir
        var index = 0
        cacheDir.listFiles()?.forEach {
            index++
            val label = getString(R.string.image_label, index)
            images.add(CapturedImage(label, Uri.fromFile(it)))
            selectedImages.add(CapturedImage(label, Uri.fromFile(it)))
        }
    }

    private fun sendImages(context: Context) = lifecycleScope.launch {
            try {
                val settings = context.otherSettings.data.first()
                val serverUrl = settings[OtherSettingsViewModel.SERVER_ADDRESS]
                if (selectedImages.isEmpty()) {
                    throw Exception(getString(R.string.error_no_images))
                }
                if (serverUrl != null && serverUrl != "") {
                    binding.progressIndicator.visibility = View.VISIBLE
                    val client = PdfApiClient(serverUrl)
                    if (client.clearImages()) {
                        val uploadList = mutableListOf<Deferred<Boolean>>()
                        for (image in selectedImages) {
                            uploadList.add(async { client.uploadImage(image) })
                        }
                        val resultsList = uploadList.awaitAll()
                        if (resultsList.all { true }) {
                            generatePdf(client)
                        }
                    }
                } else {
                   throw Exception(getString(R.string.error_address_not_set))
                }
            } catch (e: Exception) {
                showErrorDialog(e.message.toString())
            } finally {
                binding.progressIndicator.visibility = View.GONE
            }
    }

    private suspend fun generatePdf(client: PdfApiClient) {




    }

    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.error_dialog_title))
            .setMessage(resources.getString(R.string.error_dialog_message, message))
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .setIcon(R.drawable.ic_error)
            .show()
    }

    override fun onImageSelected(capturedImage: CapturedImage) {
        if (selectedImages.contains(capturedImage)) {
            selectedImages.remove(capturedImage)
            capturedImage.isSelected = false
        } else {
            selectedImages.add(capturedImage)
            capturedImage.isSelected = true
        }
    }
}