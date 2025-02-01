package com.example.cameratopdf

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.cameratopdf.databinding.ActivityMainBinding
import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import android.view.OrientationEventListener
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding

class MainActivity : AppCompatActivity() {

    enum class Rotation {
        PORTRAIT, LANDSCAPE, REVERSE_PORTRAIT, REVERSE_LANDSCAPE
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var orientationEventListener: OrientationEventListener
    private var selectedCamera = CameraSelector.DEFAULT_BACK_CAMERA

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
                topMargin = insets.top
            }
            binding.settingsButton.updateLayoutParams<MarginLayoutParams> {
                topMargin = insets.top
            }
            WindowInsetsCompat.CONSUMED
        }

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

        // Switch camera on button press
        binding.switchCameraButton.setOnClickListener {
            selectedCamera = if (selectedCamera == CameraSelector.DEFAULT_FRONT_CAMERA)
                CameraSelector.DEFAULT_BACK_CAMERA
            else
                CameraSelector.DEFAULT_FRONT_CAMERA
            startCamera(selectedCamera)
        }
    }

    // Starts camera preview
    @OptIn(ExperimentalCamera2Interop::class)
    private fun startCamera(selector: CameraSelector) {
        val cameraController = LifecycleCameraController(this)
        cameraController.bindToLifecycle(this)
        cameraController.cameraSelector = selector
        binding.viewFinder.controller = cameraController
        cameraController.isTapToFocusEnabled = true
        cameraController.isPinchToZoomEnabled = true
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
        binding.switchCameraButton.rotation = rotationDegree.toFloat()
        binding.settingsButton.rotation = rotationDegree.toFloat()
    }
}