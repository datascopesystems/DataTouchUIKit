package datatouch.uikit.components.listitems

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import datatouch.uikit.R
import datatouch.uikit.utils.Conditions.isNotNull
import datatouch.uikit.utils.ResourceUtils
import kotlinx.android.synthetic.main.action_checkbox_small.view.*

class ActionCheckboxSmall : RelativeLayout {
    private var title: String? = null
    private var textColor = 0
    private var textSize = 0
    private var isChecked = false
    private var isEnabled = true

    constructor(context: Context?) : super(context) {}
    constructor(
        context: Context?,
        attrs: AttributeSet
    ) : super(context, attrs) {
        parseAttributes(attrs)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        parseAttributes(attrs)
    }

    private fun parseAttributes(attrs: AttributeSet) {
        @SuppressLint("CustomViewStyleable") val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.CSettingsButtonCheck, 0, 0
            )
        try {
            title = typedArray.getString(R.styleable.CSettingsButtonCheck_CCTitle)
            textColor = typedArray.getColor(
                R.styleable.CSettingsButtonCheck_CCIconColor,
                ContextCompat.getColor(context, R.color.white)
            )
            textSize = typedArray.getDimensionPixelSize(
                R.styleable.CSettingsButtonCheck_CCTextSize,
                ResourceUtils.convertDpToPixel(
                    context,
                    DEFAULT_TEXT_SIZE.toFloat()
                ) as Int
            )
        } finally {
            typedArray.recycle()
        }
    }

    fun afterViews() {
        rootView.setOnClickListener { rootView() }
        setTitle(if (isNotNull(title)) title else "")
        setChecked(isChecked)
        setTextSize(textSize)
        setTextColor(textColor)
    }

    fun getTextSize(): Float {
        return textSize.toFloat()
    }

    fun setTextSize(textSize: Int) {
        this.textSize = textSize
        tvName?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
    }

    fun setTextColor(textColor: Int) {
        this.textColor = textColor
        tvName?.setTextColor(textColor)
    }

    protected fun inflateView() {
        View.inflate(context, R.layout.action_checkbox_small, this)
    }

    fun setTitle(title: String?) {
        this.title = title
        tvName?.text = title
    }

    fun setTitle(@StringRes resId: Int?) {
        title = context.getString(resId!!)
        tvName?.text = title
    }

    override fun isEnabled(): Boolean {
        return isEnabled
    }

    override fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    fun rootView() {
        if (isEnabled()) {
            toggle()
            performClick()
        }
    }

    fun toggle() {
        isChecked = !isChecked
        refreshCheckBox()
    }

    private fun refreshCheckBox() {
        ivChecked?.setImageResource(if (isChecked) R.drawable.ic_checkbox_checked_small else R.drawable.ic_checkbox_unchecked_small)
    }

    fun isChecked(): Boolean {
        return isChecked
    }

    fun setChecked(checked: Boolean) {
        isChecked = checked
        refreshCheckBox()
    }

    companion object {
        private const val DEFAULT_TEXT_SIZE = 20
    }
}