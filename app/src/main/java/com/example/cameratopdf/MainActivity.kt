package com.example.cameratopdf

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.example.cameratopdf.databinding.ActivityMainBinding
import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.Surface
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {

    enum class Rotation {
        PORTRAIT, LANDSCAPE, REVERSE_LANDSCAPE, UNKNOWN
    }

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Inflate view and adjust it
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemInsets = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = systemInsets.left
                bottomMargin = systemInsets.bottom
                rightMargin = systemInsets.right
            }
            WindowInsetsCompat.CONSUMED
        }
        adjustViewToRotation()

        // Request camera permissions and start camera
        if (allPermissionsGranted()) {
            startCamera()
        }
        else {
            requestPermissions()
        }
    }

    // Starts camera preview
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = binding.viewFinder.surfaceProvider
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview)

            } catch(exc: Exception) {
                Log.e(TAG, "Camera preview failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    // Request camera permissions
    private fun requestPermissions() = activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(baseContext,
                    resources.getString(R.string.camera_permission_denied),
                    Toast.LENGTH_SHORT).show()
            } else {
                startCamera()
            }
        }

    // Check if all permissions are granted
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Constants
    companion object {
        private const val TAG = "CameraToPdf"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
            ).toTypedArray()
    }

    // Get device rotation
    private fun getRotation(context: Context): Rotation {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val rotation: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            rotation = this.display.rotation
        }
        else {
            @Suppress("DEPRECATION")
            rotation = windowManager.defaultDisplay.rotation
        }
        return when (rotation) {
            Surface.ROTATION_0 -> Rotation.PORTRAIT
            Surface.ROTATION_90 -> Rotation.LANDSCAPE
            Surface.ROTATION_270 -> Rotation.REVERSE_LANDSCAPE
            else -> Rotation.UNKNOWN
        }
    }

    // Resets constraint layout parameters, used when adjusting view to rotation
    private fun resetLayoutParams() {
        binding.imageCaptureButton.updateLayoutParams<ConstraintLayout.LayoutParams> {
            startToStart = ConstraintLayout.LayoutParams.UNSET
            startToEnd = ConstraintLayout.LayoutParams.UNSET
            endToStart = ConstraintLayout.LayoutParams.UNSET
            endToEnd = ConstraintLayout.LayoutParams.UNSET
            topToTop = ConstraintLayout.LayoutParams.UNSET
            topToBottom = ConstraintLayout.LayoutParams.UNSET
            bottomToTop = ConstraintLayout.LayoutParams.UNSET
            bottomToBottom = ConstraintLayout.LayoutParams.UNSET
            topMargin = 0
            rightMargin = 0
            bottomMargin = 0
            leftMargin = 0
            horizontalBias = 0.5f
            verticalBias = 0.5f
        }
        binding.switchCameraButton.updateLayoutParams<ConstraintLayout.LayoutParams> {
            startToStart = ConstraintLayout.LayoutParams.UNSET
            startToEnd = ConstraintLayout.LayoutParams.UNSET
            endToStart = ConstraintLayout.LayoutParams.UNSET
            endToEnd = ConstraintLayout.LayoutParams.UNSET
            topToTop = ConstraintLayout.LayoutParams.UNSET
            topToBottom = ConstraintLayout.LayoutParams.UNSET
            bottomToTop = ConstraintLayout.LayoutParams.UNSET
            bottomToBottom = ConstraintLayout.LayoutParams.UNSET
            topMargin = 0
            rightMargin = 0
            bottomMargin = 0
            leftMargin = 0
            horizontalBias = 0.5f
            verticalBias = 0.5f
        }
    }

    // Adjusts view to current device rotation
    private fun adjustViewToRotation() {
        resetLayoutParams()
        val rotation = getRotation(this)
        when (rotation) {
            Rotation.PORTRAIT -> {
                Log.i("TEST", "Rotation: portrait")
                binding.imageCaptureButton.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    bottomToBottom = binding.container.id
                    endToEnd = binding.container.id
                    startToStart = binding.container.id
                    bottomMargin = dpToPx(48f)
                }
                binding.switchCameraButton.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    bottomToBottom = binding.container.id
                    endToEnd = binding.container.id
                    startToEnd = binding.imageCaptureButton.id
                    bottomMargin = dpToPx(32f)
                    horizontalBias = 0.7f
                }
            }
            Rotation.LANDSCAPE -> {
                Log.i("TEST", "Rotation: landscape")
                binding.imageCaptureButton.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    endToEnd = binding.container.id
                    topToTop = binding.container.id
                    bottomToBottom = binding.container.id
                    rightMargin = dpToPx(48f)
                }
                binding.switchCameraButton.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    endToEnd = binding.container.id
                    topToTop = binding.container.id
                    bottomToTop = binding.imageCaptureButton.id
                    rightMargin = dpToPx(32f)
                    verticalBias = 0.3f
                }
            }
            Rotation.REVERSE_LANDSCAPE -> {
                Log.i("TEST", "Rotation: reverse landscape")
                binding.imageCaptureButton.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    startToStart = binding.container.id
                    topToTop = binding.container.id
                    bottomToBottom = binding.container.id
                    leftMargin = dpToPx(48f)
                }
                binding.switchCameraButton.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    startToStart = binding.container.id
                    topToBottom = binding.imageCaptureButton.id
                    bottomToBottom = binding.container.id
                    leftMargin = dpToPx(32f)
                    verticalBias = 0.7f
                }
            }
            else -> Unit
        }
    }

    // Converts dp to px
    private fun Context.dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics
        ).toInt()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        adjustViewToRotation()
    }
}