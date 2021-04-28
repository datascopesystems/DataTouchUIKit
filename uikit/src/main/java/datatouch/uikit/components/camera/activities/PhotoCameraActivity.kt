package datatouch.uikit.components.camera.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
import androidx.camera.core.CameraSelector.LENS_FACING_FRONT
import androidx.camera.core.ImageCapture
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
import datatouch.uikit.components.camera.utils.CameraXUtils
import datatouch.uikit.components.toast.ToastNotification
import datatouch.uikit.core.extensions.IntExtensions.orZero
import datatouch.uikit.core.utils.views.Screenshot
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

        if (!CameraXUtils.anyCameraAvailable(this)) {
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
            playClickSound()
            val screenshot = Screenshot(previewView?.bitmap)
            val screenshotFile = activityParams.newPhotoJpgFile()
            screenshot.saveToFile(screenshotFile.path)
            onImageSaved(screenshotFile)
        }
    }

    private fun playClickSound() {
        kotlin.runCatching {
            MediaPlayer.create(applicationContext, R.raw.camera_click).start()
        }
    }

    private fun onImageSaved(photoFile: File) {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(activityParams.key, CameraActivityResult(photoFile))
        })

        finish()
    }

    private fun setupLensFacing() {
        if (!CameraXUtils.bothBackAndFrontCamerasAvailable(this)) {
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

    private val hasBackCamera
        get() = cameraProvider.get().hasCamera(
            CameraXUtils.cameraSelectorWith(LENS_FACING_BACK)
        )

    private val hasFrontCamera
        get() = cameraProvider.get().hasCamera(
            CameraXUtils.cameraSelectorWith(LENS_FACING_FRONT)
        )

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

            previewView?.post { tryBindCamera() }

        }, executor)
    }

    private fun tryBindCamera() {
        try {
            val cameraProvider = cameraProvider.get()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this as LifecycleOwner,
                CameraXUtils.cameraSelectorWith(lensFacing),
                preview, imageCapture
            )
        } catch (e: Exception) {
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