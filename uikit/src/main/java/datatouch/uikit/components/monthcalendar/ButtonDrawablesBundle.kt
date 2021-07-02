package datatouch.uikit.components.monthcalendar

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import datatouch.uikit.R


open class ButtonDrawablesBundle(val context: Context) {

    open val bgMiddle: Drawable?
    get() = ContextCompat.getDrawable(context, R.drawable.month_calendar_button_background)

    open val bgTopLeft: Drawable?
    get() = ContextCompat.getDrawable(context, R.drawable.month_calendar_button_top_left_background)

    open val bgTopRight: Drawable?
    get() = ContextCompat.getDrawable(context, R.drawable.month_calendar_button_top_right_background)

    open val bgBottomLeft: Drawable?
    get() = ContextCompat.getDrawable(context, R.drawable.month_calendar_button_bottom_left_background)

    open val bgBottomRight: Drawable?
    get() = ContextCompat.getDrawable(context, R.drawable.month_calendar_button_bottom_right_background)

    open val inactiveBgMiddle: Drawable?
    get() = ContextCompat.getDrawable(context, R.drawable.month_calendar_inactive_button_background)

    open val inactiveBgTopLeft: Drawable?
    get() = ContextCompat.getDrawable(context, R.drawable.month_calendar_inactive_button_top_left_background)

    open val inactiveBgTopRight: Drawable?
    get() = ContextCompat.getDrawable(context, R.drawable.month_calendar_inactive_button_top_right_background)

    open val inactiveBgBottomLeft: Drawable?
    get() = ContextCompat.getDrawable(context, R.drawable.month_calendar_inactive_button_bottom_left_background)

    open val inactiveBgBottomRight: Drawable?
    get() = ContextCompat.getDrawable(context, R.drawable.month_calendar_inactive_button_bottom_right_background)

    open val selectedBgMiddle: Drawable?
    get() = ContextCompat.getDrawable(context, R.drawable.month_calendar_selected_button_background)

    open val selectedBgTopLeft: Drawable?
    get() = ContextCompat.getDrawable(context, R.drawable.month_calendar_selected_button_top_left_background)

    open val selectedBgTopRight: Drawable?
    get() = ContextCompat.getDrawable(context, R.drawable.month_calendar_selected_button_top_right_background)

    open val selectedBgBottomLeft: Drawable?
    get() = ContextCompat.getDrawable(context, R.drawable.month_calendar_selected_button_bottom_left_background)

    open val selectedBgBottomRight: Drawable?
    get() = ContextCompat.getDrawable(context, R.drawable.month_calendar_selected_button_bottom_right_background)

    open val activeDayTextColor: Int
    get() = Color.WHITE

    open val inactiveDayTextColor: Int
    get() = 0xFFD5D5D5.toInt()

    open val todayTextColor: Int
    get() = 0xFF9ADFF2.toInt()
}