package datatouch.uikit.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.StyleableRes
import androidx.core.content.ContextCompat
import datatouch.uikit.R
import datatouch.uikit.core.callbacks.UiJustCallback
import datatouch.uikit.core.extensions.TypedArrayExtensions.getAppCompatDrawable
import datatouch.uikit.databinding.ToggleButtonBinding

class ToggleButton : LinearLayout {

    private val ui = ToggleButtonBinding
        .inflate(LayoutInflater.from(context), this, true)

    var isChecked = false
        private set

    var callback: UiJustCallback? = null

    private var checkedLabelText: String? = null
    private var uncheckedLabelText: String? = null

    private var checkedBackground: Drawable? = null
    private var uncheckedBackground: Drawable? = null

    private var checkedIcon: Drawable? = null
    private var uncheckedIcon: Drawable? = null

    private var layoutWidth = 0
    private var layoutHeight = 0

    private var edgeMarginPx = 0
    private var edgeDoubleMarginPx = 0

    private var iconSizeDp = 0f
    private var textSizePx = 0f

    private var glowButton = false
    private var glowColorChecked = 0
    private var glowColorUnchecked = 0
    private var glowRadiusPx = 0

    private var defaultCheckedLabelText: String? = null
    private var defaultUncheckedLabelText: String? = null

    private var defaultCheckedBackground: Drawable? = null
    private var defaultUncheckedBackground: Drawable? = null

    private var defaultCheckedIcon: Drawable? = null
    private var defaultUncheckedIcon: Drawable? = null

    private var defaultIconSize = 0f
    private var defaultTextSize = 0f

    private var defaultGlowColorChecked = 0
    private var defaultGlowColorUnchecked = 0
    private var defaultGlowRadiusPx = 0f

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        initResources()
        parseAttributes(attrs)
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
            layoutWidth =
                typedArray.getLayoutDimension(widthIndex, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutHeight =
                typedArray.getLayoutDimension(heightIndex, ViewGroup.LayoutParams.WRAP_CONTENT)
        } finally {
            typedArray.recycle()
        }
    }

    private fun initResources() {
        defaultCheckedBackground =
            ContextCompat.getDrawable(context, R.drawable.toggle_button_background_checked)
        defaultUncheckedBackground =
            ContextCompat.getDrawable(context, R.drawable.toggle_button_background_unchecked)

        defaultCheckedIcon =
            ContextCompat.getDrawable(context, R.drawable.ic_check_white)
        defaultUncheckedIcon =
            ContextCompat.getDrawable(context, R.drawable.ic_clear_white)

        defaultCheckedLabelText = context.getString(R.string.active)
        defaultUncheckedLabelText = context.getString(R.string.inactive)

        edgeMarginPx = context.resources.getDimensionPixelSize(R.dimen.gtb_edge_margin)
        edgeDoubleMarginPx = context.resources.getDimensionPixelSize(R.dimen.gtb_edge_double_margin)

        defaultTextSize =
            context.resources.getDimensionPixelSize(R.dimen.gtb_default_icon_size).toFloat()
        defaultTextSize =
            context.resources.getDimensionPixelSize(R.dimen.gtb_default_text_size).toFloat()

        defaultGlowColorChecked = ContextCompat.getColor(context, R.color.accent_end)
        defaultGlowColorUnchecked = ContextCompat.getColor(context, R.color.accent_start)
        defaultGlowRadiusPx =
            context.resources.getDimensionPixelSize(R.dimen.gtb_default_glow_radius).toFloat()
    }

    private fun parseCustomAttributes(attrs: AttributeSet?) {
        @SuppressLint("CustomViewStyleable") val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.ToggleButton, 0, 0
            )
        try {
            isChecked = typedArray.getBoolean(R.styleable.ToggleButton_gtb_checked, false)

            val checkedText = typedArray.getString(R.styleable.ToggleButton_gtb_checked_text)
            checkedLabelText =
                if (!checkedText.isNullOrEmpty()) checkedText else defaultCheckedLabelText

            val uncheckedText =
                typedArray.getString(R.styleable.ToggleButton_gtb_unchecked_text)
            uncheckedLabelText =
                if (!uncheckedText.isNullOrEmpty()) uncheckedText else defaultUncheckedLabelText

            checkedBackground =
                typedArray.getAppCompatDrawable(context, R.styleable.ToggleButton_gtb_checked_background)
                    ?: defaultCheckedBackground
            uncheckedBackground =
                typedArray.getAppCompatDrawable(context, R.styleable.ToggleButton_gtb_unchecked_background)
                    ?: defaultUncheckedBackground

            checkedIcon = typedArray.getAppCompatDrawable(context, R.styleable.ToggleButton_gtb_icon_checked)
                ?: defaultCheckedIcon
            uncheckedIcon = typedArray.getAppCompatDrawable(context, R.styleable.ToggleButton_gtb_icon_unchecked)
                ?: defaultUncheckedIcon

            iconSizeDp =
                typedArray.getDimension(R.styleable.ToggleButton_gtb_icon_size, defaultIconSize)
            textSizePx =
                typedArray.getDimension(R.styleable.ToggleButton_gtb_text_size, defaultTextSize)

            glowButton = typedArray.getBoolean(R.styleable.ToggleButton_gtb_glow_button, false)
            glowColorChecked = typedArray.getColor(
                R.styleable.ToggleButton_gtb_glow_color_checked,
                defaultGlowColorChecked
            )
            glowColorUnchecked = typedArray.getColor(
                R.styleable.ToggleButton_gtb_glow_color_unchecked,
                defaultGlowColorUnchecked
            )
            glowRadiusPx = typedArray.getDimensionPixelSize(
                R.styleable.ToggleButton_gtb_glow_button_radius,
                defaultGlowRadiusPx.toInt()
            )

        } finally {
            typedArray.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        applyNativeAttributes()
        setSizes()
        setChecked(isChecked)
        ui.llRoot.setOnClickListener { rlRoot() }
    }

    private fun applyNativeAttributes() {
        applyLayoutParams()
    }

    private fun applyLayoutParams() {
        ui.llRoot.layoutParams?.width = layoutWidth
        ui.llRoot.layoutParams?.height = layoutHeight
    }

    private fun setSizes() {
        ui.ivIcon.layoutParams?.width = iconSizeDp.toInt()
        ui.ivIcon.layoutParams?.height = iconSizeDp.toInt()
        ui.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx)
    }

    fun setChecked(checked: Boolean) {
        this.isChecked = checked
        ui.tvTitle.text = if (checked) checkedLabelText else uncheckedLabelText
        ui.llRoot.background = if (checked) checkedBackground else uncheckedBackground
        ui.ivIcon.setImageDrawable(if (checked) checkedIcon else uncheckedIcon)
        setupGlow()
        setupTitleMargins()
    }

    private fun setupGlow() {
        if (glowButton) {
            ui.buttonGlow.setColor(if (isChecked) defaultGlowColorChecked else defaultGlowColorUnchecked)
            ui.buttonGlow.setRadius(glowRadiusPx)
        } else {
            ui.buttonGlow.visibility = GONE
            val layoutParams = ui.llRoot.layoutParams as RelativeLayout.LayoutParams
            layoutParams.setMargins(0,0,0,0)
            ui.llRoot.layoutParams = layoutParams
        }
    }

    private fun setupTitleMargins() {
        val titleLp = ui.tvTitle.layoutParams as LayoutParams
        val iconLp = ui.ivIcon.layoutParams as LayoutParams
        if (isChecked) {
            iconLp.setMargins(edgeDoubleMarginPx, 0, 0, 0)
            titleLp.setMargins(0, 0, edgeMarginPx, 0)
        } else {
            iconLp.setMargins(edgeMarginPx, 0, 0, 0)
            titleLp.setMargins(0, 0, edgeDoubleMarginPx, 0)
        }
    }

    fun rlRoot() {
        isChecked = !isChecked
        setChecked(isChecked)
        callback?.invoke()
    }
}