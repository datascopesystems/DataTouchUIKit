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

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import datatouch.uikit.components.datapicker.DatePickerUtils.tryAccessibilityAnnounce
import datatouch.uikit.components.datapicker.GravitySnapHelper
import datatouch.uikit.components.datapicker.date.MonthAdapter.CalendarDay
import java.text.SimpleDateFormat
import java.util.*

/**
 * This displays a list of months in a calendar format with selectable days.
 */
abstract class DayPickerView : RecyclerView,
    DatePickerFragmentDialog.OnDateChangedListener {
    protected var mNumWeeks = 6
    protected var mShowWeekNumber = false
    protected var mDaysPerWeek = 7
    protected var mContext: Context? = null
    protected var mHandler: Handler? = null

    // highlighted time
    protected var mSelectedDay: CalendarDay? = null
    protected var mAdapter: MonthAdapter? = null
    protected var mTempDay: CalendarDay? = null

    // When the week starts; numbered like Time.<WEEKDAY> (e.g. SUNDAY=0).
    protected var mFirstDayOfWeek = 0

    // The last description announced by accessibility
    protected var mPrevMonthName: CharSequence? = null

    // which month should be displayed/highlighted [0-11]
    protected var mCurrentMonthDisplayed = 0

    // used for tracking during a scroll
    protected var mPreviousScrollPosition: Long = 0

    // used for tracking what state listview is in
    protected var mPreviousScrollState = SCROLL_STATE_IDLE
    private var mController: DatePickerController? = null
    private var linearLayoutManager: LinearLayoutManager? = null

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!,
        attrs
    ) {
        init(context)
    }

    constructor(context: Context?, controller: DatePickerController?) : super(
        context!!
    ) {
        init(context)
        setController(controller)
    }

    fun setController(controller: DatePickerController?) {
        mController = controller
        mController!!.registerOnDateChangedListener(this)
        mSelectedDay = CalendarDay(mController!!.timeZone)
        mTempDay = CalendarDay(mController!!.timeZone)
        refreshAdapter()
        onDateChanged()
    }

    fun init(context: Context?) {
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        layoutManager = linearLayoutManager
        mHandler = Handler()
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
        mContext = context
        setUpRecyclerView()
    }

    fun setScrollOrientation(orientation: Int) {
        linearLayoutManager!!.orientation = orientation
    }

    /**
     * Sets all the required fields for the list view. Override this method to
     * setResources a different list view behavior.
     */
    protected fun setUpRecyclerView() {
        isVerticalScrollBarEnabled = false
        setFadingEdgeLength(0)
        val helper = GravitySnapHelper(Gravity.TOP)
        helper.attachToRecyclerView(this)
    }

    fun onChange() {
        refreshAdapter()
    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {
        super.onLayout(changed, l, t, r, b)
        val focusedDay = findAccessibilityFocus()
        restoreAccessibilityFocus(focusedDay)
    }

    /**
     * Creates a new adapter if necessary and sets up its parameters. Override
     * this method to provide a custom adapter.
     */
    protected fun refreshAdapter() {
        if (mAdapter == null) {
            mAdapter = createMonthAdapter(mController)
        } else {
            mAdapter!!.selectedDay = mSelectedDay
        }
        // refresh the view with the new parameters
        adapter = mAdapter
    }

    abstract fun createMonthAdapter(controller: DatePickerController?): MonthAdapter?

    /**
     * This moves to the specified time in the view. If the time is not already
     * in range it will move the list so that the first of the month containing
     * the time is at the top of the view. If the new time is already in view
     * the list will not be scrolled unless forceScroll is true. This time may
     * optionally be highlighted as selected as well.
     *
     * @param day         The day to move to
     * @param animate     Whether to scroll to the given time or just redraw at the
     * new location
     * @param setSelected Whether to setResources the given time as selected
     * @param forceScroll Whether to recenter even if the time is already
     * visible
     * @return Whether or not the view animated to the new location
     */
    fun goTo(
        day: CalendarDay?,
        animate: Boolean,
        setSelected: Boolean,
        forceScroll: Boolean
    ): Boolean {

        // Set the selected day
        if (setSelected) {
            mSelectedDay?.set(day!!)
        }
        mTempDay!!.set(day!!)
        val minMonth = mController?.startDate!![Calendar.MONTH]
        val position =
            day.year - mController?.minYear!! * MonthAdapter.MONTHS_IN_YEAR + day.month - minMonth
        var child: View?
        var i = 0
        var top = 0
        // Find a child that's completely in the view
        do {
            child = getChildAt(i++)
            if (child == null) {
                break
            }
            top = child.top
        } while (top < 0)

        // Compute the first and last position visible
        val selectedPosition = child?.let { getChildAdapterPosition(it) } ?: 0
        if (setSelected) {
            mAdapter!!.selectedDay = mSelectedDay
        }


        // Check if the selected day is now outside of our visible range
        // and if so scroll to the month that contains it
        if (position != selectedPosition || forceScroll) {
            setMonthDisplayed(mTempDay)
            mPreviousScrollState = SCROLL_STATE_DRAGGING
            if (animate) {
                smoothScrollToPosition(position)
                return true
            } else {
                postSetSelection(position)
            }
        } else if (setSelected) {
            setMonthDisplayed(mSelectedDay)
        }
        return false
    }

    fun scrollToMonth(month: Int) {
        val minMonth = mController!!.startDate!![Calendar.MONTH]
        val position =
            mSelectedDay!!.year - mController!!.minYear * MonthAdapter.MONTHS_IN_YEAR + month - minMonth
        smoothScrollToPosition(position)
    }

    fun scrollToNextMonth() {
        val currentPos =
            (layoutManager as LinearLayoutManager?)!!.findFirstCompletelyVisibleItemPosition()
        if (currentPos < adapter!!.itemCount) {
            smoothScrollToPosition(currentPos + 1)
        }
    }

    fun scrollToPrevMonth() {
        val currentPos =
            (layoutManager as LinearLayoutManager?)!!.findFirstCompletelyVisibleItemPosition()
        if (currentPos > 0) {
            smoothScrollToPosition(currentPos - 1)
        }
    }

    fun postSetSelection(position: Int) {
        clearFocus()
        post { (layoutManager as LinearLayoutManager?)!!.scrollToPositionWithOffset(position, 0) }
    }

    /**
     * Sets the month displayed at the top of this view based on time. Override
     * to add custom events when the title is changed.
     */
    protected fun setMonthDisplayed(date: CalendarDay?) {
        mCurrentMonthDisplayed = date!!.month
    }

    /**
     * Gets the position of the view that is most prominently displayed within the list.
     */
    val mostVisiblePosition: Int
        get() = getChildAdapterPosition(mostVisibleMonth!!)

    val mostVisibleMonth: MonthView?
        get() {
            val verticalScroll =
                (layoutManager as LinearLayoutManager?)!!.orientation == LinearLayoutManager.VERTICAL
            val maxSize = if (verticalScroll) height else width
            var maxDisplayedSize = 0
            var i = 0
            var size = 0
            var mostVisibleMonth: MonthView? = null
            while (size < maxSize) {
                val child = getChildAt(i) ?: break
                size = if (verticalScroll) child.bottom else right
                val displayedSize =
                    Math.min(size, maxSize) - Math.max(0, child.top)
                if (displayedSize > maxDisplayedSize) {
                    mostVisibleMonth = child as MonthView
                    maxDisplayedSize = displayedSize
                }
                i++
            }
            return mostVisibleMonth
        }

    override fun onDateChanged() {
        goTo(mController!!.selectedDay, false, true, true)
    }

    /**
     * Attempts to return the date that has accessibility focus.
     *
     * @return The date that has accessibility focus, or `null` if no date
     * has focus.
     */
    private fun findAccessibilityFocus(): CalendarDay? {
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is MonthView) {
                val focus = child.accessibilityFocus
                if (focus != null) {
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        // Clear focus to avoid ListView bug in Jelly Bean MR1.
                        child.clearAccessibilityFocus()
                    }
                    return focus
                }
            }
        }
        return null
    }

    /**
     * Attempts to restore accessibility focus to a given date. No-op if
     * `day` is `null`.
     *
     * @param day The date that should receive accessibility focus
     * @return `true` if focus was restored
     */
    private fun restoreAccessibilityFocus(day: CalendarDay?): Boolean {
        if (day == null) {
            return false
        }
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is MonthView) {
                if (child.restoreAccessibilityFocus(day)) {
                    return true
                }
            }
        }
        return false
    }

    override fun onInitializeAccessibilityEvent(event: AccessibilityEvent) {
        super.onInitializeAccessibilityEvent(event)
        event.itemCount = -1
    }

    /**
     * Necessary for accessibility, to ensure we support "scrolling" forward and backward
     * in the month list.
     */
    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        if (Build.VERSION.SDK_INT >= 21) {
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD)
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD)
        } else {
            info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
            info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)
        }
    }

    /**
     * When scroll forward/backward events are received, announce the newly scrolled-to month.
     */
    @SuppressLint("NewApi")
    override fun performAccessibilityAction(action: Int, arguments: Bundle): Boolean {
        if (action != AccessibilityNodeInfo.ACTION_SCROLL_FORWARD &&
            action != AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
        ) {
            return super.performAccessibilityAction(action, arguments)
        }
        // Figure out what month is showing.
        val firstVisiblePosition = firstVisiblePosition
        val minMonth = mController!!.startDate!![Calendar.MONTH]
        val month =
            (firstVisiblePosition + minMonth) % MonthAdapter.MONTHS_IN_YEAR
        val year =
            (firstVisiblePosition + minMonth) / MonthAdapter.MONTHS_IN_YEAR + mController!!.minYear
        val day = CalendarDay(year, month, 1)

        // Scroll either forward or backward one month.
        if (action == AccessibilityNodeInfo.ACTION_SCROLL_FORWARD) {
            day.month++
            if (day.month == 12) {
                day.month = 0
                day.year++
            }
        } else if (action == AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD) {
            val firstVisibleView = getChildAt(0)
            // If the view is fully visible, jump one month back. Otherwise, we'll just jump
            // to the first day of first visible month.
            if (firstVisibleView != null && firstVisibleView.top >= -1) {
                // There's an off-by-one somewhere, so the top of the first visible item will
                // actually be -1 when it's at the exact top.
                day.month--
                if (day.month == -1) {
                    day.month = 11
                    day.year--
                }
            }
        }

        // Go to that month.
        tryAccessibilityAnnounce(
            this,
            getMonthAndYearString(day)
        )
        goTo(day, true, false, true)
        return true
    }

    private val firstVisiblePosition: Int
        private get() = getChildAdapterPosition(getChildAt(0))

    companion object {
        // The number of days to display in each week
        const val DAYS_PER_WEEK = 7

        // Affects when the month selection will change while scrolling up
        protected const val SCROLL_HYST_WEEKS = 2
        private const val TAG = "MonthFragment"
        private val YEAR_FORMAT =
            SimpleDateFormat("yyyy", Locale.getDefault())

        private fun getMonthAndYearString(day: CalendarDay): String {
            val cal = Calendar.getInstance()
            cal[day.year, day.month] = day.day
            var sbuf = ""
            sbuf += cal.getDisplayName(
                Calendar.MONTH,
                Calendar.LONG,
                Locale.getDefault()
            )
            sbuf += " "
            sbuf += YEAR_FORMAT.format(cal.time)
            return sbuf
        }
    }
}