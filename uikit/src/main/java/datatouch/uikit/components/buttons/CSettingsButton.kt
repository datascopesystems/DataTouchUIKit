package datatouch.uikit.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import datatouch.uikit.R
import datatouch.uikit.core.extensions.TypedArrayExtensions.getAppCompatDrawable
import datatouch.uikit.core.utils.Conditions
import datatouch.uikit.databinding.SettingsButtonBinding


class CSettingsButton : FrameLayout {

    private val ui = SettingsButtonBinding
        .inflate(LayoutInflater.from(context), this, true)

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
    }

    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        parseAttributes(attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        parseAttributes(attrs)
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        parseNativeAttributes(attrs)
        parseCustomAttributes(attrs)
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

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupLayoutSize()
        setupTitles()
        setupTextSize()
        setupIcon()
        setupColors()
        setUpDescriptionVisibility(descriptionVisibility)
        setUpDrawableColor()
    }

    private fun setUpDrawableColor() {
        ui.rlRoot.background = colorBackground
        ui.tvName.setTextColor(textColor)
    }

    private fun setUpDescriptionVisibility(visible: Boolean) {
        ui.tvDescription.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun setupTitles() {
        ui.tvName.text = if (Conditions.isNotNull(titleText)) titleText else ""
        ui.tvDescription.text = if (Conditions.isNotNull(descriptionText)) descriptionText else ""
    }

    private fun setupTextSize() {
        if (titleTextSize > 0) {
            ui.tvName.textSize = titleTextSize.toFloat()
        }
        if (titleDescriptionSize > 0) {
            ui.tvDescription.textSize = titleDescriptionSize.toFloat()
        }
    }

    private fun setupIcon() {
        if (Conditions.isNull(iconDrawable)) {
            ui.ivIcon.visibility = View.GONE
        } else {
            ui.ivIcon.visibility = View.VISIBLE
            ui.ivIcon.setImageDrawable(iconDrawable)
        }
    }

    private fun setupColors() {
        ui.ivIcon.setColorFilter(color)
        ui.vArcWithGlow.setColor(color)
    }

    private fun setupLayoutSize() {
        if (layoutHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
            return
        }
        if (layoutHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
            return
        }
        val rootLayoutParams = ui.rlRoot.layoutParams
        rootLayoutParams.height = layoutHeight
        ui.rlRoot.layoutParams = rootLayoutParams
        val arcLayoutParams: ViewGroup.LayoutParams = ui.vArcWithGlow.layoutParams
        arcLayoutParams.height = layoutHeight
        ui.vArcWithGlow.layoutParams = arcLayoutParams
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
        ui.tvDescription.visibility = visibility
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