package datatouch.uikit.core.utils

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import datatouch.uikit.R

object ColorUtils {

    fun darkenColor(color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] = hsv[2] * 0.8f
        return Color.HSVToColor(hsv)
    }

    fun getAccentColorFromThemeIfAvailable(context: Context): Int {
        val typedValue = TypedValue()
        // First, try the android:colorAccent
        if (Build.VERSION.SDK_INT >= 21) {
            context.theme.resolveAttribute(android.R.attr.colorAccent, typedValue, true)
            return typedValue.data
        }
        // Next, try colorAccent from support lib
        val colorAccentResId =
            context.resources.getIdentifier("colorAccent", "attr", context.packageName)
        return if (colorAccentResId != 0 && context.theme.resolveAttribute(
                colorAccentResId,
                typedValue,
                true
            )
        ) {
            typedValue.data
        } else ContextCompat.getColor(context, R.color.amdp_accent_color)
        // Return the value in amdp_accent_color
    }

    fun isDarkTheme(context: Context, current: Boolean) =
        resolveBoolean(context, R.attr.amdp_theme_dark, current)

    private fun resolveBoolean(context: Context, @AttrRes attr: Int, fallback: Boolean): Boolean {
        val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
        return try {
            a.getBoolean(0, fallback)
        } finally {
            a.recycle()
        }
    }

}