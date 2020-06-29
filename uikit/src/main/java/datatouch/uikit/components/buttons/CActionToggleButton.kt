package datatouch.uikit.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import datatouch.uikit.R
import datatouch.uikit.utils.Conditions
import datatouch.uikit.utils.ResourceUtils
import kotlinx.android.synthetic.main.action_toggle_button.view.*


class CActionToggleButton : RelativeLayout {
    var checkedLabelText: String? = null
    var uncheckedLabelText: String? = null

    private var activeBackground: Drawable? = null
    private var inactiveBackground: Drawable? = null
    private var checked = false
    private var callback: OnCheckChangedCallback =
        object : OnCheckChangedCallback {
            override fun onCheckChanged() {

            }
        }

    constructor(context: Context?) : super(context) {}
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

    protected fun inflateView() {
        View.inflate(context, R.layout.action_toggle_button, this)
    }

    private fun parseCustomAttributes(attrs: AttributeSet) {
        @SuppressLint("CustomViewStyleable") val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.CActionButton, 0, 0
            )
        try {
            checked = typedArray.getBoolean(R.styleable.CActionButton_checked, false)
            val checkedText =
                typedArray.getString(R.styleable.CActionButton_checkedLabel)
            checkedLabelText =
                if (Conditions.isNotNullOrEmpty(checkedText.toString())) checkedText else context.getString(
                    R.string.active
                )
            val uncheckedText =
                typedArray.getString(R.styleable.CActionButton_uncheckedLabel)
            uncheckedLabelText =
                if (Conditions.isNotNullOrEmpty(uncheckedText.toString())) uncheckedText else context.getString(
                    R.string.inactive
                )
            activeBackground = typedArray.getDrawable(R.styleable.CActionButton_active_background)
            inactiveBackground =
                typedArray.getDrawable(R.styleable.CActionButton_inactive_background)
        } finally {
            typedArray.recycle()
        }
    }

    fun initViews() {
        setChecked(checked)
        rootView?.setOnClickListener { rootView() }
        if (Conditions.isNull(activeBackground)) {
            activeBackground =
                context.resources.getDrawable(R.drawable.toggle_button_background_active)
        }
        if (Conditions.isNull(inactiveBackground)) {
            inactiveBackground =
                context.resources.getDrawable(R.drawable.toggle_button_background_inactive)
        }
    }

    fun setChecked(checked: Boolean) {
        this.checked = checked
        tvTitle?.text = if (checked) checkedLabelText else uncheckedLabelText
        tvTitle?.translationX = if (checked) ResourceUtils.convertDpToPixel(
            context,
            ACTIVE_STATE_TEXT_TRANSLATION_X_DP.toFloat()
        ) else ResourceUtils.convertDpToPixel(
            context,
            INACTIVE_STATE_TEXT_TRANSLATION_X_DP.toFloat()
        )
        rootView?.background = if (checked) activeBackground else inactiveBackground
    }

    fun rootView() {
        checked = !checked
        setChecked(checked)
        callback.onCheckChanged()
    }

    fun isChecked(): Boolean {
        return checked
    }

    fun setCallback(callback: OnCheckChangedCallback) {
        if (Conditions.isNotNull(callback)) this.callback = callback
    }

    interface OnCheckChangedCallback {
        fun onCheckChanged()
    }

    companion object {
        private const val ACTIVE_STATE_TEXT_TRANSLATION_X_DP = 10
        private const val INACTIVE_STATE_TEXT_TRANSLATION_X_DP = -10
    }
}