package datatouch.uikit.core.extensions

import android.content.Context
import android.util.TypedValue

object IntExtensions {

    inline fun Int?.isValidId() = this != null && this.isValidId()

    inline fun Int.isValidId() = this > 0

    inline fun Int?.orZero(): Int = this ?: 0

    inline fun Int.dp2Px(context: Context) = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics).toInt()
}