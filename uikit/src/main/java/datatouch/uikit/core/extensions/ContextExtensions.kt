package datatouch.uikit.core.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

object ContextExtensions {

    fun Context?.toActivity(): Activity? {
        var context = this
        while (context !is Activity && context is ContextWrapper) {
            context = context.baseContext
        }
        return context as Activity?
    }

    fun Context?.drawable(drawableId: Int): Drawable? = this?.let { ContextCompat.getDrawable(this, drawableId) }


}