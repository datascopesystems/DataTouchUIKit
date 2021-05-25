package datatouch.uikit.components.listitems

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import datatouch.uikit.R
import datatouch.uikit.core.extensions.TypedArrayExtensions.getAppCompatDrawable
import datatouch.uikit.core.utils.Conditions.isNotNull
import datatouch.uikit.databinding.SettingsButtonPressBinding

class CSettingsButtonPress : RelativeLayout {

    private val ui = SettingsButtonPressBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var title: String? = null
    private var icon: Drawable? = null
    private var iconRight: Drawable? = null
    private var iconColor = 0
    private var iconRightColor = 0
    private var isRightIconVisible = false

    constructor(context: Context?) : super(context) {
    }

    constructor(
        context: Context?,
        attrs: AttributeSet
    ) : super(context, attrs) {
        parseAttributes(attrs)
    }

    private fun parseAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.CSettingsButtonPress, 0, 0
        )
        try {
            title = typedArray.getString(R.styleable.CSettingsButtonPress_CPTitle)
            icon = typedArray.getAppCompatDrawable(context, R.styleable.CSettingsButtonPress_CPIcon)
            iconRight = typedArray.getAppCompatDrawable(
                context,
                R.styleable.CSettingsButtonPress_CPIconRight
            )
            iconColor = typedArray.getColor(
                R.styleable.CSettingsButtonPress_CPIconColor,
                ContextCompat.getColor(context, R.color.primary)
            )
            iconRightColor = typedArray.getColor(
                R.styleable.CSettingsButtonPress_CPIconRightColor,
                ContextCompat.getColor(context, R.color.primary)
            )
            isRightIconVisible =
                typedArray.getBoolean(R.styleable.CSettingsButtonPress_CPIconRightVisible, false)
        } finally {
            typedArray.recycle()
        }
    }

    constructor(
        context: Context?,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        parseAttributes(attrs)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setTitle(if (isNotNull(title)) title else "")
        setIcon(icon)
        setRightIcon(iconRight)
        setIconColor(iconColor)
        setRitghIconColor(iconRightColor)
        setRightIconVisible(isRightIconVisible)
    }

    fun setTitle(title: String?) {
        ui.tvContent.text = title
    }

    fun setTitle(@StringRes resId: Int?) {
        ui.tvContent.setText(resId!!)
    }

    fun setIcon(drawable: Drawable?) {
        if (isNotNull(drawable)) ui.ivIcon.setImageDrawable(drawable)
    }

    protected fun inflateView() {
        View.inflate(context, R.layout.settings_button_press, this)
    }

    fun setIcon(@DrawableRes resId: Int?) {
        ui.ivIcon.setImageDrawable(ContextCompat.getDrawable(context, resId!!))
    }

    fun setRightIconVisible(visible: Boolean) {
        ui.ivIconRight.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setRightIcon(drawable: Drawable?) {
        if (isNotNull(drawable)) ui.ivIconRight.setImageDrawable(drawable)
    }

    fun setTextColor(color: Int) {
        ui.tvContent.setTextColor(color)
    }

    fun setIconRightColor(color: Int) {
        ui.ivIconRight.setColorFilter(color)
    }

    fun setRightIcon(@DrawableRes resId: Int?) {
        ui.ivIconRight.setImageDrawable(ContextCompat.getDrawable(context, resId!!))
    }

    fun setIconColor(color: Int) {
        ui.ivIcon.setColorFilter(color)
    }

    fun setRitghIconColor(color: Int) {
        ui.ivIconRight.setColorFilter(color)
    }
}