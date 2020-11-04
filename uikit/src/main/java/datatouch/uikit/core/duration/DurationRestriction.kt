package datatouch.uikit.core.duration

import datatouch.uikit.core.extensions.ConditionsExtensions.isNotNull
import datatouch.uikit.core.extensions.ConditionsExtensions.isNull
import datatouch.uikit.core.utils.datetime.DateTimeFormatUtils
import java.util.*

class DurationRestriction(
    override val startRestrictionDateString: String?,
    override val endRestrictionDateString: String?,
    override var restrictionMessage: String = ""
) : IDurationRestriction {

    constructor(duration: Duration?) : this(duration?.startDateString, duration?.endDateString)

    override val startRestrictionDate: Date?
        get() = DateTimeFormatUtils.tryParseDate(
            startRestrictionDateString
        )
    override val endRestrictionDate: Date?
        get() = DateTimeFormatUtils.tryParseDate(
            endRestrictionDateString
        )

    override val isSingleDay
        get() = startRestrictionDateString?.equals(endRestrictionDateString) ?: false

    override val isDateRestrictionApplied
        get() = startRestrictionDate.isNotNull() || endRestrictionDate.isNotNull()

    override fun isSelectedDateAfterRestrictionEndDate(selectedDate: Date) =
        endRestrictionDate.isNotNull() && selectedDate > endRestrictionDate

    override fun isStartDateRestrictionApplied(selectedDate: Date) =
        startRestrictionDate.isNotNull() && selectedDate < startRestrictionDate

    override fun isEndDateRestrictionApplied(selectedDate: Date) =
        endRestrictionDate.isNotNull() && selectedDate > endRestrictionDate

    override fun isDateRangeIntersects(dateRangeStart: Date, dateRangeEnd: Date): Boolean {
        if (startRestrictionDate.isNull() || endRestrictionDate.isNull()) return false

        // Check if start is after end
        if (dateRangeStart > dateRangeEnd) return false

        // Check if dateRangeStart included into range [startRestrictionDate:endRestrictionDate]
        if (dateRangeStart >= startRestrictionDate && dateRangeStart <= endRestrictionDate)
            return true

        // Check if dateRangeEnd included into range [startRestrictionDate:endRestrictionDate]
        if (dateRangeEnd >= startRestrictionDate && dateRangeEnd <= endRestrictionDate)
            return true

        // Check if [startRestrictionDate:endRestrictionDate] included into range [dateRangeStart:dateRangeEnd]
        return dateRangeStart < startRestrictionDate && dateRangeEnd > endRestrictionDate
    }

    override fun isDateRangeIntersects(dateRangeStart: String, dateRangeEnd: String): Boolean {
        val dateStart = DateTimeFormatUtils.tryParseDate(dateRangeStart)
        val dateEnd = DateTimeFormatUtils.tryParseDate(dateRangeEnd)
        return dateStart.isNotNull() && dateEnd.isNotNull() && isDateRangeIntersects(
            dateStart!!,
            dateEnd!!
        )
    }

    override fun isInDateRange(date: Date): Boolean {
        if (startRestrictionDate.isNull() || endRestrictionDate.isNull()) return false

        return date >= startRestrictionDate && date <= endRestrictionDate
    }

    override fun isInDateRange(dateString: String): Boolean {
        val date = DateTimeFormatUtils.tryParseDate(dateString)
        return date.isNotNull() && isInDateRange(date!!)
    }

    override fun isInDateRange(duration: Duration): Boolean {
        val from = duration.startDate
        val to = duration.endDate
        return from.isNotNull()
                && to.isNull()
                && isInDateRange(from!!)
                && isInDateRange(to!!)
    }
}