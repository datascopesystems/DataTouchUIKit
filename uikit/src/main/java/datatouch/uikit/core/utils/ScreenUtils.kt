package datatouch.uikit.core.utils

import android.app.Activity
import android.util.DisplayMetrics
import datatouch.uikit.R


object ScreenUtils {

    private var displayMetrics : DisplayMetrics? = null

    fun getDisplayMetrics(activity: Activity?): DisplayMetrics {
        return if (displayMetrics != null) displayMetrics!!
        else {
            displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            return displayMetrics!!
        }
    }

    fun getDisplayDensityString(activity: Activity?): String? {

        val density = getDisplayMetrics(activity).density

        if (density <= 0.75f) return activity?.getString(R.string.ldpi)

        if (density >= 1.0f && density < 1.5f) return activity?.getString(R.string.mdpi)

        if (density == 1.5f) return activity?.getString(R.string.hdpi)

        if (density > 1.5f && density <= 2.0f) return activity?.getString(R.string.xhdpi)

        if (density > 2.0f && density <= 3.0f) return activity?.getString(R.string.xxhdpi)

        return activity?.getString(R.string.xxxhdpi)
    }

    fun getRefreshRate(activity: Activity?) = activity?.windowManager?.defaultDisplay?.refreshRate
}