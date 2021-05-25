package datatouch.uikit.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import datatouch.uikit.R
import datatouch.uikit.databinding.CircleCheckBoxBinding

class CCircleCheckBox : RelativeLayout {

    private val ui = CircleCheckBoxBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var checked = false

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupBackground()
    }

    fun isChecked(): Boolean {
        return checked
    }

    fun setChecked(checked: Boolean) {
        this.checked = checked
        setupBackground()
    }

    private fun setupBackground() {
        ui.vCircleCheckBox.apply {
            val paddingStart = this.paddingStart
            val paddingTop = this.paddingTop
            val paddingEnd = this.paddingEnd
            val paddingBottom = this.paddingBottom
            this.setBackgroundResource(if (checked) R.drawable.pinpad_circle_checked_background else R.drawable.pinpad_circle_unchecked_background)
            this.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom)
        }
    }
}