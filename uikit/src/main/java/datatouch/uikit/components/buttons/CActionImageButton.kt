package datatouch.uikit.components.buttons

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import datatouch.uikit.R
import datatouch.uikit.core.extensions.TypedArrayExtensions.getAppCompatDrawable
import datatouch.uikit.core.utils.Conditions
import datatouch.uikit.databinding.ActionImageButtonBinding

class CActionImageButton : RelativeLayout {

    private val ui = ActionImageButtonBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var iconDrawable: Drawable? = null
    private var backgroundViewDrawable: Drawable? = null
    private var iconColor = 0

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        parseAttributes(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        parseAttributes(attrs)
    }

    private fun parseAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.CActionImageButton, 0, 0
        )
        try {
            iconDrawable = typedArray.getAppCompatDrawable(
                context,
                R.styleable.CActionImageButton_iconDrawable
            )
            iconColor = typedArray.getColor(R.styleable.CActionImageButton_iconColor, Color.WHITE)
            backgroundViewDrawable =
                typedArray.getAppCompatDrawable(
                    context,
                    R.styleable.CActionImageButton_iconBackground
                )
        } finally {
            typedArray.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupIcon()
        setupBackground()
    }

    private fun setupIcon() {
        if (Conditions.isNull(iconDrawable)) {
            ui.ivIcon.visibility = View.GONE
        } else {
            ui.ivIcon.visibility = View.VISIBLE
            ui.ivIcon.setImageDrawable(iconDrawable)
            ui.ivIcon.setColorFilter(iconColor)
        }
    }

    private fun setupBackground() {
        if (Conditions.isNull(backgroundViewDrawable)) return
        val paddingStart = ui.rlRoot.paddingStart
        val paddingTop = ui.rlRoot.paddingTop
        val paddingEnd = ui.rlRoot.paddingEnd
        val paddingBottom = ui.rlRoot.paddingBottom
        ui.rlRoot.background = backgroundViewDrawable
        ui.rlRoot.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom)
    }

    fun setIcon(iconDrawable: Drawable?) {
        this.iconDrawable = iconDrawable
        setupIcon()
    }

    fun setBackgroundViewDrawable(backgroundViewDrawable: Drawable?) {
        this.backgroundViewDrawable = backgroundViewDrawable
        setupBackground()
    }

    fun setIconColor(color: Int) {
        iconColor = color
        setupIcon()
    }
}