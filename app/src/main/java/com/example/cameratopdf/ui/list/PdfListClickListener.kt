package com.example.cameratopdf.ui.list

import com.example.cameratopdf.models.PdfFile

interface PdfListClickListener {
    fun onPdfClick(pdfFile: PdfFile)
}