package datatouch.uikit.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import datatouch.uikit.R
import datatouch.uikit.core.extensions.TypedArrayExtensions.getAppCompatDrawable
import datatouch.uikit.core.utils.Conditions
import kotlinx.android.synthetic.main.floating_action_button_accent.view.*

class CFloatingActionButtonAccent : RelativeLayout {

    private var iconDrawable: Drawable? = null
    private var disabledButtonBackground: Drawable? = null
    private var enabledButtonBackground: Drawable? = null
    private var layoutWidth = 0
    private var layoutHeight = 0
    private var iconPadding = 0

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

    protected fun inflateView() {
        View.inflate(context, R.layout.floating_action_button_accent, this)
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
        } finally {
            typedArray.recycle()
        }
    }

    fun initViews() {
        applyNativeAttributes()
        setupIcon()
        setupBackground()
    }

    private fun setupBackground() {
        if (Conditions.isNull(enabledButtonBackground)) {
            enabledButtonBackground =
                resources.getDrawable(R.drawable.floating_action_button_background_accent)
        }
        if (Conditions.isNull(disabledButtonBackground)) {
            disabledButtonBackground =
                resources.getDrawable(R.drawable.floating_action_button_background_accent_gray)
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
            if (LayoutParams.WRAP_CONTENT == layoutWidth) rlRoot!!.layoutParams.width =
                defaultButtonSizePx
        } else rlRoot!!.layoutParams.width = layoutWidth
        if (layoutHeight < 0) {
            if (LayoutParams.WRAP_CONTENT == layoutHeight) rlRoot!!.layoutParams.height =
                defaultButtonSizePx
        } else rlRoot!!.layoutParams.height = layoutHeight
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
            ivIcon?.visibility = View.GONE
        } else {
            ivIcon?.visibility = View.VISIBLE
            ivIcon?.setImageDrawable(iconDrawable)
            ivIcon?.setPadding(iconPadding, iconPadding, iconPadding, iconPadding)
        }
    }

    override fun setBackground(drawable: Drawable) {
        if (Conditions.isNull(drawable)) return
        val paddingStart = rlRoot!!.paddingStart
        val paddingTop = rlRoot!!.paddingTop
        val paddingEnd = rlRoot!!.paddingEnd
        val paddingBottom = rlRoot!!.paddingBottom
        rlRoot?.background = drawable
        rlRoot?.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom)
    }

    override fun setVisibility(visibilityState: Int) {
        rlRoot?.visibility = visibilityState
    }

    fun setIcon(icon: Drawable?) {
        iconDrawable = icon
        ivIcon?.setImageDrawable(iconDrawable)
        setupIcon()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        setBackground((if (enabled) enabledButtonBackground else disabledButtonBackground)!!)
    }
}