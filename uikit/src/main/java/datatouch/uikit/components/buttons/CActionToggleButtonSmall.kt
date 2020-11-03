package datatouch.uikit.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import datatouch.uikit.R
import datatouch.uikit.core.utils.Conditions

class CActionToggleButtonSmall : RelativeLayout {

    private var checked = false
    private var callback: OnCheckChangedCallback =
        object : OnCheckChangedCallback {
            override fun onCheckChanged() {

            }
        }

    constructor(context: Context?) : super(context) {
        inflateView()
        initViews()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet
    ) : super(context, attrs) {
        inflateView()
        parseAttributes(attrs)
        initViews()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        inflateView()
        parseAttributes(attrs)
        initViews()
    }

    private fun parseAttributes(attrs: AttributeSet) {
        parseCustomAttributes(attrs)
    }

    private fun parseCustomAttributes(attrs: AttributeSet) {
        @SuppressLint("CustomViewStyleable") val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.CActionButton, 0, 0
            )
        checked = try {
            typedArray.getBoolean(R.styleable.CActionButton_checked, false)
        } finally {
            typedArray.recycle()
        }
    }

    fun initViews() {
        rootView.setOnClickListener { rootView() }
        setChecked(checked)
    }

    protected fun inflateView() {
        View.inflate(context, R.layout.action_toggle_button_small, this)
    }

    fun setChecked(checked: Boolean) {
        this.checked = checked
        rootView?.setBackgroundResource(if (checked) R.drawable.toggle_button_background_active else R.drawable.toggle_button_background_inactive)
    }

    fun rootView() {
        checked = !checked
        setChecked(checked)
        callback.onCheckChanged()
    }

    fun setCallback(callback: OnCheckChangedCallback) {
        if (Conditions.isNotNull(callback)) this.callback = callback
    }

    fun isChecked(): Boolean {
        return checked
    }

    interface OnCheckChangedCallback {
        fun onCheckChanged()
    }
}