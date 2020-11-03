package datatouch.uikit.components.pinpad

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.View
import android.widget.RelativeLayout
import datatouch.uikit.R
import kotlinx.android.synthetic.main.pinpad_ok_button.view.*

open class CPinPadOkButton : RelativeLayout, IResizeable {
    constructor(context: Context?) : super(context) {
        inflateView()
        afterViews()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        inflateView()
        afterViews()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        inflateView()
        afterViews()
    }

    fun afterViews() {
        setBackgroundResource(R.drawable.pinpad_ok_button_background)
    }


    private fun inflateView() {
        View.inflate(context, R.layout.pinpad_ok_button, this)
    }

    override fun setSize(size: Float) {
        tvText?.setTextSize(COMPLEX_UNIT_PX, size)
    }
}