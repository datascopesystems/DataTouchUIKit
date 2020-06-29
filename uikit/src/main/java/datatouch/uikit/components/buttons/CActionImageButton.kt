package datatouch.uikit.components.buttons

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import datatouch.uikit.R
import datatouch.uikit.utils.Conditions
import kotlinx.android.synthetic.main.action_image_button.view.*


class CActionImageButton : RelativeLayout {


    private var iconDrawable: Drawable? = null
    private var backgroundViewDrawable: Drawable? = null
    private var iconColor = 0

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        inflateView()
        parseAttributes(attrs)
        initViews()
    }

    constructor(context: Context?, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        inflateView()
        parseAttributes(attrs)
        initViews()
    }

    private fun parseAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.CActionImageButton, 0, 0
        )
        try {
            iconDrawable = typedArray.getDrawable(R.styleable.CActionImageButton_iconDrawable)
            iconColor = typedArray.getColor(R.styleable.CActionImageButton_iconColor, Color.WHITE)
            backgroundViewDrawable =
                typedArray.getDrawable(R.styleable.CActionImageButton_iconBackground)
        } finally {
            typedArray.recycle()
        }
    }

    protected fun inflateView() {
        View.inflate(context, R.layout.action_image_button, this)
    }

    protected fun initViews() {
        setupIcon()
        setupBackground()
    }

    private fun setupIcon() {
        if (Conditions.isNull(iconDrawable)) {
            ivIcon.visibility = View.GONE
        } else {
            ivIcon.visibility = View.VISIBLE
            ivIcon.setImageDrawable(iconDrawable)
            ivIcon.setColorFilter(iconColor)
        }
    }

    private fun setupBackground() {
        if (Conditions.isNull(backgroundViewDrawable)) return
        val paddingStart = rlRoot.paddingStart
        val paddingTop = rlRoot.paddingTop
        val paddingEnd = rlRoot.paddingEnd
        val paddingBottom = rlRoot.paddingBottom
        rlRoot.background = backgroundViewDrawable
        rlRoot.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom)
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