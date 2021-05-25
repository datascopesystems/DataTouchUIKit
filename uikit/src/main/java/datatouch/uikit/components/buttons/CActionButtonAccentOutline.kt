package datatouch.uikit.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import datatouch.uikit.R
import datatouch.uikit.core.extensions.TypedArrayExtensions.getAppCompatDrawable
import datatouch.uikit.core.utils.Conditions
import datatouch.uikit.databinding.ActionButtonAccentOutlineBinding

class CActionButtonAccentOutline : RelativeLayout {

    private val ui = ActionButtonAccentOutlineBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var characterLimit = NO_CHARACTERS_LIMIT
    private var titleText: String? = null
    private var iconDrawable: Drawable? = null
    private var outline: Drawable? = null
    private var textColor = Color.WHITE
    private var iconColor = Color.WHITE

    constructor(context: Context?) : super(context)

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
            characterLimit = typedArray.getInteger(
                R.styleable.CActionButton_maxTextCharacters,
                NO_CHARACTERS_LIMIT
            )

            titleText = typedArray.getString(R.styleable.CActionButton_title)

            iconDrawable = typedArray.getAppCompatDrawable(context, R.styleable.CActionButton_icon)

            outline = typedArray.getAppCompatDrawable(context, R.styleable.CActionButton_outline)

            textColor = typedArray.getColor(R.styleable.CActionButton_title_color, Color.WHITE)

            iconColor = typedArray.getColor(R.styleable.CActionButton_icon_colour, Color.WHITE)
        } finally {
            typedArray.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupOutline()
        setupTitle()
        setupIcon()
    }

    private fun setupTitle() {
        ui.tvTitle.text = if (Conditions.isNotNull(titleText)) titleText else ""
        ui.tvTitle.setTextColor(textColor)
        if (isCharacterLimitValid) ui.tvTitle.filters = arrayOf<InputFilter>(
            LengthFilter(
                characterLimit
            )
        )
    }

    private val isCharacterLimitValid: Boolean
        get() = NO_CHARACTERS_LIMIT != characterLimit && characterLimit >= 0

    fun setText(text: String?) {
        titleText = text
        setupTitle()
    }

    private fun setupOutline() {
        if (Conditions.isNotNull(outline)) {
            ui.rlRoot.background = outline
        }
    }

    private fun setupIcon() {
        if (Conditions.isNull(iconDrawable)) {
            ui.ivIcon.visibility = View.GONE
        } else {
            ui.ivIcon.visibility = View.VISIBLE
            ui.ivIcon.setImageDrawable(iconDrawable)
            setIconColor(iconColor)
        }
    }

    fun setIconColor(color: Int) = ui.ivIcon.setColorFilter(color)

    companion object {
        private const val NO_CHARACTERS_LIMIT = -1
    }
}