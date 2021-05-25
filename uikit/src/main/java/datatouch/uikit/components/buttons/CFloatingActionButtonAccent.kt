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
import datatouch.uikit.core.extensions.TypedArrayExtensions.getAppCompatDrawable
import datatouch.uikit.core.utils.Conditions
import datatouch.uikit.databinding.FloatingActionButtonAccentBinding

class CFloatingActionButtonAccent : RelativeLayout {

    private val ui = FloatingActionButtonAccentBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var iconDrawable: Drawable? = null
    private var disabledButtonBackground: Drawable? = null
    private var enabledButtonBackground: Drawable? = null
    private var layoutWidth = 0
    private var layoutHeight = 0
    private var iconPadding = 0

    constructor(context: Context?) : super(context)

    constructor(
        context: Context?,
        attrs: AttributeSet
    ) : super(context, attrs) {
        parseAttributes(attrs)
    }


    constructor(
        context: Context?,
        attrs: AttributeSet,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        parseAttributes(attrs)
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
                typedArray.getAppCompatDrawable(
                    context,
                    R.styleable.CActionButton_active_background
                )
            disabledButtonBackground =
                typedArray.getAppCompatDrawable(
                    context,
                    R.styleable.CActionButton_inactive_background
                )
        } finally {
            typedArray.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        applyNativeAttributes()
        setupIcon()
        setupBackground()
    }

    private fun setupBackground() {
        if (Conditions.isNull(enabledButtonBackground)) {
            enabledButtonBackground = ContextCompat.getDrawable(context, R.drawable.floating_action_button_background_accent)
        }
        if (Conditions.isNull(disabledButtonBackground)) {
            disabledButtonBackground = ContextCompat.getDrawable(context, R.drawable.floating_action_button_background_accent_gray)
        }
        setBackground((if (isEnabled) enabledButtonBackground else disabledButtonBackground)!!)
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

    override fun setVisibility(visibilityState: Int) {
        ui.rlRoot.visibility = visibilityState
    }

    fun setIcon(icon: Drawable?) {
        iconDrawable = icon
        ui.ivIcon.setImageDrawable(iconDrawable)
        setupIcon()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        setBackground((if (enabled) enabledButtonBackground else disabledButtonBackground)!!)
    }
}