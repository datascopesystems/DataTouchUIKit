package datatouch.uikit.core.utils

import android.graphics.Color

object ColorUtils {

    fun darkenColor(color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] = hsv[2] * 0.8f
        return Color.HSVToColor(hsv)
    }

}