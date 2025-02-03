package com.example.cameratopdf.ui.preview

import androidx.recyclerview.widget.RecyclerView
import com.example.cameratopdf.databinding.ImagePreviewBinding
import com.example.cameratopdf.models.CapturedImage

class ImagePreviewViewHolder(private val imagePreviewBinding: ImagePreviewBinding,
                             private val imagePreviewSelectedListener: ImagePreviewSelectedListener) : RecyclerView.ViewHolder(imagePreviewBinding.root) {
    fun bindImagePreview(capturedImage: CapturedImage) {
        imagePreviewBinding.photoLabel.text = capturedImage.label
        imagePreviewBinding.photoPreview.setImageURI(capturedImage.uri)
        imagePreviewBinding.photoCheckBox.isChecked = capturedImage.isSelected
        imagePreviewBinding.photoCheckBox.setOnCheckedChangeListener { _, _ ->
            imagePreviewSelectedListener.onImageSelected(capturedImage)
        }
    }
}
