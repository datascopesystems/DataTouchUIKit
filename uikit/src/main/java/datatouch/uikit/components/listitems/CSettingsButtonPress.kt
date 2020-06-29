package datatouch.uikit.components.listitems

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import datatouch.uikit.R
import datatouch.uikit.utils.Conditions.isNotNull
import kotlinx.android.synthetic.main.settings_button_press.view.*

class CSettingsButtonPress : RelativeLayout {
    private var title: String? = null
    private var icon: Drawable? = null
    private var iconRight: Drawable? = null
    private var iconColor = 0
    private var iconRightColor = 0
    private var isRightIconVisible = false

    constructor(context: Context?) : super(context) {
        inflateView()
        afterViews()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet
    ) : super(context, attrs) {
        inflateView()
        parseAttributes(attrs)
        afterViews()
    }

    private fun parseAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.CSettingsButtonPress, 0, 0
        )
        try {
            title = typedArray.getString(R.styleable.CSettingsButtonPress_CPTitle)
            icon = typedArray.getDrawable(R.styleable.CSettingsButtonPress_CPIcon)
            iconRight = typedArray.getDrawable(R.styleable.CSettingsButtonPress_CPIconRight)
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
        inflateView()
        parseAttributes(attrs)
        afterViews()
    }

    fun afterViews() {
        setTitle(if (isNotNull(title)) title else "")
        setIcon(icon)
        setRightIcon(iconRight)
        setIconColor(iconColor)
        setRitghIconColor(iconRightColor)
        setRightIconVisible(isRightIconVisible)
    }

    fun setTitle(title: String?) {
        tvContent?.text = title
    }

    fun setTitle(@StringRes resId: Int?) {
        tvContent?.setText(resId!!)
    }

    fun setIcon(drawable: Drawable?) {
        if (isNotNull(drawable)) ivIcon!!.setImageDrawable(drawable)
    }

    protected fun inflateView() {
        View.inflate(context, R.layout.settings_button_press, this)
    }

    fun setIcon(@DrawableRes resId: Int?) {
        ivIcon?.setImageDrawable(ContextCompat.getDrawable(context, resId!!))
    }

    fun setRightIconVisible(visible: Boolean) {
        ivIconRight?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setRightIcon(drawable: Drawable?) {
        if (isNotNull(drawable)) ivIconRight!!.setImageDrawable(drawable)
    }

    fun setTextColor(color: Int) {
        tvContent?.setTextColor(color)
    }

    fun setIconRightColor(color: Int) {
        ivIconRight?.setColorFilter(color)
    }

    fun setRightIcon(@DrawableRes resId: Int?) {
        ivIconRight?.setImageDrawable(ContextCompat.getDrawable(context, resId!!))
    }

    fun setIconColor(color: Int) {
        ivIcon?.setColorFilter(color)
    }

    fun setRitghIconColor(color: Int) {
        ivIconRight?.setColorFilter(color)
    }
}