package datatouch.uikit.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import datatouch.uikit.R
import kotlinx.android.synthetic.main.circle_check_box.view.*


class CCircleCheckBox : RelativeLayout {

    private var checked = false

    constructor(context: Context?) : super(context) {
        inflateView()
        afterViews()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        inflateView()
        afterViews()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        inflateView()
        afterViews()
    }

    fun afterViews() {
        setupBackground()
    }

    fun isChecked(): Boolean {
        return checked
    }

    fun setChecked(checked: Boolean) {
        this.checked = checked
        setupBackground()
    }

    protected fun inflateView() {
        View.inflate(context, R.layout.circle_check_box, this)
    }

    private fun setupBackground() {
        vCircleCheckBox?.apply {
            val paddingStart = this.paddingStart
            val paddingTop = this.paddingTop
            val paddingEnd = this.paddingEnd
            val paddingBottom = this.paddingBottom
            this.setBackgroundResource(if (checked) R.drawable.pinpad_circle_checked_background else R.drawable.pinpad_circle_unchecked_background)
            this.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom)
        }
    }
}