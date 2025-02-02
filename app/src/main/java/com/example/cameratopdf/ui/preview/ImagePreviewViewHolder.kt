package com.example.cameratopdf.ui.preview

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.example.cameratopdf.databinding.ImagePreviewBinding
import com.example.cameratopdf.models.CapturedImage

class ImagePreviewViewHolder(private val imagePreviewBinding: ImagePreviewBinding) : RecyclerView.ViewHolder(imagePreviewBinding.root) {
    fun bindImagePreview(capturedImage: CapturedImage) {
        imagePreviewBinding.photoLabel.text = capturedImage.label
        imagePreviewBinding.photoPreview.setImageURI(capturedImage.uri)
    }
}
