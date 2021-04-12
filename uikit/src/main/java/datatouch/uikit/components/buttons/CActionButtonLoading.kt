package datatouch.uikit.components.buttons

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import datatouch.uikit.R
import datatouch.uikit.core.extensions.GenericExtensions.default
import datatouch.uikit.databinding.ActionButtonLoadingBinding

class CActionButtonLoading : FrameLayout {

    private val ui = ActionButtonLoadingBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var titleText: String = ""
    private var iconDrawable: Drawable? = null
    private var outline: Drawable? = null
    private var titleTextSize: Int = 15
    private var iconSize: Int = 20

    private var indicatorColor: Int = Color.WHITE
    private var indicatorBackgroundColor: Int = Color.WHITE

    private var indicatorStrokeWidth: Float = 1f
    private var indicatorBackgroundStrokeWidth: Float = 1f

    private var indicatorSize: Int = 30

    constructor(context: Context) : super(context) {
        setupViews()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        parseAttributes(attrs)
        setupViews()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        parseAttributes(attrs)
        setupViews()
    }

    private fun parseAttributes(attrs: AttributeSet) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CActionButtonLoading, 0, 0)
        try {
            titleText = typedArray.getString(R.styleable.CActionButtonLoading_btn_title).default("")
            titleTextSize = typedArray.getDimensionPixelSize(
                R.styleable.CActionButtonLoading_btn_title_size,
                16
            )
            iconDrawable = typedArray.getDrawable(R.styleable.CActionButtonLoading_btn_icon)
            outline = typedArray.getDrawable(R.styleable.CActionButtonLoading_btn_outline)
            iconSize =
                typedArray.getDimensionPixelSize(R.styleable.CActionButtonLoading_btn_icon_size, 20)

            indicatorColor =
                typedArray.getColor(R.styleable.CActionButtonLoading_indicator_color, Color.WHITE)
            indicatorBackgroundColor = typedArray.getColor(
                R.styleable.CActionButtonLoading_indicator_background_color,
                Color.WHITE
            )

            indicatorStrokeWidth =
                typedArray.getDimension(R.styleable.CActionButtonLoading_indicator_stroke_width, 1f)
            indicatorBackgroundStrokeWidth = typedArray.getDimension(
                R.styleable.CActionButtonLoading_indicator_background_stroke_width,
                1f
            )

            indicatorSize = typedArray.getDimensionPixelSize(
                R.styleable.CActionButtonLoading_indicator_size,
                30
            )
        } finally {
            typedArray.recycle()
        }
    }

    private fun setupViews() {
        setText(titleText)
        setIcon(iconDrawable)
        setTextSize(titleTextSize)
        setIconSize(iconSize)
        outline?.let {
            setOutline(it)
        }
        setIndicatorColor(indicatorColor)
        setIndicatorBackgroundColor(indicatorBackgroundColor)

        setIndicatorStrokeWidth(indicatorStrokeWidth)
        setIndicatorBackgroundStrokeWidth(indicatorBackgroundStrokeWidth)

        setIndicatorSize(indicatorSize)
    }

    fun setText(text: String) {
        titleText = text
        ui.tvTitle.text = titleText
    }

    fun setIcon(icon: Drawable?) {
        iconDrawable = icon
        ui.ivIcon.setImageDrawable(iconDrawable)
    }

    fun setIconSize(size: Int) {
        iconSize = size
        ui.ivIcon.updateLayoutParams {
            width = iconSize
            height = iconSize
        }
    }

    fun setTextSize(size: Int) {
        titleTextSize = size
        ui.tvTitle.textSize = titleTextSize.toFloat()
    }

    fun setOutline(outline: Drawable) {
        this.outline = outline
        ui.root.background = this.outline
    }

    fun setIndicatorColor(color: Int) {
        indicatorColor = color
        ui.progressBar.setColor(indicatorColor)
    }

    fun setIndicatorBackgroundColor(color: Int) {
        indicatorBackgroundColor = color
        ui.progressBar.setBackgroundColor(indicatorBackgroundColor)
    }

    fun setIndicatorStrokeWidth(width: Float) {
        indicatorStrokeWidth = width
        ui.progressBar.setStrokeWidth(indicatorStrokeWidth)
    }

    fun setIndicatorBackgroundStrokeWidth(width: Float) {
        indicatorBackgroundStrokeWidth = width
        ui.progressBar.setBackgroundStrokeWidth(indicatorBackgroundStrokeWidth)
    }

    fun setIndicatorSize(size: Int) {
        indicatorSize = size
        ui.progressBar.updateLayoutParams {
            width = indicatorSize
            height = indicatorSize
        }
    }

    fun showLoadingState() {
        this.showLoading(true)
    }

    fun hideLoadingState() {
        this.showLoading(false)
    }

    fun showLoading(loading: Boolean) {
        ui.llBtnContainer.isEnabled = !loading
        ui.ivIcon.isVisible = !loading
        ui.progressBar.isVisible = loading
    }

    fun isLoading(): Boolean {
        return ui.progressBar.isVisible
    }
}