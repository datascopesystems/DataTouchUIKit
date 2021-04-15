package datatouch.uikit.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import datatouch.uikit.R
import datatouch.uikit.core.extensions.ConditionsExtensions.isNull
import datatouch.uikit.core.extensions.TypedArrayExtensions.getAppCompatDrawable
import datatouch.uikit.core.utils.Conditions
import datatouch.uikit.databinding.ImageButtonAccentOutlineBinding

class ImageButtonAccentOutline : RelativeLayout {

    private val ui = ImageButtonAccentOutlineBinding
        .inflate(LayoutInflater.from(context), this, true)

    private val defaultPositiveEnabledBackground by lazy {
        ContextCompat.getDrawable(
            context,
            R.drawable.image_button_background_accent_outline
        )
    }
    private val defaultNegativeEnabledBackground by lazy {
        ContextCompat.getDrawable(
            context,
            R.drawable.image_button_background_accent_negative_outline
        )
    }
    private val defaultDisabledBackground by lazy {
        ContextCompat.getDrawable(
            context,
            R.drawable.image_button_background_accent_negative_outline
        )
    }

    private var buttonType = ButtonType.Positive
    private var iconDrawable: Drawable? = null
    private var disabledButtonBackground: Drawable? = null
    private var enabledButtonBackground: Drawable? = null
    private var layoutWidth = 0
    private var layoutHeight = 0
    private var iconPadding = 0
    private var iconTintColor = InvalidColor

    constructor(context: Context?) : super(context) {
        initViews()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet
    ) : super(context, attrs) {
        parseAttributes(attrs)
        initViews()
    }


    constructor(
        context: Context?,
        attrs: AttributeSet,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        parseAttributes(attrs)
        initViews()
    }

    private fun parseAttributes(attrs: AttributeSet) {
        parseNativeAttributes(attrs)
        parseCustomAttributes(attrs)
    }

    @SuppressLint("ResourceType")
    private fun parseNativeAttributes(attrs: AttributeSet) {
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
            layoutWidth = typedArray.getLayoutDimension(
                0,
                resources.getDimensionPixelSize(R.dimen.floating_circle_button_size)
            )
            layoutHeight = typedArray.getLayoutDimension(
                1,
                resources.getDimensionPixelSize(R.dimen.floating_circle_button_size)
            )
        } finally {
            typedArray.recycle()
        }
    }

    private fun parseCustomAttributes(attrs: AttributeSet) {
        @SuppressLint("CustomViewStyleable") val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.CActionButton, 0, 0
            )
        try {
            iconDrawable = typedArray.getAppCompatDrawable(context, R.styleable.CActionButton_icon)

            iconPadding =
                typedArray.getDimensionPixelSize(R.styleable.CActionButton_icon_padding, 0)

            enabledButtonBackground =
                typedArray.getAppCompatDrawable(context, R.styleable.CActionButton_active_background)

            disabledButtonBackground =
                typedArray.getAppCompatDrawable(context, R.styleable.CActionButton_inactive_background)

            buttonType =
                ButtonType.fromInt(typedArray.getInt(R.styleable.CActionButton_button_type, 0))

            iconTintColor = typedArray.getColor(R.styleable.CActionButton_tint_color, InvalidColor)

        } finally {
            typedArray.recycle()
        }
    }

    fun initViews() {
        applyNativeAttributes()
        setupIcon()
        setupBackground()
        setupIconTintColor()
    }

    private fun setupBackground() {
        if (enabledButtonBackground.isNull())
            enabledButtonBackground = backgroundByButtonType

        if (disabledButtonBackground.isNull())
            disabledButtonBackground = defaultDisabledBackground

        background = (if (isEnabled) enabledButtonBackground else disabledButtonBackground)!!
    }

    private val backgroundByButtonType
        get() = when (buttonType) {
                ButtonType.Positive -> defaultPositiveEnabledBackground
                ButtonType.Negative -> defaultNegativeEnabledBackground
            }

    private fun applyNativeAttributes() {
        applyLayoutParams()
    }

    private fun applyLayoutParams() {
        val defaultButtonSizePx =
            resources.getDimensionPixelSize(R.dimen.floating_circle_button_size)
        if (layoutWidth < 0) {
            if (LayoutParams.WRAP_CONTENT == layoutWidth) ui.rlRoot.layoutParams.width =
                defaultButtonSizePx
        } else ui.rlRoot.layoutParams.width = layoutWidth
        if (layoutHeight < 0) {
            if (LayoutParams.WRAP_CONTENT == layoutHeight) ui.rlRoot.layoutParams.height =
                defaultButtonSizePx
        } else ui.rlRoot.layoutParams.height = layoutHeight
    }

    fun setLayoutWidth(layoutWidth: Int) {
        this.layoutWidth = layoutWidth
        applyLayoutParams()
    }

    fun setLayoutHeight(layoutHeight: Int) {
        this.layoutHeight = layoutHeight
        applyLayoutParams()
    }

    private fun setupIcon() {
        if (Conditions.isNull(iconDrawable)) {
            ui.ivIcon.visibility = View.GONE
        } else {
            ui.ivIcon.visibility = View.VISIBLE
            ui.ivIcon.setImageDrawable(iconDrawable)
            ui.ivIcon.setPadding(iconPadding, iconPadding, iconPadding, iconPadding)
        }
    }

    override fun setBackground(drawable: Drawable) {
        if (Conditions.isNull(drawable)) return
        val paddingStart = ui.rlRoot.paddingStart
        val paddingTop = ui.rlRoot.paddingTop
        val paddingEnd = ui.rlRoot.paddingEnd
        val paddingBottom = ui.rlRoot.paddingBottom
        ui.rlRoot.background = drawable
        ui.rlRoot.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom)
    }

    fun setIcon(icon: Drawable?) {
        iconDrawable = icon
        ui.ivIcon.setImageDrawable(iconDrawable)
        setupIcon()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        background = (if (enabled) enabledButtonBackground else disabledButtonBackground)!!
    }

    private fun setupIconTintColor() {
        if (iconTintColor == InvalidColor) return

        ui.ivIcon.setColorFilter(iconTintColor)
    }

    fun setButtonType(buttonType: ButtonType) {
        this.buttonType = buttonType
        enabledButtonBackground = null
        disabledButtonBackground = null
        setupBackground()
    }

}

private const val InvalidColor = -666