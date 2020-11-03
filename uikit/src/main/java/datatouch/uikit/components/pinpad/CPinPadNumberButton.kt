package datatouch.uikit.components.pinpad

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.View
import android.widget.RelativeLayout
import datatouch.uikit.R
import kotlinx.android.synthetic.main.pinpad_number_button.view.*

class CPinPadNumberButton : RelativeLayout, IResizeable {
    var buttonNumber = 0


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        inflateView()
        parseAttributes(attrs)
        afterViews()
    }

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        inflateView()
        parseAttributes(attrs)
        afterViews()
    }

    private fun parseAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.CPinPadNumberButton, 0, 0
        )
        try {
            buttonNumber = typedArray.getInteger(R.styleable.CPinPadNumberButton_button_number, 0)
        } finally {
            typedArray.recycle()
        }
    }

    fun afterViews() {
        setBackgroundResource(R.drawable.pinpad_number_button_selector)
        tvText?.text = buttonNumber.toString()
    }

    protected fun inflateView() {
        View.inflate(context, R.layout.pinpad_number_button, this)
    }

    override fun setSize(size: Float) {
        tvText?.setTextSize(COMPLEX_UNIT_PX, size)
    }
}