package com.example.cameratopdf.ui.list

import androidx.recyclerview.widget.RecyclerView
import com.example.cameratopdf.databinding.PdfDocumentListItemBinding
import com.example.cameratopdf.models.PdfFile

class PdfListViewHolder(private val pdfDocumentListItemBinding: PdfDocumentListItemBinding,
                             private val pdfListClickListener: PdfListClickListener
) : RecyclerView.ViewHolder(pdfDocumentListItemBinding.root) {
    fun bindImagePreview(pdfFile: PdfFile) {
        pdfDocumentListItemBinding.pdfFileName.text = pdfFile.name
        pdfDocumentListItemBinding.date.text = pdfFile.creationDate.toString()
        pdfDocumentListItemBinding.root.setOnClickListener {
            pdfListClickListener.onPdfClick(pdfFile)
        }
    }
}
