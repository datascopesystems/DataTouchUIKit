package datatouch.uikit.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import datatouch.uikit.R
import datatouch.uikit.core.callbacks.UiJustCallback
import datatouch.uikit.core.utils.Conditions
import datatouch.uikit.core.utils.ResourceUtils
import kotlinx.android.synthetic.main.action_toggle_button.view.*

private const val TitleMarginDp = 20f

class CActionToggleButton : RelativeLayout {
    var checkedLabelText: String? = null
    var uncheckedLabelText: String? = null

    private var activeBackground: Drawable? = null
    private var inactiveBackground: Drawable? = null
    private var checked = false
    private var callback: UiJustCallback? = null
    var defaultActiveBackground: Drawable? = null
    var defaultInactiveBackground: Drawable? = null

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
            defaultActiveBackground = context.resources.getDrawable(R.drawable.toggle_button_background_active)
            defaultInactiveBackground = context.resources.getDrawable(R.drawable.toggle_button_background_active)
            checked = typedArray.getBoolean(R.styleable.CActionButton_checked, false)
            val checkedText = typedArray.getString(R.styleable.CActionButton_checkedLabel)
            checkedLabelText = if (Conditions.isNotNullOrEmpty(checkedText.orEmpty())) checkedText else context.getString(R.string.active)
            val uncheckedText = typedArray.getString(R.styleable.CActionButton_uncheckedLabel)
            uncheckedLabelText = if (Conditions.isNotNullOrEmpty(uncheckedText.orEmpty())) uncheckedText else context.getString(R.string.inactive)
            activeBackground = typedArray.getDrawable(R.styleable.CActionButton_active_background)
            inactiveBackground = typedArray.getDrawable(R.styleable.CActionButton_inactive_background)

        } finally {
            typedArray.recycle()
        }
    }

    fun initViews() {
        setChecked(checked)
        if (Conditions.isNull(activeBackground)) {
            activeBackground = defaultActiveBackground
        }
        if (Conditions.isNull(inactiveBackground)) {
            inactiveBackground = defaultInactiveBackground
        }
    }

    fun setChecked(checked: Boolean) {
        this.checked = checked
        tvTitle?.text = if (checked) checkedLabelText else uncheckedLabelText
        rlRoot?.background = if (checked) activeBackground else inactiveBackground
        setupTitleMargins()
    }

    private fun setupTitleMargins() {
        val lp = tvTitle?.layoutParams as LayoutParams
        val marginPx = ResourceUtils.convertDpToPixel(context, TitleMarginDp).toInt()
        if (checked)
            lp.setMargins(marginPx, 0, 0, 0)
        else
            lp.setMargins(0, 0, marginPx, 0)
    }

    fun rootView() {
        checked = !checked
        setChecked(checked)
        callback?.invoke()
    }

    fun isChecked(): Boolean {
        return checked
    }

    fun setCallback(callback: UiJustCallback) {
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