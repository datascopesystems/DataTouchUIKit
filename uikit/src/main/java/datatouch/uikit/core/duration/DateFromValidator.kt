package datatouch.uikit.core.duration

import datatouch.uikit.core.callbacks.UiJustCallback
import datatouch.uikit.core.utils.datetime.DateTimeUtils
import java.util.*


class DateFromValidator(private val durationRestriction: IDurationRestriction?,
                        private val dateFrom: Date) {

    private var isDateOutsideOfRestriction = false
    private var isStartDateRestricted = false
    private var isStartDateInPast = false

    val isValid: Boolean
        get() = !isDateOutsideOfRestriction && !isStartDateRestricted && !isStartDateInPast

    fun isDateOutsideOfRestriction(callback: UiJustCallback? = null): DateFromValidator {
        if (isValid) { // Prevent checks and callback if we already got "invalid" state
            if (durationRestriction?.isDateRestrictionApplied == true
                    && durationRestriction.isSelectedDateAfterRestrictionEndDate(dateFrom)) {
                isDateOutsideOfRestriction = true
                callback?.invoke()
            }
        }
        return this
    }

    fun isStartDateRestricted(callback: UiJustCallback? = null): DateFromValidator {
        if (isValid) { // Prevent checks and callback if we already got "invalid" state
            if (durationRestriction?.isStartDateRestrictionApplied(dateFrom) == true) {
                isStartDateRestricted = true
                callback?.invoke()
            }
        }
        return this
    }

    fun isStartDateInPast(callback: UiJustCallback? = null): DateFromValidator {
        if (isValid) { // Prevent checks and callback if we already got "invalid" state
            if (DateTimeUtils.isDateInPast(dateFrom)
                    && durationRestriction?.isDateRestrictionApplied == false) {
                isStartDateInPast = true
                callback?.invoke()
            }
        }
        return this
    }

    fun onValid(callback: UiJustCallback? = null): DateFromValidator {
        if (isValid) {
            callback?.invoke()
        }
        return this
    }
}