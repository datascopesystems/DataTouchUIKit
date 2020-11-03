package datatouch.uikit.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.StyleableRes
import datatouch.uikit.R
import datatouch.uikit.core.utils.ResourceUtils
import kotlinx.android.synthetic.main.action_button_accent.view.*

class CActionButtonAccent : RelativeLayout {

    var defaultTextSize = 0
    private var titleText: String? = null
    private var iconDrawable: Drawable? = null
    var layoutWidth = 0
    var layoutHeight = 0
    private var enabled = true
    private var textSize = 0f

    constructor(context: Context) : super(context) {
        inflateView()
        initResources(context)
        parseAttributes(null)
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        inflateView()
        initResources(context)
        parseAttributes(attrs)
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        inflateView()
        initResources(context)
        parseAttributes(attrs)
        initViews()
    }

    private fun initResources(context: Context) {
        val res = context.resources
        defaultTextSize = res.getDimensionPixelSize(R.dimen.action_button_text_size)
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

    protected fun inflateView() {
        View.inflate(context, R.layout.action_button_accent, this)
    }

    private fun parseCustomAttributes(attrs: AttributeSet?) {
        @SuppressLint("CustomViewStyleable") val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.CActionButton, 0, 0
        )
        try {
            titleText = typedArray.getString(R.styleable.CActionButton_title)
            textSize = typedArray.getDimensionPixelSize(
                R.styleable.CActionButton_text_size,
                defaultTextSize
            ).toFloat()
            iconDrawable = typedArray.getDrawable(R.styleable.CActionButton_icon)
            enabled = typedArray.getBoolean(R.styleable.CActionButton_is_enabled, true)
        } finally {
            typedArray.recycle()
        }
    }

    fun initViews() {
        applyNativeAttributes()
        setupTitle()
        setupIcon()
        isEnabled = enabled
    }

    private fun applyNativeAttributes() {
        applyLayoutParams()
    }

    private fun applyLayoutParams() {
        buttonRootView?.layoutParams?.width =
            if (layoutWidth < 0) layoutWidth else ResourceUtils.convertDpToPixel(
                context,
                layoutWidth.toFloat()
            ).toInt()
        buttonRootView?.layoutParams?.height =
            if (layoutHeight < 0) layoutHeight else ResourceUtils.convertDpToPixel(
                context,
                layoutHeight.toFloat()
            ).toInt()
    }

    private fun setupTitle() {
        if (textSize > 0) {
            tvTitle?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        }
        tvTitle?.text = titleText.orEmpty()
    }

    private val hasIcon: Boolean
        get() = null != iconDrawable

    private fun setupIcon() {
        ivIcon?.visibility = if (hasIcon) View.VISIBLE else View.GONE
        if (hasIcon)
            ivIcon?.setImageDrawable(iconDrawable)
    }

    fun setText(text: String?) {
        titleText = text.orEmpty()
        setupTitle()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        buttonRootView?.setBackgroundResource(if (enabled) R.drawable.action_button_background_accent else R.drawable.action_button_background_accent_gray)

    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams) {
        layoutHeight = params.height
        layoutWidth = params.width
        applyLayoutParams()
        super.setLayoutParams(params)
    }
}