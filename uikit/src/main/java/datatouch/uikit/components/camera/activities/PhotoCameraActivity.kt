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
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
import androidx.camera.core.CameraSelector.LENS_FACING_FRONT
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import datatouch.uikit.R
import datatouch.uikit.components.camera.models.CameraActivityResult
import datatouch.uikit.components.camera.models.PhotoCameraActivityParams
import datatouch.uikit.components.camera.utils.CameraUtils
import datatouch.uikit.components.toast.ToastNotification
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
    private var lensFacing: Int = LENS_FACING_BACK
    private lateinit var activityParams: PhotoCameraActivityParams

    private val cameraProvider by lazy { ProcessCameraProvider.getInstance(this) }

    private val executor by lazy { ContextCompat.getMainExecutor(this) }

    private val permissions by lazy { listOf(Manifest.permission.CAMERA) }

    private val permissionsRequestCode by lazy { Random.nextInt(0, 10000) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        injectActivityParams()

        if (!CameraUtils.anyCameraAvailable(this)) {
            ToastNotification.showError(this, R.string.no_cameras_detected)
            cancelAndFinish()
            return
        }

        initViews()
        setupSeamlessRotation()
        setupShutterListener()
        setupLensFacing()
    }

    private fun injectActivityParams() {
        val params =
            intent.extras?.getSerializable(PhotoCameraActivityParams.Key) as? PhotoCameraActivityParams?
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
                    isReversedHorizontal = lensFacing == LENS_FACING_FRONT
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

    private fun setupLensFacing() {
        if (!CameraUtils.bothBackAndFrontCamerasAvailable(this)) {
            btnSwitchCamera?.isVisible = false
            return
        }

        btnSwitchCamera?.isVisible = true
        btnSwitchCamera?.setOnClickListener {
            lensFacing = if (LENS_FACING_FRONT == lensFacing)
                LENS_FACING_BACK
            else
                LENS_FACING_FRONT

            bindCamera()
        }

        when {
            hasBackCamera -> lensFacing = LENS_FACING_BACK
            hasFrontCamera -> lensFacing = LENS_FACING_FRONT
            else -> cancelAndFinish()
        }
    }

    private val hasBackCamera get() = cameraProvider.get().hasCamera(
        CameraUtils.cameraSelectorWithLensFacing(LENS_FACING_BACK))

    private val hasFrontCamera get() = cameraProvider.get().hasCamera(
        CameraUtils.cameraSelectorWithLensFacing(LENS_FACING_FRONT))

    private fun cancelAndFinish() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun bindCamera() {
            cameraProvider.addListener({
                    preview = Preview.Builder()
                        .setTargetRotation(previewView?.display?.rotation.orZero())
                        .build()
                        .apply { setSurfaceProvider(previewView?.surfaceProvider) }

                    imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(previewView?.display?.rotation.orZero())
                        .build()

                    // Prevent 'Could not retrieve native window from surface' error on legacy API
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                        previewView?.post { tryBindCamera() }
                    else
                        previewView?.postDelayed({ tryBindCamera() }, LegacyCameraStartDelayMs)

            }, executor)
    }

    private fun tryBindCamera() {
        try {
            val cameraProvider = cameraProvider.get()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this as LifecycleOwner,
                CameraUtils.cameraSelectorWithLensFacing(lensFacing),
                preview, imageCapture
            )
        } catch(e: Exception) {
            cancelAndFinish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!hasPermissions(this))
            ActivityCompat
                .requestPermissions(this, permissions.toTypedArray(), permissionsRequestCode)
        else
            bindCamera()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionsRequestCode && hasPermissions(this))
            bindCamera()
        else
            cancelAndFinish()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    private fun hasPermissions(context: Context) = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

}

private const val LegacyCameraStartDelayMs = 500L