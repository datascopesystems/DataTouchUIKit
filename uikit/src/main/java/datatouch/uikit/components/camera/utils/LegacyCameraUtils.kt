package datatouch.uikit.components.camera.utils

import androidx.fragment.app.FragmentActivity
import datatouch.uikit.components.camera.fragments.ProxyLegacyCameraLaunchFragment

private const val LegacyCameraActivityRequestType = 86450

object LegacyCameraUtils {

    fun openPhotoCamera(activity: FragmentActivity, callback: CameraContentSavedCallback? = null) {
        val fm = activity.supportFragmentManager
        val proxyFragment =
            ProxyLegacyCameraLaunchFragment.new(LegacyCameraActivityRequestType, callback)
        fm.beginTransaction().add(proxyFragment, proxyFragment.javaClass.name).commit()
        fm.executePendingTransactions()
    }

}