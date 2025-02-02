package com.example.cameratopdf.ui.preview

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cameratopdf.R
import com.example.cameratopdf.databinding.FragmentImagesPreviewBinding
import com.example.cameratopdf.models.CapturedImage

class ImagesPreviewFragment : Fragment() {
    private lateinit var binding: FragmentImagesPreviewBinding
    private var images = mutableListOf<CapturedImage>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImagesPreviewBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)?.supportActionBar?.title = getString(R.string.preview_images)
        getImagesFromCache()

        binding.imagesPreviewRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ImagePreviewAdapter(images)
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
        }
    }
}