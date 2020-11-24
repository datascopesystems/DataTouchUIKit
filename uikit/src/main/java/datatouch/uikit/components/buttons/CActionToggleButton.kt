package datatouch.uikit.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.StyleableRes
import androidx.core.content.ContextCompat
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
    private var layoutWidth = 0
    private var layoutHeight = 0

    constructor(context: Context) : super(context) {
        inflateView()
        parseAttributes(null)
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        inflateView()
        parseAttributes(attrs)
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle ) {
        inflateView()
        parseAttributes(attrs)
        initViews()
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        parseNativeAttributes(attrs)
        parseCustomAttributes(attrs)
    }

    private fun parseNativeAttributes(attrs: AttributeSet?) {
        val attrIndexes = intArrayOf(
            android.R.attr.layout_width,
            android.R.attr.layout_height,
            android.R.attr.paddingLeft,
            android.R.attr.paddingTop,
            android.R.attr.paddingRight,
            android.R.attr.paddingBottom
        )
        val typedArray = context.obtainStyledAttributes(attrs, attrIndexes, 0, 0)
        try {
            @StyleableRes val widthIndex = 0
            @StyleableRes val heightIndex = 1
            layoutWidth = typedArray.getLayoutDimension(widthIndex, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutHeight = typedArray.getLayoutDimension(heightIndex, ViewGroup.LayoutParams.WRAP_CONTENT)
        } finally {
            typedArray.recycle()
        }
    }


    private fun inflateView() {
        View.inflate(context, R.layout.action_toggle_button, this)
    }

    private fun parseCustomAttributes(attrs: AttributeSet?) {
        @SuppressLint("CustomViewStyleable") val typedArray =
                context.obtainStyledAttributes(
                        attrs,
                        R.styleable.CActionButton, 0, 0
                )
        try {
            defaultActiveBackground = ContextCompat.getDrawable(context, R.drawable.toggle_button_background_active)
            defaultInactiveBackground = ContextCompat.getDrawable(context, R.drawable.toggle_button_background_inactive)
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
        applyNativeAttributes()
        setChecked(checked)
        rlRoot.setOnClickListener { rlRoot() }
        if (Conditions.isNull(activeBackground)) {
            activeBackground = defaultActiveBackground
        }
        if (Conditions.isNull(inactiveBackground)) {
            inactiveBackground = defaultInactiveBackground
        }
    }

    private fun applyNativeAttributes() {
        applyLayoutParams()
    }

    private fun applyLayoutParams() {
        rlRoot?.layoutParams?.width =
            if (layoutWidth < 0) layoutWidth else ResourceUtils.convertDpToPixel(
                context,
                layoutWidth.toFloat()
            ).toInt()
        rlRoot?.layoutParams?.height =
            if (layoutHeight < 0) layoutHeight else ResourceUtils.convertDpToPixel(
                context,
                layoutHeight.toFloat()
            ).toInt()
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

    fun rlRoot() {
        checked = !checked
        setChecked(checked)
        callback?.invoke()
    }

    fun isChecked() = checked

    fun setCallback(callback: UiJustCallback) {
        if (Conditions.isNotNull(callback)) this.callback = callback
    }

}