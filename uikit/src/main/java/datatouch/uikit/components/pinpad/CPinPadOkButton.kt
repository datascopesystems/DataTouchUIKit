package datatouch.uikit.components.pinpad

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import datatouch.uikit.R
import datatouch.uikit.databinding.PinpadOkButtonBinding

open class CPinPadOkButton : RelativeLayout, IResizeable {

    private val ui = PinpadOkButtonBinding
        .inflate(LayoutInflater.from(context), this)

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        setAppearanceDefault()
    }

    override fun setSize(size: Float) {
        ui.tvText.setTextSize(COMPLEX_UNIT_PX, size)
    }

    fun setAppearanceDefault() {
        ui.tvText.isVisible = true
        ui.ivIcon.isVisible = false
        setBackgroundResource(R.drawable.pinpad_ok_button_background)
    }

    fun setAppearanceArrowRight() {
        ui.tvText.isVisible = false
        ui.ivIcon.isVisible = true
        ui.ivIcon.setImageResource(R.drawable.ic_arrow_thin_right_white)
        setBackgroundResource(R.drawable.pinpad_cancel_button_background)
    }
}