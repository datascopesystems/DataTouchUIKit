package datatouch.uikit.components.recyclerview

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import datatouch.uikit.R


class VerticalLineDivider : DividerItemDecoration {

    private var dividerDrawable: Drawable?

    constructor(context: Context) : super(context, HORIZONTAL) {
        dividerDrawable = ContextCompat.getDrawable(context, R.drawable.line_divider)
        dividerDrawable?.let { setDrawable(it) }
    }

    constructor(context: Context, color: Int) : this(context) {
        dividerDrawable?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY)
        dividerDrawable?.let { setDrawable(it) }
    }

}