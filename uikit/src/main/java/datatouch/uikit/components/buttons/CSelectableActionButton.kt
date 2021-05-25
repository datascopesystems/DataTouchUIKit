package datatouch.uikit.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import datatouch.uikit.R
import datatouch.uikit.core.extensions.TypedArrayExtensions.getAppCompatDrawable
import datatouch.uikit.core.utils.Conditions
import datatouch.uikit.databinding.SelectableActionButtonBinding

class CSelectableActionButton : RelativeLayout {

    private val ui = SelectableActionButtonBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var titleText: String? = null
    private var iconDrawable: Drawable? = null

    constructor(context: Context?) : super(context) {}
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
        setupTitle()
        setupIcon()
        setupBackground(false)
    }

    private fun setupTitle() {
        ui.tvTitle.text = if (Conditions.isNotNull(titleText)) titleText else ""
    }

    private fun setupIcon() {
        if (Conditions.isNull(iconDrawable)) {
            ui.ivIcon.visibility = View.GONE
        } else {
            ui.ivIcon.visibility = View.VISIBLE
            ui.ivIcon.setImageDrawable(iconDrawable)
        }
    }

    fun setChecked(checked: Boolean) {
        setupBackground(checked)
    }

    private fun setupBackground(checked: Boolean) {
        ui.rlRoot.also {
            val paddingStart = it.paddingStart
            val paddingTop = it.paddingTop
            val paddingEnd = it.paddingEnd
            val paddingBottom = it.paddingBottom
            it.setBackgroundResource(if (checked) R.drawable.selectable_action_button_background_accent else R.drawable.selectable_action_button_background_general)
            it.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom)

        }
    }
}