package com.example.cameratopdf

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.cameratopdf.databinding.ActivityMainBinding
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.OrientationEventListener
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.MeteringPointFactory
import androidx.camera.core.TorchState
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import com.example.cameratopdf.ui.settings.other.OtherSettingsViewModel
import com.example.cameratopdf.ui.settings.other.OtherSettingsViewModel.Companion.otherSettings
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    enum class Rotation {
        PORTRAIT, LANDSCAPE, REVERSE_PORTRAIT, REVERSE_LANDSCAPE
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var orientationEventListener: OrientationEventListener
    private var selectedCamera = CameraSelector.DEFAULT_BACK_CAMERA
    private var torchState : Int? = TorchState.OFF

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<MarginLayoutParams> {
                leftMargin = insets.left
                rightMargin = insets.right
                bottomMargin = insets.bottom
            }
            binding.appName.updateLayoutParams<MarginLayoutParams> {
                topMargin = insets.top + 20
            }
            binding.settingsButton.updateLayoutParams<MarginLayoutParams> {
                topMargin = insets.top + 20
            }
            binding.infoButton.updateLayoutParams<MarginLayoutParams> {
                topMargin = insets.top + 20
            }
            WindowInsetsCompat.CONSUMED
        }
        loadTheme(this)

        // Device rotation listener
        orientationEventListener = object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) return

                // Determine the rotation of the screen
                val rotation = when (orientation) {
                    in 45..134 -> Rotation.REVERSE_LANDSCAPE
                    in 135..224 -> Rotation.REVERSE_PORTRAIT
                    in 225..314 -> Rotation.LANDSCAPE
                    else -> Rotation.PORTRAIT
                }
                rotateView(rotation)
            }
        }
        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable()
        } else {
            Log.e(TAG, "Orientation detection not supported")
        }

        // Request camera permissions and start camera
        if (allPermissionsGranted()) {
            startCamera(selectedCamera)
        } else {
            requestPermissions()
        }

        // Switch camera
        binding.switchCameraButton.setOnClickListener {
            selectedCamera = if (selectedCamera == CameraSelector.DEFAULT_FRONT_CAMERA)
                CameraSelector.DEFAULT_BACK_CAMERA
            else
                CameraSelector.DEFAULT_FRONT_CAMERA
            startCamera(selectedCamera)
        }

        // Open settings
        binding.settingsButton.setOnClickListener {
            val intent = Intent(this, ContentActivity::class.java)
            startActivity(intent)
        }

        // Open info
        binding.infoButton.setOnClickListener {
            lifecycleScope.launch {
                MaterialAlertDialogBuilder(this@MainActivity)
                    .setTitle(resources.getString(R.string.info_title))
                    .setMessage(resources.getString(R.string.info_message))
                    .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setIcon(R.drawable.ic_info)
                    .show()
            }
        }
    }

    // Starts camera preview
    @SuppressLint("ClickableViewAccessibility")
    @OptIn(ExperimentalCamera2Interop::class)
    private fun startCamera(selector: CameraSelector) {
        val cameraController = LifecycleCameraController(this)
        cameraController.bindToLifecycle(this)
        cameraController.cameraSelector = selector
        binding.viewFinder.controller = cameraController
        cameraController.isTapToFocusEnabled = false
        cameraController.isPinchToZoomEnabled = false

        // Flash toggle
        cameraController.torchState.observe(this) {
            torchState = it
            torchState?.let { state ->
                if (state == TorchState.OFF) {
                    binding.flashButton.foreground = AppCompatResources.getDrawable(this,  R.drawable.flash_off_button)
                } else {
                    binding.flashButton.foreground = AppCompatResources.getDrawable(this,  R.drawable.flash_on_button)
                }
            }
        }
        binding.flashButton.setOnClickListener {
            if (torchState == TorchState.OFF)
                cameraController.enableTorch(true)
            else
                cameraController.enableTorch(false)
        }

        // Detect tap to focus and pinch to zoom gestures
        val gestureDetector = GestureDetector(this, GestureListener(cameraController))
        val scaleGestureDetector = ScaleGestureDetector(this, ScaleListener(cameraController))
        binding.viewFinder.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            scaleGestureDetector.onTouchEvent(event)
        }
    }

    // Request camera permissions
    private fun requestPermissions() = activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(
                    baseContext,
                    resources.getString(R.string.camera_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                startCamera(selectedCamera)
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
            mutableListOf(
                Manifest.permission.CAMERA,
            ).toTypedArray()
    }

    // Rotate view based on device orientation
    private fun rotateView(rotation: Rotation) {
        val rotationDegree = when (rotation) {
            Rotation.PORTRAIT -> 0
            Rotation.LANDSCAPE -> 90
            Rotation.REVERSE_PORTRAIT -> 180
            Rotation.REVERSE_LANDSCAPE -> 270
        }
        binding.pdfsButton.rotation = rotationDegree.toFloat()
        binding.switchCameraButton.rotation = rotationDegree.toFloat()
        binding.infoButton.rotation = rotationDegree.toFloat()
        binding.settingsButton.rotation = rotationDegree.toFloat()
    }

    // Tap to focus listener
    private inner class GestureListener(val cameraController: CameraController) : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(event: MotionEvent): Boolean {
            val factory: MeteringPointFactory = binding.viewFinder.meteringPointFactory
            val point = factory.createPoint(event.x, event.y)
            val action = FocusMeteringAction.Builder(point).build()
            cameraController.cameraControl?.startFocusAndMetering(action)

            // Show focus circle
            binding.focusCircleView.setCirclePosition(event.x, event.y)
            binding.focusCircleView.visibility = View.VISIBLE

            // Hide focus circle after a delay
            binding.focusCircleView.postDelayed({
                binding.focusCircleView.visibility = View.GONE
            }, 3000)
            return true
        }
    }

    // Pinch to zoom listener
    private inner class ScaleListener(val cameraController: CameraController): ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            cameraController.cameraControl?.setZoomRatio(
                cameraController.cameraInfo?.zoomState?.value?.zoomRatio?.times(detector.scaleFactor) ?: 1f
            )
            return true
        }
    }

    private fun loadTheme(context: Context) {
        lifecycleScope.launch {
            val settings = context.otherSettings.data.first()
            val theme =
                settings[OtherSettingsViewModel.THEME] ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            when (theme) {
                AppCompatDelegate.MODE_NIGHT_YES -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )

                AppCompatDelegate.MODE_NIGHT_NO -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )

                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }
}