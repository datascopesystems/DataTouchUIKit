package datatouch.uikit.core.utils.datetime

import android.app.TimePickerDialog
import android.content.Context
import datatouch.uikit.components.timepicker.TimePoint

typealias TimePickerCallback = (timePoint: TimePoint, pickedDateTimeStr: String) -> Unit

object TimePickerUtils {

    fun showTimePicker(context: Context?,
                       timePoint: TimePoint,
                       callback: TimePickerCallback,
                       is24HourFormat :Boolean = true) = context?.let {
        val dialog = TimePickerDialog(it, { _, hourOfDay, minute ->

            val selectedTimePoint = TimePoint(hourOfDay, minute)
            callback.invoke(selectedTimePoint, selectedTimePoint.toString())

        }, timePoint.hour, timePoint.minute, is24HourFormat)

        dialog.show()
    }

}