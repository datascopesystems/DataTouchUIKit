package datatouch.uikit.core.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import datatouch.uikit.components.tooltip.ColourUtils
import datatouch.uikit.components.windows.base.WindowActivity

object ContextExtensions {

    fun Context?.toWindowActivity(): WindowActivity<*>? {
        var context = this
        while (context !is Activity && context is ContextWrapper) {
            context = context.baseContext
        }
        return context as WindowActivity<*>?
    }

    fun Context?.toActivity(): Activity? {
        var context = this
        while (context !is Activity && context is ContextWrapper) {
            context = context.baseContext
        }
        return context as Activity?
    }

    fun Context?.drawable(drawableId: Int): Drawable? = this?.let { ContextCompat.getDrawable(this, drawableId) }

    fun Context?.colorString(resId: Int): String = when (this != null) {
        true -> ContextCompat.getColor(this, resId).let {
            ColourUtils.getHexStringColor(it)
        }
        else -> ColourUtils.getHexStringColor(ColourUtils.DEFAULT_COLOUR)
    }

    fun Context?.colorInt(resId: Int): Int = when (this != null) {
        true -> ContextCompat.getColor(this, resId)
        else -> ColourUtils.DEFAULT_COLOUR
    }

}