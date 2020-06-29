package datatouch.uikit.interfaces

import java.io.Serializable
import java.util.*

interface IDurationRestriction : Serializable {
    val isDateRestrictionApplied: Boolean

    val restrictionMessage: String

    val startRestrictionDate: Date?
    val endRestrictionDate: Date?

    val startRestrictionDateString: String?
    val endRestrictionDateString: String?

    fun isSelectedDateAfterRestrictionEndDate(selectedDate: Date): Boolean
    fun isStartDateRestrictionApplied(selectedDate: Date): Boolean
    fun isEndDateRestrictionApplied(selectedDate: Date): Boolean

    fun isDateRangeIntersects(dateRangeStart: Date, dateRangeEnd: Date): Boolean
    fun isDateRangeIntersects(dateRangeStart: String, dateRangeEnd: String): Boolean
    fun isInDateRange(date: Date): Boolean
    fun isInDateRange(dateString: String): Boolean
}
