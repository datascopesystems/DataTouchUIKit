package datatouch.uikit.components.camera.utils

import androidx.fragment.app.FragmentActivity

object CameraUtils {

    fun openPhotoCamera(
        activity: FragmentActivity,
        photoDirPath: String? = null,
        callback: CameraContentSavedCallback? = null) {

        // While CameraX is still alpha and unstable, using legacy utils
        LegacyCameraUtils.openPhotoCamera(activity, callback)

        //CameraXUtils.openPhotoCamera(activity, photoDirPath, callback)
    }

}