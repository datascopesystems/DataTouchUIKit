package datatouch.uikit.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.RectF
import android.util.DisplayMetrics
import android.view.View

object ResourceUtils {
    private const val DEFAULT_DARK_COEFFICIENT = 0.8f
    fun convertDpToPixel(context: Context, dp: Float): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun makeColorDarker(sourceHexColor: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(sourceHexColor, hsv)
        hsv[2] *= DEFAULT_DARK_COEFFICIENT
        return Color.HSVToColor(hsv)
    }

    fun setAlphaFromArgb(color: Int, argb: Int): Int {
        var argb = argb
        argb = argb or 0x00FFFFFF
        return color and argb
    }

    fun randomDarkAlphaColor(alpha: Int): Int {
        val r = (0xcf * Math.random()).toInt()
        val g = (0xcf * Math.random()).toInt()
        val b = (0xcf * Math.random()).toInt()
        return Color.argb(alpha, r, g, b)
    }

    fun convertDipToPixels(context: Context, dips: Float): Int {
        return (dips * context.resources.displayMetrics.density + 0.5f).toInt()
    }

    fun calculateRectOnScreen(view: View): RectF {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        return RectF(
            location[0].toFloat(),
            location[1].toFloat(),
            (location[0] + view.measuredWidth).toFloat(),
            (location[1] + view.measuredHeight).toFloat()
        )
    }

    fun getDisplayContentHeight(context: Context): Int {
        val screenHeight = screenHeight
        val navigationBarHeight = getNavigationBarHeight(context)
        val statusBarHeight = getStatusBarHeight(context)
        return screenHeight - navigationBarHeight - statusBarHeight
    }

    private val screenHeight: Int
        private get() = Resources.getSystem().displayMetrics.heightPixels

    private fun getNavigationBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    private fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId =
            context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}