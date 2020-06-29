package datatouch.uikit.components

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView

class InterceptableHorizontalScrollView : HorizontalScrollView {
    private var interceptTouchEvents = false

    constructor(context: Context) : super(context) {}
    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return interceptTouchEvents || super.onInterceptTouchEvent(ev)
    }

    fun setAlwaysInterceptTouchEvents() {
        interceptTouchEvents = true
    }
}