package datatouch.uikit.core.utils.datetime

import datatouch.uikit.core.utils.datetime.internal.DateTimeUtilsInternal
import org.apache.commons.lang3.time.DateUtils
import java.util.*

object DateTimeUtils  {

    @JvmStatic
    fun currentSystemTimeMillis() = System.currentTimeMillis()

    fun isDateInPast(dateToCheck: Date?) : Boolean = DateTimeUtilsInternal.now().runCatching {
        !DateUtils.isSameDay(dateToCheck, this) && this > dateToCheck
    }.getOrDefault(false)

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