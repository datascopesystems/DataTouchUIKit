package datatouch.uikit.components.camera.utils

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import datatouch.uikit.components.camera.fragments.ProxyCameraLaunchFragment
import datatouch.uikit.components.camera.models.CameraActivityParams
import datatouch.uikit.components.camera.models.CameraSavedContent
import datatouch.uikit.components.camera.models.PhotoCameraActivityParams
import datatouch.uikit.core.callbacks.UiCallback
import java.io.File

typealias CameraContentSavedCallback = UiCallback<CameraSavedContent>

private const val CameraActivityRequestCode = 4212

object CameraUtils {

    fun openPhotoCamera(
        activity: FragmentActivity,
        photoDirPath: String? = null,
        callback: CameraContentSavedCallback? = null) = startCameraActivityForResult(
        activity,
        PhotoCameraActivityParams(photoDirPath?.run { File(photoDirPath) } ?: activity.filesDir),
        callback)

    private fun startCameraActivityForResult(
        activity: FragmentActivity,
        params: CameraActivityParams,
        callback: CameraContentSavedCallback? = null) {
        val fm = activity.supportFragmentManager
        val proxyFragment = ProxyCameraLaunchFragment(fm, params.key, callback)
        fm.beginTransaction().add(proxyFragment, proxyFragment.javaClass.name).commit()
        fm.executePendingTransactions()

        proxyFragment.startActivityForResult(
            Intent(activity, params.cameraActivityClass), CameraActivityRequestCode)
    }

}