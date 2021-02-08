package datatouch.uikit.components.textview


import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import datatouch.uikit.R
import datatouch.uikit.core.extensions.ConditionsExtensions.isNotNull
import datatouch.uikit.core.utils.views.ViewUtils
import datatouch.uikit.databinding.TextViewBinding

class TextView : LinearLayout {

    private val ui = TextViewBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var layoutWidth = 0
    private var layoutHeight = 0

    private var startIconDrawable: Drawable? = null
    private var endIconDrawable: Drawable? = null

    private var startIconColor = InvalidColor
    private var endIconColor = InvalidColor

    private var startIconSizePx = 0
    private var endIconSizePx = 0

    private var startIconMarginPx = 0
    private var endIconMarginPx = 0

    private var font = Font.Regular
    private var fontColor = Color.WHITE
    private var fontSizePx = 0
    private var textGravity = Gravity.NO_GRAVITY

    private var maxLines = DefaultMaxLines

    var text = ""
        set(value) {
            field = value
            ui.tvTextView.text = value
        }

    private var typeface: Typeface? = null

    constructor(context: Context?) : super(context) { init() }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { init(attrs) }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr) { init(attrs) }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) { init(attrs) }

    private fun init(attrs: AttributeSet? = null) {
        parseAttributes(attrs)
        initViews()
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        val parsedNativeAttributes = ViewUtils.parseNativeAttributes(context, attrs)
        layoutHeight = parsedNativeAttributes.layoutHeight
        layoutWidth = parsedNativeAttributes.layoutWidth
        parseCustomAttributes(attrs)
    }

    private fun parseCustomAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.TextView, 0, 0)

        try {
            startIconDrawable = typedArray.getDrawable(R.styleable.TextView_tv_start_icon)

            endIconDrawable = typedArray.getDrawable(R.styleable.TextView_tv_end_icon)

            startIconColor =
                typedArray.getColor(R.styleable.TextView_tv_start_icon_tint, InvalidColor)

            endIconColor = typedArray.getColor(R.styleable.TextView_tv_end_icon_tint, InvalidColor)

            font =
                Font.fromValue(typedArray.getInt(R.styleable.TextView_tv_font, Font.Regular.value))

            fontColor = typedArray.getColor(R.styleable.TextView_tv_font_color, Color.WHITE)

            startIconSizePx = typedArray.getDimensionPixelSize(
                R.styleable.TextView_tv_start_icon_size,
                ViewUtils.convertDp2Px(context, DefaultIconSizeDp))

            endIconSizePx = typedArray.getDimensionPixelSize(
                R.styleable.TextView_tv_end_icon_size,
                ViewUtils.convertDp2Px(context, DefaultIconSizeDp))

            startIconMarginPx = typedArray.getDimensionPixelSize(
                R.styleable.TextView_tv_start_icon_margin,
                ViewUtils.convertDp2Px(context, DefaultIconMarginDp))

            endIconMarginPx = typedArray.getDimensionPixelSize(
                R.styleable.TextView_tv_end_icon_margin,
                ViewUtils.convertDp2Px(context, DefaultIconMarginDp))

            text = typedArray.getString(R.styleable.TextView_tv_text).orEmpty()

            textGravity =
                typedArray.getInt(R.styleable.TextView_tv_text_gravity, Gravity.NO_GRAVITY)

            fontSizePx = typedArray.getDimensionPixelSize(
                R.styleable.TextView_tv_font_size,
                ViewUtils.spToPx(context, DefaultTextSizeSp))

            maxLines = typedArray.getInt(R.styleable.TextView_tv_max_lines, DefaultMaxLines)

        } finally {
            typedArray.recycle()
        }
    }

    private fun initViews() {
        typeface = ui.tvTextView.typeface

        setupIconDrawable(ui.ivStartIcon, startIconDrawable)
        setupIconDrawable(ui.ivEndIcon, endIconDrawable)

        setupIconColor(ui.ivStartIcon, startIconColor)
        setupIconColor(ui.ivEndIcon, endIconColor)

        setupTypeface()
        setupFontColor()
        setupTextGravity()
        setupTextSize()

        setupIconSize(ui.ivStartIcon, startIconSizePx)
        setupIconSize(ui.ivEndIcon, endIconSizePx)

        setupIconMargin(ui.ivStartIcon, startIconMarginPx)
        setupIconMargin(ui.ivEndIcon, endIconMarginPx)
    }

    private fun setupIconDrawable(iv: ImageView, iconDrawable: Drawable?) {
        iv.isVisible = iconDrawable.isNotNull()
        iv.setImageDrawable(iconDrawable)
    }

    private fun setupIconColor(iv : ImageView, iconColor: Int) {
        if (iconColor != InvalidColor)
            iv.setColorFilter(iconColor)
    }

    private fun setupTypeface() {
        val typefaceStyle = when (font) {
            Font.Regular -> Typeface.NORMAL
            Font.Bold -> Typeface.BOLD
            Font.Italic -> Typeface.ITALIC
        }

        ui.tvTextView.setTypeface(typeface, typefaceStyle)
    }

    private fun setupFontColor() = ui.tvTextView.setTextColor(fontColor)


    private fun setupTextGravity() { ui.tvTextView.gravity = textGravity }

    private fun setupTextSize() {
        ui.tvTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSizePx.toFloat())
    }

    private fun setupIconSize(iv: ImageView, sizePx: Int) {
        iv.layoutParams.height = sizePx
        iv.layoutParams.width = sizePx
    }

    private fun setupIconMargin(iv: ImageView, marginPx: Int) {
        val lp = iv.layoutParams as RelativeLayout.LayoutParams
        lp.marginEnd = marginPx
    }

    fun setStartIconColor(color: Int) {
        startIconColor = color
        setupIconColor(ui.ivStartIcon, startIconColor)
    }

    fun setEndIconColor(color: Int) {
        endIconColor = color
        setupIconColor(ui.ivStartIcon, endIconColor)
    }

    fun setStartIconColorRes(@ColorRes resId: Int) =
        setStartIconColor(ContextCompat.getColor(context, resId))

    fun setEndIconColorRes(@ColorRes resId: Int) =
        setEndIconColor(ContextCompat.getColor(context, resId))

    fun setFontColor(color: Int) {
        fontColor = color
        setupFontColor()
    }

    fun setFontColorRes(@ColorRes resId: Int) =
        setFontColor(ContextCompat.getColor(context, resId))

}

private const val InvalidColor = -666
private const val DefaultTextSizeSp = 16f
private const val DefaultMaxLines = 1
private const val DefaultIconSizeDp = 15f
private const val DefaultIconMarginDp = 5f