package datatouch.uikit.core.utils.datetime

import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import datatouch.uikit.R
import datatouch.uikit.core.extensions.GenericExtensions.default
import datatouch.uikit.core.utils.datetime.internal.DateTimeUtilsInternal
import java.util.*

typealias DatePickerCallback = (pickedDate: Date, pickedDateStr: String) -> Unit

object DatePickerUtils {

    private const val MinYear = 2015
    private const val MaxYear = 2030

    val defaultMinDate = createDate(MinYear, 1, 1)
    val defaultMaxDate = createDate(MaxYear, 1, 1)

    @JvmStatic
    fun getMonthsAsStringArray(context: Context): List<String> {
        return listOf(*context.resources.getStringArray(R.array.months))
    }

    fun tryAccessibilityAnnounce(view: View?, text: CharSequence?) {
        if (view != null && text != null) {
            view.announceForAccessibility(text)
        }
    }

    fun trimToMidnight(calendar: Calendar): Calendar {
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar
    }

    fun createDate(year: Int, month: Int, day: Int): Date {
        val cal = Calendar.getInstance()
        cal[Calendar.YEAR] = year
        cal[Calendar.MONTH] = month
        cal[Calendar.DAY_OF_MONTH] = day
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        cal[Calendar.MILLISECOND] = 0
        return cal.time
    }

    fun showDatePickerInRange(
        context: Context?,
        selectedDate: Date?,
        callback: DatePickerCallback,
        minDate: Date? = null,
        maxDate: Date? = null) = context?.let {

        val calendar = DateTimeUtilsInternal.calendar()
        calendar.time = selectedDate.default(DateTimeUtilsInternal.now())

        val dialog = DatePickerDialog(it,
                { _, year, month, dayOfMonth ->
                    callback.invoke(
                        DateTimeUtilsInternal.getDate(year, month, dayOfMonth),
                        DateTimeFormatUtils.format(
                            DateTimeUtilsInternal.getDate(
                                year,
                                month,
                                dayOfMonth)))
                },
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH])


        dialog.datePicker.minDate = minDate?.time.default(defaultMinDate.time)
        dialog.datePicker.maxDate = maxDate?.time.default(defaultMaxDate.time)

        dialog.show()
    }

}