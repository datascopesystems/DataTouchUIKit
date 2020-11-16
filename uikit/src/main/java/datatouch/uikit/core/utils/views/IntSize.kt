package datatouch.uikit.core.utils.views

import android.content.Context
import datatouch.uikit.core.extensions.IntExtensions.toDp

class IntSize(var w: Int = 0, var h: Int = 0) {

    fun isPositive(): Boolean {
        return w > 0 && h > 0
    }

    fun set(size: IntSize) {
        w = size.w
        h = size.h
    }

    companion object {
        @JvmStatic
        fun ofDP(context: Context?, dpWidth: Int, dpHeight: Int): IntSize {
            return context?.run {
                IntSize(dpWidth.toDp(this), dpHeight.toDp(this))
            } ?: IntSize(dpWidth, dpHeight)
        }
    }
}