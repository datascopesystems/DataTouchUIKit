package datatouch.uikit.components.camera.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import datatouch.uikit.R
import datatouch.uikit.components.camera.models.CameraActivityResult
import datatouch.uikit.components.camera.models.CameraConfiguration
import datatouch.uikit.components.camera.models.PhotoCameraActivityParams
import datatouch.uikit.core.extensions.IntExtensions.orZero
import java.io.File
import kotlin.random.Random

class PhotoCameraActivity : AppCompatActivity() {

    private var rootLayout: ViewGroup? = null
    private var previewView: PreviewView? = null
    private var btnShutter: View? = null
    private var btnSwitchCamera: View? = null

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private lateinit var activityParams: PhotoCameraActivityParams

    private val cameraProvider by lazy {
        ProcessCameraProvider.getInstance(this)
    }

    private val executor by lazy {
        ContextCompat.getMainExecutor(this)
    }

    private val metadata by lazy {
        packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA).metaData
    }

    private val permissions by lazy {
        listOf(Manifest.permission.CAMERA)
    }

    private val permissionsRequestCode by lazy {
        Random.nextInt(0, 10000)
    }

    private fun getConfigurationValue(key: String): Any? = when {
        intent.extras?.containsKey(key) == true -> intent.extras?.get(key)
        metadata?.containsKey(key) == true -> metadata.get(key)
        else -> null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        injectActivityParams()
        initViews()
        setupSeamlessRotation()
        setupShutterListener()
        setupSwitchCameraListener()
        applyUserConfiguration()
    }

    private fun injectActivityParams() {
        val params = intent.extras?.getSerializable(PhotoCameraActivityParams.Key) as? PhotoCameraActivityParams?
        params?.let { activityParams = it } ?: run {
            activityParams = PhotoCameraActivityParams(filesDir)
        }
    }

    private fun initViews() {
        previewView = findViewById(R.id.previewView)
        rootLayout = findViewById(R.id.rootLayout)
        btnShutter = findViewById(R.id.btnShutter)
        btnSwitchCamera = findViewById(R.id.btnSwitchCamera)
    }

    private fun setupSeamlessRotation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.attributes.rotationAnimation =
                WindowManager.LayoutParams.ROTATION_ANIMATION_SEAMLESS
        }
    }

    private fun setupShutterListener() {
        btnShutter?.setOnClickListener {

            // Disable all camera controls
            btnShutter?.isEnabled = false
            btnSwitchCamera?.isEnabled = false

            // Get a stable reference of the modifiable image capture use case
            imageCapture?.let { imageCapture ->

                // Create output file to hold the image
                // Setup image capture metadata
                val metadata = ImageCapture.Metadata().apply {
                    // Mirror image when using the front camera
                    isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
                }

                val newPhotoJpgFile = activityParams.newPhotoJpgFile()

                // Create output options object which contains file + metadata
                val outputOptions = ImageCapture.OutputFileOptions.Builder(newPhotoJpgFile)
                    .setMetadata(metadata)
                    .build()

                // Setup image capture listener which is triggered after photo has been taken
                imageCapture.takePicture(
                    outputOptions, executor, object : ImageCapture.OnImageSavedCallback {

                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            onImageSaved(newPhotoJpgFile)
                        }

                        override fun onError(exc: ImageCaptureException) {
                            cancelAndFinish()
                        }
                    })
            }
        }
    }

    private fun onImageSaved(photoFile: File) {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(activityParams.key, CameraActivityResult(photoFile))
        })
        finish()
    }

    private fun setupSwitchCameraListener() {
        btnSwitchCamera?.setOnClickListener {

            // Flip-flop the required lens facing
            lensFacing = if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
                CameraSelector.LENS_FACING_BACK
            } else {
                CameraSelector.LENS_FACING_FRONT
            }

            bindCameraUseCases()
        }
    }

    private fun applyUserConfiguration() {

        // If the user requested a specific lens facing, select it
        getConfigurationValue(CameraConfiguration.CAMERA_LENS_FACING)?.let {
            lensFacing = it as Int
        }

        // If the user disabled camera switching, hide the button
        if (true == getConfigurationValue(CameraConfiguration.CAMERA_SWITCH_DISABLED)) {
            btnSwitchCamera?.visibility = View.GONE
        }
    }

    private fun cancelAndFinish() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    /** Declare and bind preview, capture and analysis use cases */
    private fun bindCameraUseCases() = previewView?.post {
        cameraProvider.addListener(Runnable {

            // Camera provider is now guaranteed to be available
            val cameraProvider = cameraProvider.get()

            // Set up the view finder use case to display camera preview
            preview = Preview.Builder()
                .setTargetRotation(previewView?.display?.rotation.orZero())
                .build()
                .apply { setSurfaceProvider(previewView?.surfaceProvider) }

            // Set up the capture use case to allow users to take photos
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetRotation(previewView?.display?.rotation.orZero())
                .build()

            // Create a new camera selector each time, enforcing lens facing
            val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

            // Apply declared configs to CameraX using the same lifecycle owner
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this as LifecycleOwner, cameraSelector, preview, imageCapture
            )
        }, executor)
    }

    override fun onResume() {
        super.onResume()

        // Request permissions each time the app resumes, since they can be revoked at any time
        if (!hasPermissions(this)) {
            ActivityCompat.requestPermissions(
                this, permissions.toTypedArray(), permissionsRequestCode
            )
        } else {
            bindCameraUseCases()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionsRequestCode && hasPermissions(this)) {
            bindCameraUseCases()
        } else {
            // Indicate that the user cancelled the action and exit if no permissions are granted
            cancelAndFinish()
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    private fun hasPermissions(context: Context) = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

}