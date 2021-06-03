package datatouch.uikit.core.device.identification

import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.telephony.TelephonyManager

object DeviceTypeUtils {

    fun isWearable(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_WATCH)
    }

    fun isPhone(context: Context): Boolean {
        val manager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return manager.phoneType != TelephonyManager.PHONE_TYPE_NONE
    }

    fun isTV(context: Context): Boolean {
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
                || context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
    }

    fun isTablet(context: Context) = context.resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE

}