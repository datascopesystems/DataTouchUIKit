package datatouch.uikit.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView
import datatouch.uikit.core.utils.Conditions.isNotNull

open class InterceptableWebView(
    context: Context?,
    attrs: AttributeSet?
) : WebView(context, attrs) {
    private var callback: OnTouchEventCallback? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        callback!!.onTouchEvent()
        return super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return false
    }

    fun setCallback(callback: OnTouchEventCallback?) {
        if (isNotNull(callback)) this.callback = callback
    }

    interface OnTouchEventCallback {
        fun onTouchEvent()
    }
}