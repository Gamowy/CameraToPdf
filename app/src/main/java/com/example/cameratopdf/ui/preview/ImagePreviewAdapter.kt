package com.example.cameratopdf.ui.preview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cameratopdf.databinding.ImagePreviewBinding
import com.example.cameratopdf.models.CapturedImage

class ImagePreviewAdapter(private val images: List<CapturedImage>,
                          private val imagePreviewSelectedListener: ImagePreviewSelectedListener) : RecyclerView.Adapter<ImagePreviewViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePreviewViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ImagePreviewBinding.inflate(from, parent, false)
        return ImagePreviewViewHolder(binding, imagePreviewSelectedListener)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImagePreviewViewHolder, position: Int) {
        holder.bindImagePreview(images[position])
    }
}