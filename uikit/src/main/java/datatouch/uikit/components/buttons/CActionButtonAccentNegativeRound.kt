package datatouch.uikit.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import datatouch.uikit.R
import datatouch.uikit.core.extensions.TypedArrayExtensions.getAppCompatDrawable
import datatouch.uikit.core.utils.Conditions
import datatouch.uikit.core.utils.ResourceUtils
import datatouch.uikit.databinding.ActionButtonAccentNegativeRoundBinding

class CActionButtonAccentNegativeRound : RelativeLayout {

    private val ui = ActionButtonAccentNegativeRoundBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var titleText: String? = null
    private var iconDrawable: Drawable? = null
    private var layoutWidth = 0
    private var layoutHeight = 0

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
            layoutWidth = typedArray.getLayoutDimension(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutHeight = typedArray.getLayoutDimension(1, ViewGroup.LayoutParams.WRAP_CONTENT)
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
            iconDrawable = typedArray.getAppCompatDrawable(context, R.styleable.CActionButton_icon)
        } finally {
            typedArray.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        applyNativeAttributes()
        setupTitle()
        setupIcon()
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
        ui.tvTitle?.text = if (Conditions.isNotNull(titleText)) titleText else ""
    }

    private fun setupIcon() {
        if (Conditions.isNull(iconDrawable)) {
            ui.ivIcon.visibility = View.GONE
        } else {
            ui.ivIcon.visibility = View.VISIBLE
            ui.ivIcon.setImageDrawable(iconDrawable)
        }
    }

    fun setText(text: String?) {
        titleText = text
        setupTitle()
    }
}