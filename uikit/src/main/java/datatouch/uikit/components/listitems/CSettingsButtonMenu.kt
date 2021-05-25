package datatouch.uikit.components.listitems

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import datatouch.uikit.R
import datatouch.uikit.core.extensions.TypedArrayExtensions.getAppCompatDrawable
import datatouch.uikit.core.utils.Conditions
import datatouch.uikit.core.utils.ResourceUtils
import datatouch.uikit.databinding.SettingsButtonMenuBinding

class CSettingsButtonMenu : RelativeLayout {

    private val ui = SettingsButtonMenuBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var title: String? = null
    private var iconDrawable: Drawable? = null
    private var iconColor = 0
    private var iconSize = 0
    private var textSize = 0

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

    @SuppressLint("CustomViewStyleable")
    private fun parseAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.CSettingsButtonCheck, 0, 0
        )
        try {
            title = typedArray.getString(R.styleable.CSettingsButtonCheck_CCTitle)
            iconDrawable = typedArray.getAppCompatDrawable(context, R.styleable.CSettingsButtonCheck_CCIcon)
            iconColor = typedArray.getColor(
                R.styleable.CSettingsButtonCheck_CCIconColor,
                ContextCompat.getColor(context, R.color.primary)
            )
            iconSize = typedArray.getDimensionPixelSize(
                R.styleable.CSettingsButtonCheck_CCIconSize,
                DEFAULT_ICON_SIZE
            )
            textSize = typedArray.getDimensionPixelSize(
                R.styleable.CSettingsButtonCheck_CCTextSize,
                ResourceUtils.convertDpToPixel(
                    context,
                    DEFAULT_TEXT_SIZE.toFloat()
                ).toInt()
            )
        } finally {
            typedArray.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setTitle(if (Conditions.isNotNull(title)) title else "")
        setIconDrawable(iconDrawable)
        setIconColor(iconColor)
        setIconSize(iconSize)
        setTextSize(textSize)
    }

    fun getTextSize(): Float {
        return textSize.toFloat()
    }

    fun setTextSize(textSize: Int) {
        this.textSize = textSize
        ui.tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
    }

    fun getIconSize(): Float {
        return iconSize.toFloat()
    }

    fun setIconSize(iconSize: Int) {
        this.iconSize = iconSize
        if (Conditions.isNotNull(ui.ivIcon.layoutParams)) {
            ui.ivIcon.layoutParams?.height = iconSize
            ui.ivIcon.layoutParams?.width = iconSize
        } else {
            ui.ivIcon.layoutParams = LayoutParams(iconSize, iconSize)
        }
    }

    fun setTitle(title: String?) {
        this.title = title
        ui.tvContent.text = title
    }

    fun setTitle(@StringRes resId: Int?) {
        title = context.getString(resId!!)
        ui.tvContent.text = title
    }

    fun setIconDrawable(drawable: Drawable?) {
        if (Conditions.isNotNull(drawable)) {
            iconDrawable = drawable
            ui.ivIcon.setImageDrawable(drawable)
        }
    }

    fun setIcon(@DrawableRes resId: Int?) {
        iconDrawable = ContextCompat.getDrawable(context, resId!!)
        ui.ivIcon.setImageDrawable(iconDrawable)
    }

    fun setIconColor(color: Int) {
        iconColor = color
        ui.ivIcon.setColorFilter(iconColor)
    }


    fun rlRoot() {
        performClick()
    }

    companion object {
        private const val DEFAULT_ICON_SIZE = 45
        private const val DEFAULT_TEXT_SIZE = 20
    }
}