package com.example.cameratopdf.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cameratopdf.databinding.PdfDocumentListItemBinding
import com.example.cameratopdf.models.PdfFile

class PdfListAdapter(private val pdfFiles: List<PdfFile>,
                     private val pdfListClickListener: PdfListClickListener) : RecyclerView.Adapter<PdfListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfListViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = PdfDocumentListItemBinding.inflate(from, parent, false)
        return PdfListViewHolder(binding, pdfListClickListener)
    }

    override fun getItemCount(): Int = pdfFiles.size

    override fun onBindViewHolder(holder: PdfListViewHolder, position: Int) {
        holder.bindImagePreview(pdfFiles[position])
    }
}