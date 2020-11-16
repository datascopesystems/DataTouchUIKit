package datatouch.uikit.core.utils.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.util.Consumer
import datatouch.uikit.core.utils.Conditions.isNotNull

object ViewUtils {
    fun toggleViewVisibility(target: View, isVisible: Boolean) {
        target.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    fun isViewAddedToParent(view: View, viewGroup: ViewGroup): Boolean {
        val existing = viewGroup.findViewById<View>(view.id)
        if (null == existing) {
            val parent = view.parent as ViewGroup
            return isNotNull(parent)
        }
        return true
    }

    fun tryToRemoveViewFromParent(view: View) {
        try {
            val parent = view.parent as ViewGroup
            parent.removeView(view)
        } catch (ignored: Exception) {
        }
    }

    fun <T : View?> iterateOverMultipleChildViews(
        parent: ViewGroup,
        childViewClasses: List<Class<*>?>,
        consumer: Consumer<T>
    ) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child != null && childViewClasses.contains(child.javaClass)) {
                consumer.accept(child as T)
            } else if (child is ViewGroup) {
                iterateOverMultipleChildViews(
                    child,
                    childViewClasses,
                    consumer
                )
            }
        }
    }

    fun getHexStringColor(intColor: Int): String {
        return String.format("#%06X", 0xFFFFFF and intColor)
    }

    fun getBackgroundColor(view: View): Int {
        var color = Color.BLACK
        if (view.background is ColorDrawable) color =
            (view.background as ColorDrawable).color
        return color
    }

    fun convertDp2Px(context: Context, dp: Int): Int {
        val r = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            r.displayMetrics
        ).toInt()
    }

    fun setViewMarginEnd(v: View, marginEndDP: Int) {
        if (isNotNull(v)) {
            val layoutParams = v.layoutParams
            if (layoutParams is MarginLayoutParams) {
                val context = v.context
                val margin =
                    if (isNotNull(context)) convertDp2Px(
                        context,
                        marginEndDP
                    ) else marginEndDP
                val lp = layoutParams
                lp.marginEnd = margin
                v.layoutParams = lp
            }
        }
    }
}