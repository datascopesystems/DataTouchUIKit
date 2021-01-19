package datatouch.uikit.components.camera.utils

import android.content.Context
import android.content.Intent
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.fragment.app.FragmentActivity
import datatouch.uikit.components.camera.fragments.ProxyCameraLaunchFragment
import datatouch.uikit.components.camera.models.CameraActivityParams
import datatouch.uikit.components.camera.models.CameraSavedContent
import datatouch.uikit.components.camera.models.PhotoCameraActivityParams
import datatouch.uikit.core.callbacks.UiCallback
import java.io.File

typealias CameraContentSavedCallback = UiCallback<CameraSavedContent>

private const val CameraActivityRequestCode = 4212

object CameraXUtils {

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

    fun anyCameraAvailable(context: Context): Boolean {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        return hasBackCamera(cameraProvider) || hasFrontCamera(cameraProvider)
    }

    internal fun bothBackAndFrontCamerasAvailable(context: Context) : Boolean {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        return hasBackCamera(cameraProvider) && hasFrontCamera(cameraProvider)
    }

    internal fun hasBackCamera(cameraProvider: ProcessCameraProvider) =
        cameraProvider.hasCamera(cameraSelectorWithLensFacing(CameraSelector.LENS_FACING_BACK))

    internal fun cameraSelectorWithLensFacing(lensFacing: Int) =
        CameraSelector.Builder().requireLensFacing(lensFacing).build()

    internal fun hasFrontCamera(cameraProvider: ProcessCameraProvider) =
        cameraProvider.hasCamera(cameraSelectorWithLensFacing(CameraSelector.LENS_FACING_FRONT))
}