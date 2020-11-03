package datatouch.uikit.components.listitems

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import datatouch.uikit.R
import datatouch.uikit.core.utils.Conditions
import datatouch.uikit.core.utils.ResourceUtils
import kotlinx.android.synthetic.main.settings_button_check.view.*

class CSettingsButtonCheck : RelativeLayout {

    private var title: String? = null
    private var iconDrawable: Drawable? = null
    private var textColor = 0
    private var iconColor = 0
    private var iconSize = 0
    private var textSize = 0
    private var isChecked = false

    constructor(context: Context?) : super(context) {}
    constructor(
        context: Context?,
        attrs: AttributeSet
    ) : super(context, attrs) {
        inflateView()
        parseAttributes(attrs)
        afterViews()
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

    protected fun inflateView() {
        View.inflate(context, R.layout.settings_button_check, this)
    }

    private fun parseAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.CSettingsButtonCheck, 0, 0
        )
        try {
            title = typedArray.getString(R.styleable.CSettingsButtonCheck_CCTitle)
            iconDrawable = typedArray.getDrawable(R.styleable.CSettingsButtonCheck_CCIcon)
            iconColor = typedArray.getColor(
                R.styleable.CSettingsButtonCheck_CCIconColor,
                ContextCompat.getColor(context, R.color.primary)
            )
            textColor = typedArray.getColor(
                R.styleable.CSettingsButtonCheck_CCIconColor,
                ContextCompat.getColor(context, R.color.white)
            )
            iconSize = typedArray.getDimensionPixelSize(
                R.styleable.CSettingsButtonCheck_CCIconSize,
                DEFAULT_ICON_SIZE
            )
            textSize = typedArray.getDimensionPixelSize(
                R.styleable.CSettingsButtonCheck_CCTextSize,
                ResourceUtils.convertDpToPixel(
                    context,
                    DEFAULT_TEXT_SIZE
                ) as Int
            )
        } finally {
            typedArray.recycle()
        }
    }

    fun afterViews() {
        setTitle(if (Conditions.isNotNull(title)) title else "")
        setIconDrawable(iconDrawable)
        setIconColor(iconColor)
        setChecked(isChecked)
        setIconSize(iconSize)
        setTextSize(textSize)
        setTextColor(textColor)
    }

    fun getTextSize(): Float {
        return textSize.toFloat()
    }

    fun setTextSize(textSize: Int) {
        this.textSize = textSize
        tvContent?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
    }

    fun setTextColor(textColor: Int) {
        this.textColor = textColor
        tvContent?.setTextColor(textColor)
    }

    fun getIconSize(): Float {
        return iconSize.toFloat()
    }

    fun setIconSize(iconSize: Int) {
        this.iconSize = iconSize
        if (Conditions.isNotNull(ivIcon?.layoutParams)) {
            ivIcon?.layoutParams?.height = iconSize
            ivIcon?.layoutParams?.width = iconSize
        } else {
            ivIcon?.layoutParams = LayoutParams(iconSize, iconSize)
        }
    }

    fun setTitle(title: String?) {
        this.title = title
        tvContent?.text = title
    }

    fun setTitle(@StringRes resId: Int?) {
        title = context.getString(resId!!)
        tvContent?.text = title
    }

    fun setIconDrawable(drawable: Drawable?) {
        if (Conditions.isNotNull(drawable)) {
            iconDrawable = drawable
            ivIcon?.setImageDrawable(drawable)
        }
    }

    fun setIcon(@DrawableRes resId: Int?) {
        iconDrawable = ContextCompat.getDrawable(context, resId!!)
        ivIcon?.setImageDrawable(iconDrawable)
    }

    fun setIconColor(color: Int) {
        iconColor = color
        ivIcon?.setColorFilter(iconColor)
    }

    fun rlRoot() {
        toggle()
        performClick()
    }

    fun toggle() {
        isChecked = !isChecked
        refreshCheckBox()
    }

    private fun refreshCheckBox() {
        ivChecked?.setImageResource(if (isChecked) R.drawable.ic_check_box_checked_green else R.drawable.ic_check_box_unchecked_green)
    }

    fun isChecked(): Boolean {
        return isChecked
    }

    fun setChecked(checked: Boolean) {
        isChecked = checked
        refreshCheckBox()
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)
        setChecked(isChecked)
    }

    companion object {
        private const val DEFAULT_ICON_SIZE = 45
        private const val DEFAULT_TEXT_SIZE = 20F
    }
}