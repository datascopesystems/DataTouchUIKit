package datatouch.uikit.components

import android.annotation.SuppressLint
import android.content.Context

import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

open class NonScrollableViewPager : ViewPager {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent) = false
    override fun onInterceptTouchEvent(event: MotionEvent) = false
}