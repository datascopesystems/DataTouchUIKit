package datatouch.uikit.components.pinpad

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.LayoutInflater
import android.widget.RelativeLayout
import datatouch.uikit.R
import datatouch.uikit.databinding.PinpadCancelButtonBinding

class CPinPadCancelButton : RelativeLayout, IResizeable {

    private val ui = PinpadCancelButtonBinding
        .inflate(LayoutInflater.from(context), this)

    constructor(context: Context?) : super(context) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setBackgroundResource(R.drawable.pinpad_cancel_button_background)
    }

    override fun setSize(size: Float) {
        ui.tvText.setTextSize(COMPLEX_UNIT_PX, size)
    }
}