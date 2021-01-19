package datatouch.uikit.components.camera.utils

import androidx.fragment.app.FragmentActivity
import datatouch.uikit.components.camera.fragments.ProxyLegacyCameraLaunchFragment

private const val LegacyCameraActivityRequestType = 86450

object LegacyCameraUtils {

    // Use this camera for Android API < 23
    // Because of some CameraX issues on older Android
    fun openPhotoCamera(
        activity: FragmentActivity,
        callback: CameraContentSavedCallback? = null
    ) {
        val fm = activity.supportFragmentManager
        val proxyFragment =
            ProxyLegacyCameraLaunchFragment.new(LegacyCameraActivityRequestType, callback)
        fm.beginTransaction().add(proxyFragment, proxyFragment.javaClass.name).commit()
        fm.executePendingTransactions()
    }

}