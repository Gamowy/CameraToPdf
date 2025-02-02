package com.example.cameratopdf.ui.settings.other

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.cameratopdf.R
import com.example.cameratopdf.databinding.FragmentOtherSettingsBinding
import kotlinx.coroutines.launch

class OtherSettingsFragment : Fragment() {
    private lateinit var binding: FragmentOtherSettingsBinding
    private val viewModel: OtherSettingsViewModel by viewModels()
    private lateinit var appContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtherSettingsBinding.inflate(inflater, container, false)
        appContext = requireContext().applicationContext
        binding.themePicker.threshold = 100
        binding.languagePicker.threshold = 100

        lifecycleScope.launch {
            viewModel.loadSettings(appContext)
            binding.serverAddress.setText(viewModel.serverAddress.value)
            val theme = viewModel.theme.value
            when (theme) {
                AppCompatDelegate.MODE_NIGHT_YES -> binding.themePicker.setText(resources.getStringArray(R.array.themeList)[2])
                AppCompatDelegate.MODE_NIGHT_NO -> binding.themePicker.setText(resources.getStringArray(R.array.themeList)[1])
                else -> binding.themePicker.setText(resources.getStringArray(R.array.themeList)[0])
            }
        }

        val currentLocale = resources.configuration.locales[0]
        when (currentLocale.language) {
            "en" -> binding.languagePicker.setText(resources.getStringArray(R.array.languagesList)[0])
            "pl" -> binding.languagePicker.setText(resources.getStringArray(R.array.languagesList)[1])
        }
        binding.languagePicker.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
            val value = binding.languagePicker.text.toString()
            when (value) {
                "English" ->  {
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
                    activity?.finish()
                }
                "Polski" -> {
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("pl"))
                    activity?.finish()
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.serverAddress.addTextChangedListener { text ->
            lifecycleScope.launch {
                viewModel.setServerAddress(appContext, text.toString())
            }
        }
        binding.themePicker.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
            val value = binding.themePicker.text.toString()
            when (value) {
                resources.getStringArray(R.array.themeList)[2] -> {
                    lifecycleScope.launch {
                        viewModel.setTheme(appContext, AppCompatDelegate.MODE_NIGHT_YES)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                }
                resources.getStringArray(R.array.themeList)[1] -> {
                    lifecycleScope.launch {
                        viewModel.setTheme(appContext, AppCompatDelegate.MODE_NIGHT_NO)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
                resources.getStringArray(R.array.themeList)[0] -> {
                    lifecycleScope.launch {
                        viewModel.setTheme(appContext, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                }
            }
        }
    }
}