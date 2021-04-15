package datatouch.uikit.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import datatouch.uikit.R
import datatouch.uikit.core.extensions.TypedArrayExtensions.getAppCompatDrawable
import datatouch.uikit.core.utils.Conditions
import kotlinx.android.synthetic.main.settings_button.view.*


class CSettingsButton : FrameLayout {

    private var titleText: String? = null
    private var descriptionText: String? = null
    private var iconDrawable: Drawable? = null
    private var colorBackground =
        ContextCompat.getDrawable(context, R.drawable.settings_button_background)
    private var color = 0
    private var textColor = 0
    private var descriptionVisibility = false
    private var layoutWidth = 0
    private var layoutHeight = 0
    private var titleTextSize = 0
    private var titleDescriptionSize = 0
    var colorFilter: ColorFilter? = null
        set(colorFilter) {
            field = colorFilter
            colorBackground?.colorFilter = colorFilter
        }

    constructor(context: Context) : super(context) {
        inflateView()
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        inflateView()
        parseAttributes(attrs)
        initViews()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        inflateView()
        parseAttributes(attrs)
        initViews()
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        parseNativeAttributes(attrs)
        parseCustomAttributes(attrs)
    }

    protected fun inflateView() {
        View.inflate(context, R.layout.settings_button, this)
    }

    @SuppressLint("ResourceType")
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
            layoutWidth = typedArray.getLayoutDimension(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutHeight = typedArray.getLayoutDimension(1, ViewGroup.LayoutParams.WRAP_CONTENT)
        } finally {
            typedArray.recycle()
        }
    }

    private fun parseCustomAttributes(attrs: AttributeSet?) {
        @SuppressLint("CustomViewStyleable") val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.CSettingsButton, 0, 0
            )
        try {
            titleText = typedArray.getString(R.styleable.CSettingsButton_titleText)
            descriptionText = typedArray.getString(R.styleable.CSettingsButton_description_text)
            color = typedArray.getColor(
                R.styleable.CSettingsButton_icon_color,
                Color.WHITE
            )
            iconDrawable = typedArray.getAppCompatDrawable(context, R.styleable.CSettingsButton_settings_icon)
            colorBackground =
                typedArray.getAppCompatDrawable(context, R.styleable.CSettingsButton_background_drawable)
            textColor = typedArray.getColor(
                R.styleable.CSettingsButton_text_color,
                Color.WHITE
            )
            descriptionVisibility =
                typedArray.getBoolean(R.styleable.CSettingsButton_description_visibility, true)
            titleTextSize =
                typedArray.getDimensionPixelSize(R.styleable.CSettingsButton_title_text_size, 0)
            titleDescriptionSize = typedArray.getDimensionPixelSize(
                R.styleable.CSettingsButton_title_description_size,
                0
            )
        } finally {
            typedArray.recycle()
        }
    }

    fun initViews() {
        setupLayoutSize()
        setupTitles()
        setupTextSize()
        setupIcon()
        setupColors()
        setUpDescriptionVisibility(descriptionVisibility)
        setUpDrawableColor()
    }

    private fun setUpDrawableColor() {
        rlRoot?.background = colorBackground
        tvName?.setTextColor(textColor)
    }

    private fun setUpDescriptionVisibility(visible: Boolean) {
        tvDescription?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun setupTitles() {
        tvName?.text = if (Conditions.isNotNull(titleText)) titleText else ""
        tvDescription?.text = if (Conditions.isNotNull(descriptionText)) descriptionText else ""
    }

    private fun setupTextSize() {
        if (titleTextSize > 0) {
            tvName?.textSize = titleTextSize.toFloat()
        }
        if (titleDescriptionSize > 0) {
            tvDescription?.textSize = titleDescriptionSize.toFloat()
        }
    }

    private fun setupIcon() {
        if (Conditions.isNull(iconDrawable)) {
            ivIcon?.visibility = View.GONE
        } else {
            ivIcon?.visibility = View.VISIBLE
            ivIcon?.setImageDrawable(iconDrawable)
        }
    }

    private fun setupColors() {
        ivIcon?.setColorFilter(color)
        vArcWithGlow?.setColor(color)
    }

    private fun setupLayoutSize() {
        if (layoutHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
            return
        }
        if (layoutHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
            return
        }
        val rootLayoutParams = rlRoot!!.layoutParams
        rootLayoutParams.height = layoutHeight
        rlRoot?.layoutParams = rootLayoutParams
        val arcLayoutParams: ViewGroup.LayoutParams = vArcWithGlow.getLayoutParams()
        arcLayoutParams.height = layoutHeight
        vArcWithGlow.layoutParams = arcLayoutParams
    }

    fun setText(text: String?) {
        titleText = text
        setupTitles()
    }

    fun setDescriptionText(text: String?) {
        descriptionText = text
        setupTitles()
    }

    fun setDescriptionVisibility(visibility: Int) {
        tvDescription?.visibility = visibility
    }

    fun setSettingsIcon(iconDrawable: Drawable?) {
        this.iconDrawable = iconDrawable
        setupIcon()
    }

    fun setIconColor(color: Int) {
        this.color = color
        setupColors()
    }

    fun setTitleTextSize(titleTextSize: Int) {
        this.titleTextSize = titleTextSize
        setupTextSize()
    }

    fun setTitleDescriptionSize(titleDescriptionSize: Int) {
        this.titleDescriptionSize = titleDescriptionSize
        setupTextSize()
    }

}