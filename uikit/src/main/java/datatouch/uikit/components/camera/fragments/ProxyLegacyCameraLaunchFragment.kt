package datatouch.uikit.components.camera.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import datatouch.uikit.R
import datatouch.uikit.components.camera.models.CameraSavedContent
import datatouch.uikit.components.camera.utils.CameraContentSavedCallback
import datatouch.uikit.components.toast.ToastNotification
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.EasyImage.ImageSource
import java.io.File

class ProxyLegacyCameraLaunchFragment : Fragment() {

    private var requestType = 0
    private var callback: CameraContentSavedCallback? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EasyImage.openCameraForImage(this, requestType)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        fragmentManager?.beginTransaction()?.remove(this)?.commit()

        EasyImage.handleActivityResult(requestCode, resultCode, data, activity,
            object : DefaultCallback() {

                override fun onImagePickerError(e: Exception, source: ImageSource, type: Int) {
                    ToastNotification.showError(activity, R.string.error_capturing_image)
                }

                override fun onImagesPicked(
                    imageFiles: MutableList<File>,
                    source: ImageSource?,
                    type: Int
                ) {
                    if (requestType == type)
                        imageFiles.firstOrNull()
                            ?.let { callback?.invoke(CameraSavedContent(it)) }
                }

            }
        )
    }

    companion object {

        fun new(requestType : Int, callback: CameraContentSavedCallback?): ProxyLegacyCameraLaunchFragment {
            val f = ProxyLegacyCameraLaunchFragment()
            f.requestType = requestType
            f.callback = callback
            return f
        }

    }

}