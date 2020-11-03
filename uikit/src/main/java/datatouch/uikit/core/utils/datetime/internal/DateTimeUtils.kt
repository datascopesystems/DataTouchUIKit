package datatouch.uikit.core.utils.datetime.internal

import java.util.*

internal object DateTimeUtils {

    internal fun now() = Date()

    internal fun calendar(date: Date = now()) = Calendar.getInstance().also { it.time = date }

    internal fun getDate(year: Int, month: Int, day: Int): Date {
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

}