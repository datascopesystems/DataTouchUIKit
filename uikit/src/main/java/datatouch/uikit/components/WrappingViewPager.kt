package datatouch.uikit.components

import android.content.Context

import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.viewpager.widget.ViewPager
import datatouch.uikit.utils.ResourceUtils

class WrappingViewPager : ViewPager {

    var verticalPaddingHeight: Int = 0

    private var currentPageView: View? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var newHeightMeasured = heightMeasureSpec
        if (currentPageView == null) {
            super.onMeasure(widthMeasureSpec, newHeightMeasured)
            return
        }
        var height = 0
        currentPageView!!.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
        val h = currentPageView!!.measuredHeight
        if (h > height) height = h
        newHeightMeasured = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)

        val viewHeightSize = MeasureSpec.getSize(newHeightMeasured) + verticalPaddingHeight
        val screenHeightSize = ResourceUtils.getDisplayContentHeight(context)
        if (viewHeightSize > screenHeightSize) {
            layoutParams.height = MATCH_PARENT
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        } else {
            layoutParams.height = WRAP_CONTENT
            super.onMeasure(widthMeasureSpec, newHeightMeasured)
        }
    }

    fun measureCurrentView(currentView: View?) {
        currentPageView = currentView
        requestLayout()
    }

}