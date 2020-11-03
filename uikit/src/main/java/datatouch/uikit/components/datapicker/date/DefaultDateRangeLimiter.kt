/*
 * Copyright (C) 2017 Wouter Dullaert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package datatouch.uikit.components.datapicker.date

import android.os.Parcel
import android.os.Parcelable
import datatouch.uikit.utils.datetime.DatePickerUtils.trimToMidnight
import java.util.*

internal class DefaultDateRangeLimiter : DateRangeLimiter {
    @Transient
    private var mController: DatePickerController? = null
    private var mMinYear = DEFAULT_START_YEAR
    private var mMaxYear = DEFAULT_END_YEAR
    var minDate: Calendar? = null
        private set
    var maxDate: Calendar? = null
        private set
    var selectableDays: TreeSet<Calendar>? =
        TreeSet()
    var disabledDays: HashSet<Calendar>? =
        HashSet()

    constructor() {}
    constructor(`in`: Parcel) {
        mMinYear = `in`.readInt()
        mMaxYear = `in`.readInt()
        minDate = `in`.readSerializable() as Calendar?
        maxDate = `in`.readSerializable() as Calendar?
        selectableDays = `in`.readSerializable() as TreeSet<Calendar>?
        disabledDays = `in`.readSerializable() as HashSet<Calendar>?
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        out.writeInt(mMinYear)
        out.writeInt(mMaxYear)
        out.writeSerializable(minDate)
        out.writeSerializable(maxDate)
        out.writeSerializable(selectableDays)
        out.writeSerializable(disabledDays)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun setController(controller: DatePickerController) {
        mController = controller
    }

    fun setYearRange(startYear: Int, endYear: Int) {
        require(endYear >= startYear) { "Year end must be larger than or equal to year start" }
        mMinYear = startYear
        mMaxYear = endYear
    }

    fun setMinDate(calendar: Calendar) {
        minDate = trimToMidnight((calendar.clone() as Calendar))
    }

    fun setMaxDate(calendar: Calendar) {
        maxDate = trimToMidnight((calendar.clone() as Calendar))
    }

    fun getSelectableDays(): Array<Calendar>? {
        return if (selectableDays!!.isEmpty()) null else selectableDays!!.toTypedArray()
    }

    fun setSelectableDays(days: Array<Calendar?>) {
        for (selectableDay in days) trimToMidnight(
            selectableDay!!
        )
        selectableDays!!.addAll(listOf(*days).map { it!! })
    }

    fun getDisabledDays(): Array<Calendar>? {
        return if (disabledDays!!.isEmpty()) null else disabledDays!!.toTypedArray()
    }

    fun setDisabledDays(days: Array<Calendar?>) {
        for (disabledDay in days) trimToMidnight(disabledDay!!)
        disabledDays!!.addAll(listOf(*days).map { it!! })
    }

    override fun getMinYear(): Int {
        if (!selectableDays!!.isEmpty()) return selectableDays!!.first()[Calendar.YEAR]
        // Ensure no years can be selected outside of the given minimum date
        return if (minDate != null && minDate!![Calendar.YEAR] > mMinYear) minDate!![Calendar.YEAR] else mMinYear
    }

    override fun getMaxYear(): Int {
        if (!selectableDays!!.isEmpty()) return selectableDays!!.last()[Calendar.YEAR]
        // Ensure no years can be selected outside of the given maximum date
        return if (maxDate != null && maxDate!![Calendar.YEAR] < mMaxYear) maxDate!![Calendar.YEAR] else mMaxYear
    }

    override fun getStartDate(): Calendar {
        if (!selectableDays!!.isEmpty()) return selectableDays!!.first().clone() as Calendar
        if (minDate != null) return minDate!!.clone() as Calendar
        val timeZone =
            (if (mController == null) TimeZone.getDefault() else mController!!.timeZone)!!
        val output = Calendar.getInstance(timeZone)
        output[Calendar.YEAR] = mMinYear
        output[Calendar.DAY_OF_MONTH] = 1
        output[Calendar.MONTH] = Calendar.JANUARY
        return output
    }

    override fun getEndDate(): Calendar {
        if (!selectableDays!!.isEmpty()) return selectableDays!!.last().clone() as Calendar
        if (maxDate != null) return maxDate!!.clone() as Calendar
        val timeZone =
            (if (mController == null) TimeZone.getDefault() else mController!!.timeZone)!!
        val output = Calendar.getInstance(timeZone)
        output[Calendar.YEAR] = mMaxYear
        output[Calendar.DAY_OF_MONTH] = 31
        output[Calendar.MONTH] = Calendar.DECEMBER
        return output
    }

    /**
     * @return true if the specified year/month/day are within the selectable days or the range setResources by minDate and maxDate.
     * If one or either have not been setResources, they are considered as Integer.MIN_VALUE and
     * Integer.MAX_VALUE.
     */
    override fun isOutOfRange(year: Int, month: Int, day: Int): Boolean {
        val date = Calendar.getInstance()
        date[Calendar.YEAR] = year
        date[Calendar.MONTH] = month
        date[Calendar.DAY_OF_MONTH] = day
        return isOutOfRange(date)
    }

    private fun isOutOfRange(calendar: Calendar): Boolean {
        trimToMidnight(calendar)
        return isDisabled(calendar) || !isSelectable(calendar)
    }

    private fun isDisabled(c: Calendar): Boolean {
        return disabledDays!!.contains(trimToMidnight(c)) || isBeforeMin(c) || isAfterMax(
            c
        )
    }

    private fun isSelectable(c: Calendar): Boolean {
        return selectableDays!!.isEmpty() || selectableDays!!.contains(trimToMidnight(c))
    }

    private fun isBeforeMin(calendar: Calendar): Boolean {
        return minDate != null && calendar.before(minDate) || calendar[Calendar.YEAR] < mMinYear
    }

    private fun isAfterMax(calendar: Calendar): Boolean {
        return maxDate != null && calendar.after(maxDate) || calendar[Calendar.YEAR] > mMaxYear
    }

    override fun setToNearestDate(calendar: Calendar): Calendar {
        if (!selectableDays!!.isEmpty()) {
            var newCalendar: Calendar? = null
            val higher = selectableDays!!.ceiling(calendar)
            val lower = selectableDays!!.lower(calendar)
            if (higher == null && lower != null) newCalendar =
                lower else if (lower == null && higher != null) newCalendar = higher
            if (newCalendar != null || higher == null) {
                newCalendar = newCalendar ?: calendar
                val timeZone =
                    (if (mController == null) TimeZone.getDefault() else mController!!.timeZone)!!
                newCalendar.timeZone = timeZone
                return newCalendar.clone() as Calendar
            }
            val highDistance =
                Math.abs(higher.timeInMillis - calendar.timeInMillis)
            val lowDistance =
                Math.abs(calendar.timeInMillis - lower!!.timeInMillis)
            return if (lowDistance < highDistance) lower.clone() as Calendar else higher.clone() as Calendar
        }
        if (!disabledDays!!.isEmpty()) {
            val forwardDate =
                (if (isBeforeMin(calendar)) startDate else calendar.clone() as Calendar)
            val backwardDate =
                (if (isAfterMax(calendar)) endDate else calendar.clone() as Calendar)
            while (isDisabled(forwardDate) && isDisabled(backwardDate)) {
                forwardDate.add(Calendar.DAY_OF_MONTH, 1)
                backwardDate.add(Calendar.DAY_OF_MONTH, -1)
            }
            if (!isDisabled(backwardDate)) {
                return backwardDate
            }
            if (!isDisabled(forwardDate)) {
                return forwardDate
            }
        }
        if (minDate != null && isBeforeMin(calendar)) {
            return minDate!!.clone() as Calendar
        }
        return if (maxDate != null && isAfterMax(calendar)) {
            maxDate!!.clone() as Calendar
        } else calendar
    }

    companion object {
        val CREATOR: Parcelable.Creator<DefaultDateRangeLimiter?> =
            object : Parcelable.Creator<DefaultDateRangeLimiter?> {
                override fun createFromParcel(`in`: Parcel): DefaultDateRangeLimiter? {
                    return DefaultDateRangeLimiter(`in`)
                }

                override fun newArray(size: Int): Array<DefaultDateRangeLimiter?> {
                    return arrayOfNulls(size)
                }
            }
        private const val DEFAULT_START_YEAR = 1900
        private const val DEFAULT_END_YEAR = 2100
    }
}