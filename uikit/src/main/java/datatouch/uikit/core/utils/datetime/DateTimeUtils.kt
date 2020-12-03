package datatouch.uikit.core.utils.datetime

import datatouch.uikit.core.utils.datetime.internal.DateTimeUtilsInternal
import java.util.*

object DateTimeUtils  {

    @JvmStatic
    fun currentSystemTimeMillis() = System.currentTimeMillis()

    fun isDateInPast(dateToCheck: Date?) : Boolean = DateTimeUtilsInternal.now().runCatching {
        !isSameDay(dateToCheck, this) && this > dateToCheck
    }.getOrDefault(false)

    private fun isSameDay(date1: Date?, date2: Date?): Boolean {
        if (date1 == null || date2 == null) {
            return false
        }

        val cal1 = Calendar.getInstance();
        cal1.time = date1;

        val cal2 = Calendar.getInstance();
        cal2.time = date2;

        return isSameDay(cal1, cal2);
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1[Calendar.ERA] == cal2[Calendar.ERA]
                && cal1[Calendar.YEAR] == cal2[Calendar.YEAR]
                && cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR]
    }

    fun isDateInRange(startDate: String?, endDate: String?, selectedDate: String?): Boolean {
        val start = DateTimeFormatUtils.parseDate(startDate)
        val end = DateTimeFormatUtils.parseDate(endDate)
        val selected = DateTimeFormatUtils.parseDate(selectedDate)
        return selected.before(end)
                && selected.after(start)
                || startDate == selectedDate
                || endDate == selectedDate
    }
}