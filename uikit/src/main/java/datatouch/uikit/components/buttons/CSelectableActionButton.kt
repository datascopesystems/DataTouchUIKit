package datatouch.uikit.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import datatouch.uikit.R
import datatouch.uikit.utils.Conditions
import kotlinx.android.synthetic.main.selectable_action_button.view.*

class CSelectableActionButton : RelativeLayout {

    private var titleText: String? = null
    private var iconDrawable: Drawable? = null

    constructor(context: Context?) : super(context) {}
    constructor(
        context: Context?,
        attrs: AttributeSet
    ) : super(context, attrs) {
        inflateView()
        parseAttributes(attrs)
        initViews()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        inflateView()
        parseAttributes(attrs)
        initViews()
    }

    private fun parseAttributes(attrs: AttributeSet) {
        @SuppressLint("CustomViewStyleable") val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.CActionButton, 0, 0
            )
        try {
            titleText = typedArray.getString(R.styleable.CActionButton_title)
            iconDrawable = typedArray.getDrawable(R.styleable.CActionButton_icon)
        } finally {
            typedArray.recycle()
        }
    }

    fun initViews() {
        setupTitle()
        setupIcon()
        setupBackground(false)
    }

    protected fun inflateView() {
        View.inflate(context, R.layout.selectable_action_button, this)
    }

    private fun setupTitle() {
        tvTitle?.text = if (Conditions.isNotNull(titleText)) titleText else ""
    }

    private fun setupIcon() {
        if (Conditions.isNull(iconDrawable)) {
            ivIcon?.visibility = View.GONE
        } else {
            ivIcon?.visibility = View.VISIBLE
            ivIcon?.setImageDrawable(iconDrawable)
        }
    }

    fun setChecked(checked: Boolean) {
        setupBackground(checked)
    }

    private fun setupBackground(checked: Boolean) {
        rlRoot?.also {
            val paddingStart = it.paddingStart
            val paddingTop = it.paddingTop
            val paddingEnd = it.paddingEnd
            val paddingBottom = it.paddingBottom
            it.setBackgroundResource(if (checked) R.drawable.selectable_action_button_background_accent else R.drawable.selectable_action_button_background_general)
            it.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom)

        }
    }
}