package datatouch.uikit.components.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import datatouch.uikit.R
import datatouch.uikit.core.extensions.IntExtensions.orZero

class HorizontalLineDivider(context: Context) : ItemDecoration() {

    private val divider: Drawable? = ContextCompat.getDrawable(context, R.drawable.line_divider)

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + divider?.intrinsicHeight.orZero()
            divider?.setBounds(left, top, right, bottom)
            divider?.draw(c)
        }
    }

}