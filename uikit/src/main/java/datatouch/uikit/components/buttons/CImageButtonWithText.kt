package datatouch.uikit.components.buttons

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import datatouch.uikit.R
import datatouch.uikit.core.extensions.TypedArrayExtensions.getAppCompatDrawable
import datatouch.uikit.core.utils.Conditions
import datatouch.uikit.core.utils.animation.AnimationUtils
import datatouch.uikit.databinding.ImageButtonWithTextBinding

class CImageButtonWithText : RelativeLayout {

    private val ui = ImageButtonWithTextBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var title: String? = null
    private var iconDrawable: Drawable? = null
    private var isSelected = false

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
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        parseAttributes(attrs)
    }

    private fun parseAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.CImageButtonWithText, 0, 0
        )
        try {
            title = typedArray.getString(R.styleable.CImageButtonWithText_CIBText)
            iconDrawable =
                typedArray.getAppCompatDrawable(context, R.styleable.CImageButtonWithText_CIBIcon)
        } finally {
            typedArray.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        ui.rlRoot.setOnClickListener { rlRoot() }
        setTitle(if (Conditions.isNotNull(title)) title else "")
        setIconDrawable(iconDrawable)
        setSelected(isSelected)
    }

    fun setTitle(title: String?) {
        this.title = title
        ui.tvButtonText.text = title
    }

    fun setTitle(@StringRes resId: Int?) {
        title = context.getString(resId!!)
        ui.tvButtonText.text = title
    }

    fun setIconDrawable(drawable: Drawable?) {
        if (Conditions.isNotNull(drawable)) {
            iconDrawable = drawable
            ui.ivButtonIcon.setImageDrawable(drawable)
        }
    }

    fun setIcon(@DrawableRes resId: Int?) {
        iconDrawable = ContextCompat.getDrawable(context, resId!!)
        ui.ivButtonIcon.setImageDrawable(iconDrawable)
    }

    fun rlRoot() {
        toggle()
        performClick()
    }

    fun toggle() {
        isSelected = !isSelected
        refreshSelection()
    }

    private fun refreshSelection() {
        ui.vHighlight.setBackgroundColor(
            if (isSelected) ContextCompat.getColor(context, R.color.accent_end) else ContextCompat.getColor(context, R.color.accent)
        )
        ui.tvButtonText.setTextColor(if (isSelected) ContextCompat.getColor(context, R.color.accent_end) else Color.WHITE)
        AnimationUtils.animate(
            ui.vHighlight,
            if (isSelected) AnimationUtils.AnimationTechniques.FADE_IN else AnimationUtils.AnimationTechniques.FADE_OUT,
            400
        )
    }

    override fun isSelected(): Boolean {
        return isSelected
    }

    override fun setSelected(checked: Boolean) {
        isSelected = checked
        refreshSelection()
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)
        setSelected(isSelected)
    }
}