package com.example.cameratopdf.ui.list

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cameratopdf.R
import com.example.cameratopdf.databinding.FragmentPdfListBinding
import com.example.cameratopdf.models.PdfFile
import com.example.cameratopdf.network.PdfApiClient
import com.example.cameratopdf.ui.settings.other.OtherSettingsViewModel
import com.example.cameratopdf.ui.settings.other.OtherSettingsViewModel.Companion.otherSettings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class PdfListFragment : Fragment(), PdfListClickListener {
    private lateinit var binding: FragmentPdfListBinding
    private lateinit var appContext: Context
    private var pdfFiles = mutableListOf<PdfFile>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPdfListBinding.inflate(inflater, container, false)
        appContext = requireContext().applicationContext
        (activity as AppCompatActivity?)?.supportActionBar?.title = getString(R.string.pdf_list_title)
        getPdfsList()
        binding.pdfListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = PdfListAdapter(pdfFiles, this@PdfListFragment)
        }

        binding.refreshButton.setOnClickListener {
            getPdfsList()
        }
        return binding.root
    }

    private fun getPdfsList() = lifecycleScope.launch {
        try {
            val settings = appContext.otherSettings.data.first()
            val serverUrl = settings[OtherSettingsViewModel.SERVER_ADDRESS]
            if (serverUrl != null && serverUrl != "") {
                binding.progressIndicator.visibility = View.VISIBLE
                val client = PdfApiClient(serverUrl)
                val pdfs = client.getAllFilesList()
            }
        }
        catch (e: Exception) {
            Log.e("TEST", e.message.toString())
        }
        finally {
            binding.progressIndicator.visibility = View.GONE
        }
    }

    override fun onPdfClick(pdfFile: PdfFile) {
        TODO("Not yet implemented")
    }
}