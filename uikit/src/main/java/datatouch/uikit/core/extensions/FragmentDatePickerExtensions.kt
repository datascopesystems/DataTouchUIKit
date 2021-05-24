package datatouch.uikit.core.extensions

import androidx.fragment.app.Fragment
import datatouch.uikit.components.timepicker.TimePoint
import datatouch.uikit.core.extensions.ContextDatePickerExtensions.showDatePicker
import datatouch.uikit.core.extensions.ContextDatePickerExtensions.showTimePicker
import datatouch.uikit.core.utils.datetime.DatePickerCallback
import datatouch.uikit.core.utils.datetime.DateTimeUtils
import datatouch.uikit.core.utils.datetime.TimePickerCallback
import java.util.*

object FragmentDatePickerExtensions {

    fun Fragment?.showDatePicker(selDate: Date?, callback: DatePickerCallback) =
        this?.context.showDatePicker(selDate, callback)

    fun Fragment?.showDatePicker(callback: DatePickerCallback) =
        this?.context.showDatePicker(DateTimeUtils.now(), callback)

    fun Fragment?.showTimePicker(timePoint: TimePoint, callback: TimePickerCallback) =
        this?.context.showTimePicker(timePoint, callback)

}