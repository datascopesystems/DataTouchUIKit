/*
 * Copyright (C) 2013 The Android Open Source Project
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

import android.content.Context
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.recyclerview.widget.RecyclerView
import datatouch.uikit.components.datapicker.date.MonthAdapter.MonthViewHolder
import java.util.*

abstract class MonthAdapter(protected val mController: DatePickerController) :
    RecyclerView.Adapter<MonthViewHolder>(),
    MonthView.OnDayClickListener {
    private var mSelectedDay: CalendarDay? = null

    /**
     * Updates the selected day and related parameters.
     *
     * @param day The day to highlight
     */
    var selectedDay: CalendarDay?
        get() = mSelectedDay
        set(day) {
            mSelectedDay = day
            notifyDataSetChanged()
        }

    /**
     * Set up the gesture detector and selected time
     */
    protected fun init() {
        mSelectedDay = CalendarDay(System.currentTimeMillis(), mController.timeZone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val v = createMonthView(parent.context)
        // Set up the new view
        val params = AbsListView.LayoutParams(
            AbsListView.LayoutParams.MATCH_PARENT,
            AbsListView.LayoutParams.MATCH_PARENT
        )
        v.layoutParams = params
        v.isClickable = true
        v.setOnDayClickListener(this)
        return MonthViewHolder(v)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        holder.bind(position, mController, mSelectedDay)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        val endDate = mController.endDate
        val startDate = mController.startDate
        val endMonth =
            endDate!![Calendar.YEAR] * MONTHS_IN_YEAR + endDate[Calendar.MONTH]
        val startMonth =
            startDate!![Calendar.YEAR] * MONTHS_IN_YEAR + startDate[Calendar.MONTH]
        return endMonth - startMonth + 1
        //return ((mController.getMaxYear() - mController.getMinYear()) + 1) * MONTHS_IN_YEAR;
    }

    abstract fun createMonthView(context: Context?): MonthView
    override fun onDayClick(view: MonthView?, day: CalendarDay?) {
        day?.let { onDayTapped(it) }
    }

    /**
     * Maintains the same hour/min/sec but moves the day to the tapped day.
     *
     * @param day The day that was tapped
     */
    protected fun onDayTapped(day: CalendarDay) {
        mController.onDayOfMonthSelected(day.year, day.month, day.day)
        selectedDay = day
    }

    /**
     * A convenience class to represent a specific date.
     */
    class CalendarDay {
        var year = 0
        var month = 0
        var day = 0
        var mTimeZone: TimeZone? = null
        private var calendar: Calendar? = null

        constructor(timeZone: TimeZone?) {
            mTimeZone = timeZone
            setTime(System.currentTimeMillis())
        }

        constructor(timeInMillis: Long, timeZone: TimeZone?) {
            mTimeZone = timeZone
            setTime(timeInMillis)
        }

        constructor(calendar: Calendar, timeZone: TimeZone?) {
            mTimeZone = timeZone
            year = calendar[Calendar.YEAR]
            month = calendar[Calendar.MONTH]
            day = calendar[Calendar.DAY_OF_MONTH]
        }

        constructor(year: Int, month: Int, day: Int) {
            setDay(year, month, day)
        }

        fun set(date: CalendarDay) {
            year = date.year
            month = date.month
            day = date.day
        }

        fun setDay(year: Int, month: Int, day: Int) {
            this.year = year
            this.month = month
            this.day = day
        }

        private fun setTime(timeInMillis: Long) {
            if (calendar == null) {
                calendar = Calendar.getInstance(mTimeZone)
            }
            calendar!!.timeInMillis = timeInMillis
            month = calendar!![Calendar.MONTH]
            year = calendar!![Calendar.YEAR]
            day = calendar!![Calendar.DAY_OF_MONTH]
        }

    }

    class MonthViewHolder(itemView: MonthView?) :
        RecyclerView.ViewHolder(itemView!!) {
        fun bind(
            position: Int,
            mController: DatePickerController,
            selectedCalendarDay: CalendarDay?
        ) {
            val month =
                (position + mController.startDate!![Calendar.MONTH]) % MONTHS_IN_YEAR
            val year =
                (position + mController.startDate!![Calendar.MONTH]) / MONTHS_IN_YEAR + mController.minYear
            var selectedDay = -1
            if (isSelectedDayInMonth(selectedCalendarDay, year, month)) {
                selectedDay = selectedCalendarDay!!.day
            }
            (itemView as MonthView).setMonthParams(
                selectedDay,
                year,
                month,
                mController.firstDayOfWeek
            )
            itemView.invalidate()
        }

        private fun isSelectedDayInMonth(
            selectedDay: CalendarDay?,
            year: Int,
            month: Int
        ): Boolean {
            return selectedDay!!.year == year && selectedDay.month == month
        }
    }

    companion object {
        const val MONTHS_IN_YEAR = 12
        protected var WEEK_7_OVERHANG_HEIGHT = 7
    }

    init {
        init()
        selectedDay = mController.selectedDay
        setHasStableIds(true)
    }
}