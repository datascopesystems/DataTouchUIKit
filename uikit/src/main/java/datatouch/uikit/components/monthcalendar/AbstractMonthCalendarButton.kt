package datatouch.uikit.components.monthcalendar

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import datatouch.uikit.core.callbacks.UiCallback
import datatouch.uikit.core.extensions.IntExtensions.dp2Px
import datatouch.uikit.core.utils.views.IntSize
import org.threeten.bp.LocalDate


abstract class AbstractMonthCalendarButton : LinearLayout {

    private var buttonCellType = ButtonCellType.UNDEFINED
    private var drawablesBundle: ButtonDrawablesBundle

    var isDayActive: Boolean = true
        private set

    private var isToday: Boolean = false

    private var date: LocalDate? = null

    private var onClickCallback: UiCallback<LocalDate?>? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        this.isClickable = true
        drawablesBundle = ButtonDrawablesBundle(context)
        val w = DEFAULT_BUTTON_WIDTH_DP.dp2Px(context)
        val h = DEFAULT_BUTTON_HEIGHT_DP.dp2Px(context)
        layoutParams = ViewGroup.LayoutParams(w, h)
    }

    protected open fun renderUi() {
        setIsActiveDay(true)
        setupDateText()
    }

    fun setDrawablesBundle(bundle: ButtonDrawablesBundle) {
        drawablesBundle = bundle
    }

    fun setIsActiveDay(isDayActive: Boolean) {
        this.isDayActive = isDayActive
        updateButtonTextColor(getButtonTextView())
        updateButtonBackground()
    }

    fun setDate(date: LocalDate?) {
        this.date = date
        setupDateText()
    }

    fun setTodayMarker(isToday: Boolean) {
        this.isToday = isToday

        val textView = getButtonTextView()
        updateButtonTextColor(textView)

        textView.paint.isUnderlineText = isToday
    }

    fun setOnClickCallback(callback: UiCallback<LocalDate?>?) {
        onClickCallback = callback

        when (callback == null) {
            true -> setOnClickListener(null)
            else -> setOnClickListener {
                onClickCallback?.invoke(date)
            }
        }
    }

    private fun setupDateText() {
        if (date != null) {
            val text = date?.dayOfMonth.toString()
            getButtonTextView().text = text
        }
    }

    protected open fun getBackgroundDrawableForType(buttonCellType: ButtonCellType): Drawable? {
        var drawable:Drawable?

        if (buttonCellType == ButtonCellType.MIDDLE) {
            drawable = if (isDayActive) drawablesBundle.bgMiddle else drawablesBundle.inactiveBgMiddle
            drawable = if (isSelected) drawablesBundle.selectedBgMiddle else drawable
            return drawable
        }

        if (buttonCellType == ButtonCellType.TOP_LEFT) {
            drawable = if (isDayActive) drawablesBundle.bgTopLeft else drawablesBundle.inactiveBgTopLeft
            drawable = if (isSelected) drawablesBundle.selectedBgTopLeft else drawable
            return drawable
        }

        if (buttonCellType == ButtonCellType.TOP_RIGHT) {
            drawable = if (isDayActive) drawablesBundle.bgTopRight else drawablesBundle.inactiveBgTopRight
            drawable = if (isSelected) drawablesBundle.selectedBgTopRight else drawable
            return drawable
        }

        if (buttonCellType == ButtonCellType.BOTTOM_LEFT) {
            drawable = if (isDayActive) drawablesBundle.bgBottomLeft else drawablesBundle.inactiveBgBottomLeft
            drawable = if (isSelected) drawablesBundle.selectedBgBottomLeft else drawable
            return drawable
        }

        if (buttonCellType == ButtonCellType.BOTTOM_RIGHT) {
            drawable = if (isDayActive) drawablesBundle.bgBottomRight else drawablesBundle.inactiveBgBottomRight
            drawable = if (isSelected) drawablesBundle.selectedBgBottomRight else drawable
            return drawable
        }

        drawable = if (isDayActive) drawablesBundle.bgMiddle else drawablesBundle.inactiveBgMiddle
        drawable = if (isSelected) drawablesBundle.selectedBgMiddle else drawable
        return drawable
    }

    protected abstract fun getButtonRootView(): View

    protected abstract fun getButtonTextView(): TextView

    abstract fun resetButtonState()

    abstract fun getMinSize(): IntSize

    protected abstract fun downsizeUIComponents()

    private fun updateButtonTextColor(textView: TextView?) {
        var color = if (isDayActive) drawablesBundle.activeDayTextColor else drawablesBundle.inactiveDayTextColor
        if (isToday) {
            color = drawablesBundle.todayTextColor
        }
        textView?.setTextColor(color)
    }

    private fun updateButtonBackground() {
        val bgDrawable = getBackgroundDrawableForType(buttonCellType)
        getButtonRootView().background = bgDrawable
    }

    fun setCellType(buttonCellType: ButtonCellType) {
        if (this.buttonCellType != buttonCellType) {
            this.buttonCellType = buttonCellType
            updateButtonBackground()
        }
    }

    override fun setSelected(selected: Boolean) {
        if (isSelected != selected) {
            super.setSelected(selected)
            updateButtonBackground()
        }
    }

    open fun setSize(w: Int, h: Int) {
        updateLayoutParams {
            width = w
            height = h
        }
        getButtonRootView().updateLayoutParams {
            width = w
            height = h
        }

        getMinSize().let {
            if (w < it.w || h < it.h) {
                downsizeUIComponents()
            }
        }
    }

    fun setDayTextSizeSp(sizeSp: Float) {
        getButtonTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp)
    }

    fun setDayTextBold(bold: Boolean) {
        val tv = getButtonTextView()
        when (bold) {
            true -> tv.setTypeface(tv.typeface, Typeface.BOLD)
            else -> tv.setTypeface(Typeface.create(tv.typeface, Typeface.NORMAL))
        }
    }
    
    companion object {
        const val DEFAULT_BUTTON_WIDTH_DP = 130
        const val DEFAULT_BUTTON_HEIGHT_DP = 80
    }
}