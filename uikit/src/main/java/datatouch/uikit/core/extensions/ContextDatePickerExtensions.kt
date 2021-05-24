package datatouch.uikit.core.extensions

import android.content.Context
import datatouch.uikit.components.timepicker.TimePoint
import datatouch.uikit.core.utils.datetime.*
import java.util.*

object ContextDatePickerExtensions {

    fun Context?.showDatePicker(callback: DatePickerCallback) =
        showDatePicker(DateTimeUtils.now(), callback)

    fun Context?.showDatePicker(selDate: Date?, callback: DatePickerCallback) =
        DatePickerUtils.showDatePickerInRange(
            this, selDate, callback, null, null
        )

    fun Context?.showDatePicker(
        selDate: Date?,
        minDate: Date?,
        maxDate: Date?,
        callback: DatePickerCallback
    ) =
        DatePickerUtils.showDatePickerInRange(
            this, selDate, callback, minDate, maxDate
        )

    fun Context?.showTimePicker(timePoint: TimePoint, callback: TimePickerCallback) =
        TimePickerUtils.showTimePicker(this, timePoint, callback)

}