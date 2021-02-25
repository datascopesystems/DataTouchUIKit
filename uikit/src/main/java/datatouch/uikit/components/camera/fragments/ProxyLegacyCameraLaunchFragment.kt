package datatouch.uikit.components.camera.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import datatouch.uikit.R
import datatouch.uikit.components.camera.models.CameraSavedContent
import datatouch.uikit.components.camera.utils.CameraContentSavedCallback
import datatouch.uikit.components.toast.ToastNotification
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import java.io.File
import kotlin.random.Random


class ProxyLegacyCameraLaunchFragment : Fragment() {

    private val permissions by lazy { listOf(Manifest.permission.CAMERA) }
    private val permissionsRequestCode by lazy { Random.nextInt(0, 10000) }

    private var requestType = 0
    private var callback: CameraContentSavedCallback? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasPermissions(context!!))
            ActivityCompat
                .requestPermissions(activity!!, permissions.toTypedArray(), permissionsRequestCode)
        else
            EasyImage.openCameraForImage(this, requestType)
    }


    private fun hasPermissions(context: Context) = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        fragmentManager?.beginTransaction()?.remove(this)?.commit()

        EasyImage.handleActivityResult(requestCode, resultCode, data, activity,
            object : DefaultCallback() {

                override fun onImagePickerError(e: Exception, source: EasyImage.ImageSource, type: Int) {
                    ToastNotification.showError(activity, R.string.error_capturing_image)
                }

                override fun onImagesPicked(imageFiles: MutableList<File>, source: EasyImage.ImageSource?, type: Int) {
                    if (requestType == type)
                        imageFiles.firstOrNull()
                            ?.let { callback?.invoke(CameraSavedContent(it)) }
                }

            }
        )
    }

    companion object {
        fun new(requestType: Int, callback: CameraContentSavedCallback?): ProxyLegacyCameraLaunchFragment {
            val f = ProxyLegacyCameraLaunchFragment()
            f.requestType = requestType
            f.callback = callback
            return f
        }

    }

}