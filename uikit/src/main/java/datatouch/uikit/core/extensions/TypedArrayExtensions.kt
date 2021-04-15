package datatouch.uikit.core.extensions

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

object TypedArrayExtensions {

    fun TypedArray.getAppCompatDrawable(context: Context, styleableResIndex: Int): Drawable? {
        val drawableResId = getResourceId(styleableResIndex, InvalidResourceId)
        return if (drawableResId == InvalidResourceId) null
        else ContextCompat.getDrawable(context, drawableResId)
    }

}

private const val InvalidResourceId = -1