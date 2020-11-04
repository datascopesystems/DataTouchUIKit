package datatouch.uikit.core.duration

import datatouch.uikit.core.extensions.ConditionsExtensions.isNotNull
import datatouch.uikit.core.extensions.GenericExtensions.default
import datatouch.uikit.core.utils.datetime.DateTimeFormatUtils
import datatouch.uikit.core.utils.datetime.DateTimeFormatUtils.tryParseDate
import datatouch.uikit.core.utils.datetime.DateTimeUtils
import datatouch.uikit.core.utils.datetime.internal.DateTimeUtilsInternal
import java.io.Serializable
import java.util.*

class Duration(var startDate: Date?, var endDate: Date?) : Serializable {

    constructor(startDate: String?, endDate: String?) : this(tryParseDate(startDate), tryParseDate(endDate))

    constructor(date: String?) : this(tryParseDate(date), tryParseDate(date))

    constructor(date: Date?) : this(date, date)

    val startDateString get() = startDate?.let { DateTimeFormatUtils.format(it) }

    val endDateString get() = endDate?.let { DateTimeFormatUtils.format(it) }

    val isLimited = startDate.isNotNull() && endDate.isNotNull()

    fun formatDefault() = format(DefaultDurationFormat)

    fun format(formatString: String): String {
        return String.format(
                Locale.getDefault(), formatString,
                startDateString, endDateString)
    }

    fun intersect(other: Duration): Duration {
        val newStart = listOf(this.startDate, other.startDate).maxBy { it.default(DateTimeUtilsInternal.now()) }
        val newEnd = listOf(this.endDate, other.endDate).minBy { it.default(DateTimeUtilsInternal.now()) }
        return Duration(newStart, newEnd)
    }

    fun isDateInDuration(date: String) =
            DateTimeUtils.isDateInRange(startDateString, endDateString, date)

    companion object {
        fun createForToday() = Duration(DateTimeUtilsInternal.now(), DateTimeUtilsInternal.now())
        fun createForSingle(dateString: String) = Duration(dateString, dateString)
    }
}

private const val DefaultDurationFormat = "%1\$s - %2\$s"