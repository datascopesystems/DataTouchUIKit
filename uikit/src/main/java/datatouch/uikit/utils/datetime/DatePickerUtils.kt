package datatouch.uikit.utils.datetime

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import datatouch.uikit.R
import datatouch.uikit.components.datapicker.date.DatePickerFragmentDialog
import datatouch.uikit.utils.datetime.internal.DateTimeUtils
import datatouch.uikit.utils.default
import java.util.*

typealias DatePickerCallback = (pickedDate: Date, pickedDateStr: String) -> Unit

object DatePickerUtils {

    private const val StartDateRange = 2015
    private const val EndDateRange = 2025

    val yearsRange: ArrayList<String>
        get() {
            val years = ArrayList<String>()
            for (i in StartDateRange until EndDateRange) years.add(i.toString())
            return years
        }

    @JvmStatic
    fun getMonthsAsStringArray(context: Context): List<String> {
        return listOf(*context.resources.getStringArray(R.array.months))
    }

    @JvmStatic
    @SuppressLint("NewApi")
    fun tryAccessibilityAnnounce(view: View?, text: CharSequence?) {
        if (view != null && text != null) {
            view.announceForAccessibility(text)
        }
    }

    @JvmStatic
    fun trimToMidnight(calendar: Calendar): Calendar {
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar
    }

    fun showDatePicker(fragmentActivity: FragmentActivity?,
                       currentDate: Date?,
                       callback: DatePickerCallback
    ) = fragmentActivity?.let {
        val calendar = DateTimeUtils.calendar()
        calendar.time = currentDate.default(DateTimeUtils.now())
        val dialog = DatePickerFragmentDialog.newInstance(
            object : DatePickerFragmentDialog.OnDateSetListener {
                override fun onDateSet(view: DatePickerFragmentDialog?,
                                       year: Int, monthOfYear: Int, dayOfMonth: Int) {
                    callback.invoke(
                        DateTimeUtils.getDate(year, monthOfYear, dayOfMonth),
                        DateTimeFormatUtils.format(DateTimeUtils.getDate(year, monthOfYear, dayOfMonth)))
                }
            },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH])
        dialog.isThemeDark = true
        dialog.setAccentColor(ContextCompat.getColor(it, R.color.accent_start_dark))
        dialog.setYearRange(StartDateRange, EndDateRange)
        dialog.show(it.supportFragmentManager, DatePickerFragmentDialog::class.java.name)
    }

    @JvmStatic
    fun showDatePickerInRange(fragmentActivity: FragmentActivity?,
                              currentDate: Date?,
                              callback: DatePickerCallback,
                              minDate: Date?,
                              maxDate: Date?,
                              disableDates: List<Date>? = null) = fragmentActivity?.let {
        val calendar = DateTimeUtils.calendar()
        calendar.time = currentDate.default(DateTimeUtils.now())
        val dialog = DatePickerFragmentDialog.newInstance(
            object : DatePickerFragmentDialog.OnDateSetListener {
                override fun onDateSet(view: DatePickerFragmentDialog?,
                                       year: Int, monthOfYear: Int, dayOfMonth: Int) {
                    callback.invoke(
                        DateTimeUtils.getDate(year, monthOfYear, dayOfMonth),
                        DateTimeFormatUtils.format(DateTimeUtils.getDate(year, monthOfYear, dayOfMonth)))
                }
            },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH])
        dialog.isThemeDark = true
        dialog.setAccentColor(ContextCompat.getColor(it, R.color.accent_start_dark))

        minDate?.apply { dialog.setMinDate(time) }
        maxDate?.apply { dialog.setMaxDate(time) }

        dialog.setYearRange(StartDateRange, EndDateRange)
        dialog.setMonthYearPickersVisible(false)
        dialog.setDisabledDays(disableDates?.map { date ->
            val c = Calendar.getInstance()
            c.timeInMillis = date.time
            c
        }?.toTypedArray())
        dialog.show(it.supportFragmentManager, DatePickerFragmentDialog::class.java.name)
    }

}