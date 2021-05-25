package datatouch.uikit.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.StyleableRes
import datatouch.uikit.R
import datatouch.uikit.core.extensions.TypedArrayExtensions.getAppCompatDrawable
import datatouch.uikit.core.utils.Conditions.isNotNull
import datatouch.uikit.core.utils.Conditions.isNotNullOrEmpty
import datatouch.uikit.core.utils.Conditions.isNull
import datatouch.uikit.core.utils.ResourceUtils
import datatouch.uikit.databinding.ActionButtonBinding

class CActionButton : RelativeLayout {
    private var titleText: String? = null
    private var iconDrawable: Drawable? = null
    private var backgroundDrawableImg: Drawable? = null
    private var layoutWidth = 0
    private var layoutHeight = 0
    private var textColor = 0
    private var iconColor = 0
    private var maxLines = 0

    private val ui = ActionButtonBinding
        .inflate(LayoutInflater.from(context), this, true)

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

    private fun parseNativeAttributes(attrs: AttributeSet) {
        val attrIndexes = intArrayOf(
            android.R.attr.layout_width,
            android.R.attr.layout_height,
            android.R.attr.paddingLeft,
            android.R.attr.paddingTop,
            android.R.attr.paddingRight,
            android.R.attr.paddingBottom
        )
        @StyleableRes val widthIndex = 0
        @StyleableRes val heightIndex = 1
        val typedArray = context.obtainStyledAttributes(attrs, attrIndexes, 0, 0)
        try {
            layoutWidth = typedArray.getLayoutDimension(widthIndex, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutHeight = typedArray.getLayoutDimension(heightIndex, ViewGroup.LayoutParams.WRAP_CONTENT)
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
            titleText = typedArray.getString(R.styleable.CActionButton_title)
            textColor = typedArray.getColor(
                R.styleable.CActionButton_title_color,
                Color.WHITE
            )
            iconColor = typedArray.getColor(
                R.styleable.CActionButton_icon_colour,
                Color.WHITE
            )
            iconDrawable = typedArray.getAppCompatDrawable(context, R.styleable.CActionButton_icon)
            backgroundDrawableImg =
                typedArray.getAppCompatDrawable(context, R.styleable.CActionButton_background)
            maxLines = typedArray.getInt(R.styleable.CActionButton_maxLines, 1)
        } finally {
            typedArray.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        applyNativeAttributes()
        setupTitle()
        setupIcon()
        setupBackground()
    }

    private fun applyNativeAttributes() {
        applyLayoutParams()
    }

    private fun applyLayoutParams() {
        rootView?.layoutParams?.width =
            if (layoutWidth < 0) layoutWidth else ResourceUtils.convertDpToPixel(
                context,
                layoutWidth.toFloat()
            ).toInt()
        rootView?.layoutParams?.height =
            if (layoutHeight < 0) layoutHeight else ResourceUtils.convertDpToPixel(
                context,
                layoutHeight.toFloat()
            ).toInt()
    }

    private fun setupTitle() = ui.apply {
        if (isNotNullOrEmpty(titleText!!)) {
            tvTitle.visibility = View.VISIBLE
            vDivider.visibility =
                if (ivIcon.visibility != View.GONE) View.VISIBLE else View.GONE
            tvTitle.text = if (isNotNull(titleText)) titleText else ""
            tvTitle.setTextColor(textColor)
            tvTitle.maxLines = maxLines
        } else {
            tvTitle.visibility = View.GONE
            vDivider.visibility = View.GONE
        }
    }

    private fun setupIcon() = ui.apply {
        if (isNull(iconDrawable)) {
            ivIcon.visibility = View.GONE
            vDivider.visibility = View.GONE
        } else {
            ivIcon.visibility = View.VISIBLE
            vDivider.visibility =
                if (tvTitle.visibility != View.GONE) View.VISIBLE else View.GONE
            ivIcon.setImageDrawable(iconDrawable)
            setIconColor(iconColor)
        }
    }

    private fun setupBackground() {
        if (isNull(backgroundDrawableImg)) return
        rootView.apply {
            val paddingStart = this.paddingStart
            val paddingTop = this.paddingTop
            val paddingEnd = this.paddingEnd
            val paddingBottom = this.paddingBottom
            this.background = backgroundDrawableImg
            this.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom)
        }
    }

    fun setText(text: String?) {
        titleText = text
        setupTitle()
    }

    fun setIcon(icon: Drawable?) {
        iconDrawable = icon
        ui.ivIcon.setImageDrawable(iconDrawable)
        setupIcon()
    }

    fun setIconColor(color: Int) {
        ui.ivIcon.setColorFilter(color)
    }

    fun setBackgroundColour(backgroundDrawable: Drawable?) {
        rootView?.background = backgroundDrawable
    }
}