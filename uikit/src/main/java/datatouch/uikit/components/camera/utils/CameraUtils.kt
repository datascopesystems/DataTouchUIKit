package datatouch.uikit.components.camera.utils

import android.os.Build
import androidx.fragment.app.FragmentActivity

object CameraUtils {

    fun openPhotoCamera(
        activity: FragmentActivity,
        photoDirPath: String? = null,
        callback: CameraContentSavedCallback? = null) {

        // Prevent compatibility issues on API < 23 (Marshmallow)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            LegacyCameraUtils.openPhotoCamera(activity, callback)
        else {
            CameraXUtils.openPhotoCamera(activity, photoDirPath, callback)
        }

    }

}