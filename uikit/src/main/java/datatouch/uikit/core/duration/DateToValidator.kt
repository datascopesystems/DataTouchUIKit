package datatouch.uikit.core.duration

import datatouch.uikit.core.callbacks.UiJustCallback
import datatouch.uikit.core.utils.datetime.DateTimeUtils
import java.util.*


class DateToValidator(private val durationRestriction: IDurationRestriction?,
                      private val dateTo: Date) {

    private var isDateOutsideOfRestriction = false
    private var isEndDateInPast = false

    val isValid: Boolean
        get() = !isDateOutsideOfRestriction && !isEndDateInPast

    fun isDateOutsideOfRestriction(callback: UiJustCallback? = null): DateToValidator {
        if (isValid) { // Prevent checks and callback if we already got "invalid" state
            // Check if selected dateTo is after endRestrictionDate
            if (durationRestriction?.isEndDateRestrictionApplied(dateTo) == true) {
                isDateOutsideOfRestriction = true
                callback?.invoke()
            }
        }
        return this
    }

    fun isEndDateInPast(callback: UiJustCallback? = null): DateToValidator {
        if (isValid) { // Prevent checks and callback if we already got "invalid" state
            if (DateTimeUtils.isDateInPast(dateTo)
                    && durationRestriction?.isDateRestrictionApplied == false) {
                isEndDateInPast = true
                callback?.invoke()
            }
        }
        return this
    }

    fun onValid(callback: UiJustCallback): DateToValidator {
        if (isValid) {
            callback.invoke()
        }
        return this
    }
}