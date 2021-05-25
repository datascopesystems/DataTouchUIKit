package datatouch.uikit.components.pinpad

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.LayoutInflater
import android.widget.RelativeLayout
import datatouch.uikit.R
import datatouch.uikit.databinding.PinpadNumberButtonBinding

class CPinPadNumberButton : RelativeLayout, IResizeable {

    private val ui = PinpadNumberButtonBinding
        .inflate(LayoutInflater.from(context), this)

    var buttonNumber = 0

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        parseAttributes(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        parseAttributes(attrs)
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

    override fun onFinishInflate() {
        super.onFinishInflate()
        setBackgroundResource(R.drawable.pinpad_number_button_selector)
        ui.tvText.text = buttonNumber.toString()
    }

    override fun setSize(size: Float) {
        ui.tvText.setTextSize(COMPLEX_UNIT_PX, size)
    }
}