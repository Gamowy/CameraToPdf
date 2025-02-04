package com.example.cameratopdf

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.example.cameratopdf.databinding.ActivityContentBinding
import com.example.cameratopdf.ui.list.PdfListFragment
import com.example.cameratopdf.ui.preview.ImagesPreviewFragment
import com.example.cameratopdf.ui.settings.SettingsFragment

class ContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityContentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<MarginLayoutParams> {
                topMargin = insets.top
                leftMargin = insets.left
                rightMargin = insets.right
                bottomMargin = insets.bottom
            }
            WindowInsetsCompat.CONSUMED
        }
        setSupportActionBar(binding.appbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val extra = intent.getStringExtra("openFragment")
        extra?.let {
            val openFragment = when (it) {
                "settings" -> SettingsFragment()
                "preview" -> ImagesPreviewFragment()
                "pdfs" -> PdfListFragment()
                else -> null
            }
            if (openFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, openFragment)
                    .commit()
                return
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    @SuppressLint("ChromeOsOnConfigurationChanged")
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        finish()
    }
}