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
import datatouch.uikit.core.utils.Conditions
import datatouch.uikit.core.utils.ResourceUtils
import datatouch.uikit.databinding.LabeledActionButtonBinding


class CLabeledActionButton : RelativeLayout {

    private val ui = LabeledActionButtonBinding
        .inflate(LayoutInflater.from(context), this, true)

    var titleText: String? = null
        private set
    private var labelText: String? = null
    private var iconDrawable: Drawable? = null
    private var backgroundDrawableImg: Drawable? = null
    private var layoutWidth = 0
    private var layoutHeight = 0
    private var textColor = 0

    constructor(context: Context?) : super(context) {
    }

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

    fun setSingleLine() {
        ui.tvTitle.maxLines = 1
        ui.tvLabel.maxLines = 1
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

    private fun parseCustomAttributes(attrs: AttributeSet) {
        @SuppressLint("CustomViewStyleable") val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.CActionButton, 0, 0
            )
        try {
            titleText = typedArray.getString(R.styleable.CActionButton_title)
            labelText = typedArray.getString(R.styleable.CActionButton_label)
            textColor = typedArray.getColor(
                R.styleable.CActionButton_title_color,
                Color.WHITE
            )
            iconDrawable = typedArray.getAppCompatDrawable(context, R.styleable.CActionButton_icon)
            backgroundDrawableImg =
                typedArray.getAppCompatDrawable(context, R.styleable.CActionButton_background)
        } finally {
            typedArray.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        applyNativeAttributes()
        setupTitle()
        setupLabel()
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

    private fun setupTitle() {
        ui.tvTitle.text = if (Conditions.isNotNull(titleText)) titleText else ""
        ui.tvTitle.setTextColor(textColor)
    }

    private fun setupLabel() {
        ui.tvLabel.text = if (Conditions.isNotNull(labelText)) labelText else ""
    }

    private fun setupIcon() {
        if (Conditions.isNull(iconDrawable)) {
            ui.ivIcon.visibility = View.GONE
        } else {
            ui.ivIcon.visibility = View.VISIBLE
            ui.ivIcon.setImageDrawable(iconDrawable)
        }
    }

    private fun setupBackground() {
        if (Conditions.isNull(backgroundDrawableImg)) return
        rootView?.apply {
            val paddingStart = this.paddingStart
            val paddingTop = this.paddingTop
            val paddingEnd = this.paddingEnd
            val paddingBottom = this.paddingBottom
            this.background = backgroundDrawableImg
            this.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom)
        }
    }

    fun setTitle(text: String?) {
        titleText = text
        setupTitle()
    }

}