package datatouch.uikit.components.pinpad

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.isVisible
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
        setAppearanceDefault()
    }


    private fun inflateView() {
        View.inflate(context, R.layout.pinpad_ok_button, this)
    }

    override fun setSize(size: Float) {
        tvText?.setTextSize(COMPLEX_UNIT_PX, size)
    }

    fun setAppearanceDefault() {
        tvText?.isVisible = true
        ivIcon?.isVisible = false
        setBackgroundResource(R.drawable.pinpad_ok_button_background)
    }

    fun setAppearanceArrowRight() {
        tvText?.isVisible = false
        ivIcon?.isVisible = true
        ivIcon?.setImageResource(R.drawable.ic_pinpad_ok_button_arrow_right)
        setBackgroundResource(R.drawable.pinpad_cancel_button_background)
    }
}