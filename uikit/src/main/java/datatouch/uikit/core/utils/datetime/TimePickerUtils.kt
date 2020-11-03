package datatouch.uikit.core.utils.datetime

import androidx.fragment.app.FragmentActivity
import datatouch.uikit.components.timepicker.TimePickerDialog
import datatouch.uikit.components.timepicker.TimePoint

typealias TimePickerCallback = (timePoint: TimePoint, pickedDateTimeStr: String) -> Unit

object TimePickerUtils {

    fun showTimePicker(fragmentActivity: FragmentActivity?,
                       timeData: TimePoint,
                       callback: TimePickerCallback) = fragmentActivity?.let {
        val tpd = TimePickerDialog.newInstance(
            callback,
            timeData,
            true
        )
        tpd.dismissOnPause(false)
        tpd.enableSeconds(false)
        tpd.isThemeDark = true
        tpd.show(fragmentActivity.supportFragmentManager, TimePickerDialog::javaClass.name)
    }

}