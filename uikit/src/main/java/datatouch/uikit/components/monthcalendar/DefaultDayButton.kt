package datatouch.uikit.components.monthcalendar

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import datatouch.uikit.core.extensions.IntExtensions.dp2Px
import datatouch.uikit.core.utils.views.IntSize
import datatouch.uikit.databinding.DefaultDayButtonBinding


class DefaultDayButton(context: Context) : AbstractMonthCalendarButton(context) {

    private val ui = DefaultDayButtonBinding
        .inflate(LayoutInflater.from(context), this, true)

    init { renderUi() }

    override fun getButtonRootView(): View {
        return ui.llRootView
    }

    override fun getButtonTextView(): TextView {
        return ui.tvDayOfMonth
    }

    override fun resetButtonState() {
    }

    override fun getMinSize(): IntSize {
        return IntSize(BUTTON_MIN_WIDTH_DP.dp2Px(context), BUTTON_MIN_HEIGHT_DP.dp2Px(context))
    }

    override fun downsizeUIComponents() {
        ui.tvDayOfMonth.setTextSize(TypedValue.COMPLEX_UNIT_SP, MIN_TEXT_SIZE_SP)
    }

    companion object {
        private const val BUTTON_MIN_WIDTH_DP = 80
        private const val BUTTON_MIN_HEIGHT_DP = 60
        private const val MIN_TEXT_SIZE_SP = 12f
    }
}