package com.example.cameratopdf.ui.preview

import com.example.cameratopdf.models.CapturedImage

interface ImagePreviewSelectedListener {
    fun onImageSelected(capturedImage: CapturedImage)
}