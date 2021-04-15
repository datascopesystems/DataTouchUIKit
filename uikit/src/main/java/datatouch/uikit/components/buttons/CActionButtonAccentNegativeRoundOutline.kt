package datatouch.uikit.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import datatouch.uikit.R
import datatouch.uikit.core.extensions.TypedArrayExtensions.getAppCompatDrawable
import datatouch.uikit.core.utils.Conditions
import kotlinx.android.synthetic.main.action_button_accent_negative_round.view.ivIcon
import kotlinx.android.synthetic.main.action_button_accent_negative_round.view.tvTitle
import kotlinx.android.synthetic.main.action_button_accent_outline.view.*

open class CActionButtonAccentNegativeRoundOutline : RelativeLayout {

    private var characterLimit = NO_CHARACTERS_LIMIT
    private var titleText: String? = null
    private var iconDrawable: Drawable? = null
    private var outline: Drawable? = null

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
            characterLimit = typedArray.getInteger(
                R.styleable.CActionButton_maxTextCharacters,
                NO_CHARACTERS_LIMIT
            )
            titleText = typedArray.getString(R.styleable.CActionButton_title)
            iconDrawable = typedArray.getAppCompatDrawable(context, R.styleable.CActionButton_icon)
            outline = typedArray.getAppCompatDrawable(context, R.styleable.CActionButton_outline)
        } finally {
            typedArray.recycle()
        }
    }

    fun initViews() {
        setupOutline()
        setupTitle()
        setupIcon()
    }

    protected fun inflateView() {
        View.inflate(context, R.layout.action_button_negative_accent_outline, this)
    }

    private fun setupTitle() {
        tvTitle?.text = if (Conditions.isNotNull(titleText)) titleText else ""
        if (isCharacterLimitValid) tvTitle?.filters = arrayOf<InputFilter>(
            InputFilter.LengthFilter(
                characterLimit
            )
        )
    }

    private val isCharacterLimitValid: Boolean
        private get() = NO_CHARACTERS_LIMIT != characterLimit && characterLimit >= 0

    fun setText(text: String?) {
        titleText = text
        setupTitle()
    }

    private fun setupOutline() {
        if (Conditions.isNotNull(outline)) {
            rlRoot?.background = outline
        }
    }

    private fun setupIcon() {
        if (Conditions.isNull(iconDrawable)) {
            ivIcon?.visibility = View.GONE
        } else {
            ivIcon?.visibility = View.VISIBLE
            ivIcon?.setImageDrawable(iconDrawable)
        }
    }

    companion object {
        private const val NO_CHARACTERS_LIMIT = -1
    }
}