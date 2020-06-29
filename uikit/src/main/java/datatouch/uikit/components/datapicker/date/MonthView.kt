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
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.customview.widget.ExploreByTouchHelper
import datatouch.uikit.R
import datatouch.uikit.components.datapicker.date.MonthAdapter.CalendarDay
import java.security.InvalidParameterException
import java.text.SimpleDateFormat
import java.util.*

/**
 * A calendar-like view displaying a specified month and the appropriate selectable day numbers
 * within the specified month.
 */
abstract class MonthView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    protected var mController: DatePickerController? = null
) : View(context, attr) {
    protected val mDayLabelCalendar: Calendar
    private val mStringBuilder: StringBuilder
    private val mCalendar: Calendar
    private val mTouchHelper: MonthViewTouchHelper

    // affects the padding on the sides of this view
    protected var mEdgePadding = 0
    protected var mMonthNumPaint: Paint? = null
    protected var mMonthTitlePaint: Paint? = null

    protected var mSelectedCirclePaint: Paint? = null
    protected var mMonthDayLabelPaint: Paint? = null

    // The Julian day of the first day displayed by this item
    protected var mFirstJulianDay = -1

    // The month of the first day in this week
    protected var mFirstMonth = -1

    // The month of the last day in this week
    protected var mLastMonth = -1
    var month = 0
        protected set
    var year = 0
        protected set

    // Quick reference to the width of this view, matches parent
    protected var mWidth = 0

    // The height this view should draw at in pixels, setResources by height param
    protected var mRowHeight = DEFAULT_HEIGHT

    // If this view contains the today
    protected var mHasToday = false

    // Which day is selected [0-6] or -1 if no day is selected
    protected var mSelectedDay = -1

    // Which day is today [0-6] or -1 if no day is today
    protected var mToday = DEFAULT_SELECTED_DAY

    // Which day of the week to start on [0-6]
    protected var mWeekStart = DEFAULT_WEEK_START

    // How many days to display
    protected var mNumDays = DEFAULT_NUM_DAYS

    // The number of days + a spot for week number if it is displayed
    protected var mNumCells = DEFAULT_NUM_DAYS
    protected var mMaxCells = DEFAULT_NUM_CELLS

    // The left edge of the selected day
    protected var mSelectedLeft = -1

    // The right edge of the selected day
    protected var mSelectedRight = -1
    protected var mNumRows = DEFAULT_NUM_ROWS

    // Optional listener for handling day click actions
    protected var mOnDayClickListener: OnDayClickListener? =
        null
    protected var mDayTextColor = 0
    protected var mSelectedDayTextColor: Int
    protected var mMonthDayTextColor = 0
    protected var mTodayNumberColor: Int
    protected var mHighlightedDayTextColor = 0
    protected var mDisabledDayTextColor = 0
    protected var mMonthTitleColor: Int
    private val mDayOfWeekTypeface: String
    private val mMonthTitleTypeface: String

    // Whether to prevent setting the accessibility delegate
    private val mLockAccessibilityDelegate: Boolean
    private var weekDayLabelFormatter: SimpleDateFormat? = null
    private var mDayOfWeekStart = 0
    fun setDatePickerController(controller: DatePickerController?) {
        mController = controller
    }

    protected val monthViewTouchHelper: MonthViewTouchHelper
        protected get() = MonthViewTouchHelper(this)

    override fun setAccessibilityDelegate(delegate: AccessibilityDelegate?) {
        // Workaround for a JB MR1 issue where accessibility delegates on
        // top-level ListView items are overwritten.
        if (!mLockAccessibilityDelegate) {
            super.setAccessibilityDelegate(delegate)
        }
    }

    fun setOnDayClickListener(listener: OnDayClickListener?) {
        mOnDayClickListener = listener
    }

    public override fun dispatchHoverEvent(event: MotionEvent): Boolean {
        // First right-of-refusal goes the touch exploration helper.
        return if (mTouchHelper.dispatchHoverEvent(event)) {
            true
        } else super.dispatchHoverEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                val day = getDayFromLocation(event.x, event.y)
                if (day >= 0) {
                    onDayClick(day)
                }
            }
        }
        return true
    }

    /**
     * Sets up the text and style properties for painting. Override this if you
     * want to use a different paint.
     */
    protected fun initView() {
        mMonthTitlePaint = Paint()
        mMonthTitlePaint?.apply {
            mMonthTitlePaint?.isFakeBoldText = true
            mMonthTitlePaint?.isAntiAlias = true
            mMonthTitlePaint?.textSize = MONTH_LABEL_TEXT_SIZE.toFloat()
            mMonthTitlePaint?.typeface = Typeface.create(mMonthTitleTypeface, Typeface.BOLD)
            mMonthTitlePaint?.color = mDayTextColor
            mMonthTitlePaint?.textAlign = Align.CENTER
            mMonthTitlePaint?.style = Paint.Style.FILL
        }
        mSelectedCirclePaint = Paint()
        mSelectedCirclePaint?.apply {
            this.isFakeBoldText = true
            this.isAntiAlias = true
            this.color = mTodayNumberColor
            this.textAlign = Align.CENTER
            this.style = Paint.Style.FILL
            this.alpha = SELECTED_CIRCLE_ALPHA
        }
        mMonthDayLabelPaint = Paint()
        mMonthDayLabelPaint?.apply {
            this.isAntiAlias = true
            this.textSize = MONTH_DAY_LABEL_TEXT_SIZE.toFloat()
            this.color = mMonthDayTextColor
            this.style = Paint.Style.FILL
            this.textAlign = Align.CENTER
            this.isFakeBoldText = true
        }
        mMonthTitlePaint?.typeface = Typeface.create(mDayOfWeekTypeface, Typeface.BOLD)
        mMonthNumPaint = Paint()
        mMonthNumPaint?.apply {
            this.isAntiAlias = true
            this.textSize = MINI_DAY_NUMBER_TEXT_SIZE.toFloat()
            this.style = Paint.Style.FILL
            this.textAlign = Align.CENTER
            this.isFakeBoldText = false
        }
    }

    override fun onDraw(canvas: Canvas) {
        drawMonthTitle(canvas)
        drawMonthDayLabels(canvas)
        drawMonthNums(canvas)
    }

    /**
     * Sets all the parameters for displaying this week. The only required
     * parameter is the week number. Other parameters have a default value and
     * will only update if a new value is included, except for focus month,
     * which will always default to no focus month if no value is passed in.
     */
    fun setMonthParams(selectedDay: Int, year: Int, month: Int, weekStart: Int) {
        if (month == -1 && year == -1) {
            throw InvalidParameterException("You must specify month and year for this view")
        }
        mSelectedDay = selectedDay

        // Allocate space for caching the day numbers and focus values
        this.month = month
        this.year = year

        // Figure out what day today is
        //final Time today = new Time(Time.getCurrentTimezone());
        //today.setToNow();
        val today = Calendar.getInstance(mController!!.timeZone)
        mHasToday = false
        mToday = -1
        mCalendar[Calendar.MONTH] = this.month
        mCalendar[Calendar.YEAR] = this.year
        mCalendar[Calendar.DAY_OF_MONTH] = 1
        mDayOfWeekStart = mCalendar[Calendar.DAY_OF_WEEK]
        mWeekStart = if (weekStart != -1) {
            weekStart
        } else {
            mCalendar.firstDayOfWeek
        }
        mNumCells = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 0 until mNumCells) {
            val day = i + 1
            if (sameDay(day, today)) {
                mHasToday = true
                mToday = day
            }
        }

        // Invalidate cached accessibility information.
        mTouchHelper.invalidateRoot()
    }

    fun setSelectedDay(day: Int) {
        mSelectedDay = day
    }

    private fun calculateNumRows(): Int {
        val offset = findDayOffset()
        val dividend = (offset + mNumCells) / mNumDays
        val remainder = (offset + mNumCells) % mNumDays
        return dividend + if (remainder > 0) 1 else 0
    }

    private fun sameDay(day: Int, today: Calendar): Boolean {
        return year == today[Calendar.YEAR] && month == today[Calendar.MONTH] && day == today[Calendar.DAY_OF_MONTH]
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            mRowHeight * mNumRows + monthHeaderSize + 5
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mWidth = w

        // Invalidate cached accessibility information.
        mTouchHelper.invalidateRoot()
    }

    private val monthAndYearString: String
        private get() {
            val locale = Locale.getDefault()
            var pattern: String? = "MMMM yyyy"
            pattern = if (Build.VERSION.SDK_INT < 18) {
                context.resources.getString(R.string.amdp_date_v1_monthyear)
            } else {
                DateFormat.getBestDateTimePattern(locale, pattern)
            }
            val formatter = SimpleDateFormat(pattern, locale)
            formatter.timeZone = mController!!.timeZone
            formatter.applyLocalizedPattern(pattern)
            mStringBuilder.setLength(0)
            val string = formatter.format(mCalendar.time)
            return string.substring(0, 1).toUpperCase() + string.substring(1)
        }

    protected fun drawMonthTitle(canvas: Canvas) {
        val x = (mWidth + 2 * mEdgePadding) / 2
        val y = (monthHeaderSize - MONTH_DAY_LABEL_TEXT_SIZE) / 2
        canvas.drawText(monthAndYearString, x.toFloat(), y.toFloat(), mMonthTitlePaint!!)
    }

    protected fun drawMonthDayLabels(canvas: Canvas) {
        val y = monthHeaderSize - MONTH_DAY_LABEL_TEXT_SIZE / 2
        val dayWidthHalf = (mWidth - mEdgePadding * 2) / (mNumDays * 2)
        for (i in 0 until mNumDays) {
            val x = (2 * i + 1) * dayWidthHalf + mEdgePadding
            val calendarDay = (i + mWeekStart) % mNumDays
            mDayLabelCalendar[Calendar.DAY_OF_WEEK] = calendarDay
            val weekString = getWeekDayLabel(mDayLabelCalendar)
            canvas.drawText(weekString, x.toFloat(), y.toFloat(), mMonthDayLabelPaint!!)
        }
    }

    /**
     * Draws the week and month day numbers for this week. Override this method
     * if you need different placement.
     *
     * @param canvas The canvas to draw on
     */
    protected fun drawMonthNums(canvas: Canvas?) {
        var y =
            ((mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2 - DAY_SEPARATOR_WIDTH
                    + monthHeaderSize)
        val dayWidthHalf = (mWidth - mEdgePadding * 2) / (mNumDays * 2.0f)
        var j = findDayOffset()
        for (dayNumber in 1..mNumCells) {
            val x = ((2 * j + 1) * dayWidthHalf + mEdgePadding).toInt()
            val yRelativeToDay =
                (mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2 - DAY_SEPARATOR_WIDTH
            val startX = (x - dayWidthHalf).toInt()
            val stopX = (x + dayWidthHalf).toInt()
            val startY = (y - yRelativeToDay)
            drawMonthDay(
                canvas,
                year,
                month,
                dayNumber,
                x,
                y,
                startX,
                stopX,
                startY,
                (startY + mRowHeight)
            )
            j++
            if (j == mNumDays) {
                j = 0
                y += mRowHeight
            }
        }
    }

    /**
     * This method should draw the month day.  Implemented by sub-classes to allow customization.
     *
     * @param canvas The canvas to draw on
     * @param year   The year of this month day
     * @param month  The month of this month day
     * @param day    The day number of this month day
     * @param x      The default x position to draw the day number
     * @param y      The default y position to draw the day number
     * @param startX The left boundary of the day number rect
     * @param stopX  The right boundary of the day number rect
     * @param startY The top boundary of the day number rect
     * @param stopY  The bottom boundary of the day number rect
     */
    abstract fun drawMonthDay(
        canvas: Canvas?, year: Int, month: Int, day: Int,
        x: Int, y: Int, startX: Int, stopX: Int, startY: Int, stopY: Int
    )

    protected fun findDayOffset(): Int {
        return ((if (mDayOfWeekStart < mWeekStart) mDayOfWeekStart + mNumDays else mDayOfWeekStart)
                - mWeekStart)
    }

    /**
     * Calculates the day that the given x position is in, accounting for week
     * number. Returns the day or -1 if the position wasn't in a day.
     *
     * @param x The x position of the touch event
     * @return The day number, or -1 if the position wasn't in a day
     */
    fun getDayFromLocation(x: Float, y: Float): Int {
        val day = getInternalDayFromLocation(x, y)
        return if (day < 1 || day > mNumCells) {
            -1
        } else day
    }

    /**
     * Calculates the day that the given x position is in, accounting for week
     * number.
     *
     * @param x The x position of the touch event
     * @return The day number
     */
    protected fun getInternalDayFromLocation(x: Float, y: Float): Int {
        val dayStart = mEdgePadding
        if (x < dayStart || x > mWidth - mEdgePadding) {
            return -1
        }
        // Selection is (x - start) / (pixels/day) == (x -s) * day / pixels
        val row = (y - monthHeaderSize).toInt() / mRowHeight
        val column = ((x - dayStart) * mNumDays / (mWidth - dayStart - mEdgePadding)).toInt()
        var day = column - findDayOffset() + 1
        day += row * mNumDays
        return day
    }

    /**
     * Called when the user clicks on a day. Handles callbacks to the
     * [OnDayClickListener] if one is setResources.
     *
     *
     * If the day is out of the range setResources by minDate and/or maxDate, this is a no-op.
     *
     * @param day The day that was clicked
     */
    private fun onDayClick(day: Int) {
        // If the min / max date are setResources, only process the click if it's a valid selection.
        if (mController!!.isOutOfRange(year, month, day)) {
            return
        }
        if (mOnDayClickListener != null) {
            mOnDayClickListener?.onDayClick(this, CalendarDay(year, month, day))
        }

        // This is a no-op if accessibility is turned off.
        mTouchHelper.sendEventForVirtualView(day, AccessibilityEvent.TYPE_VIEW_CLICKED)
    }

    /**
     * @param year
     * @param month
     * @param day
     * @return true if the given date should be highlighted
     */
    protected fun isHighlighted(year: Int, month: Int, day: Int): Boolean {
        return mController!!.isHighlighted(year, month, day)
    }

    /**
     * Return a 1 or 2 letter String for use as a weekday label
     *
     * @param day The day for which to generate a label
     * @return The weekday label
     */
    private fun getWeekDayLabel(day: Calendar): String {
        val locale = Locale.getDefault()

        // Localised short version of the string is not available on API < 18
        if (Build.VERSION.SDK_INT < 18) {
            val dayName =
                SimpleDateFormat("E", locale).format(day.time)
            var dayLabel = dayName.toUpperCase(locale).substring(0, 1)

            // Chinese labels should be fetched right to left
            if (locale == Locale.CHINA || locale == Locale.CHINESE || locale == Locale.SIMPLIFIED_CHINESE || locale == Locale.TRADITIONAL_CHINESE) {
                val len = dayName.length
                dayLabel = dayName.substring(len - 1, len)
            }

            // Most hebrew labels should select the second to last character
            if (locale.language == "he" || locale.language == "iw") {
                dayLabel = if (mDayLabelCalendar[Calendar.DAY_OF_WEEK] != Calendar.SATURDAY) {
                    val len = dayName.length
                    dayName.substring(len - 2, len - 1)
                } else {
                    // I know this is duplication, but it makes the code easier to grok by
                    // having all hebrew code in the same block
                    dayName.toUpperCase(locale).substring(0, 1)
                }
            }

            // Catalan labels should be two digits in lowercase
            if (locale.language == "ca") dayLabel = dayName.toLowerCase().substring(0, 2)

            // Correct single character label in Spanish is X
            if (locale.language == "es" && day[Calendar.DAY_OF_WEEK] == Calendar.WEDNESDAY) dayLabel =
                "X"
            return dayLabel
        }
        // Getting the short label is a one liner on API >= 18
        if (weekDayLabelFormatter == null) {
            weekDayLabelFormatter = SimpleDateFormat("EEEEE", locale)
        }
        return weekDayLabelFormatter!!.format(day.time)
    }

    /**
     * @return The date that has accessibility focus, or `null` if no date
     * has focus
     */
    val accessibilityFocus: CalendarDay?
        get() {
            val day = mTouchHelper.focusedVirtualView
            return if (day >= 0) {
                CalendarDay(year, month, day)
            } else null
        }

    /**
     * Clears accessibility focus within the view. No-op if the view does not
     * contain accessibility focus.
     */
    fun clearAccessibilityFocus() {
        mTouchHelper.clearFocusedVirtualView()
    }

    /**
     * Attempts to restore accessibility focus to the specified date.
     *
     * @param day The date which should receive focus
     * @return `false` if the date is not valid for this month view, or
     * `true` if the date received focus
     */
    fun restoreAccessibilityFocus(day: CalendarDay): Boolean {
        if (day.year != year || day.month != month || day.day > mNumCells) {
            return false
        }
        mTouchHelper.focusedVirtualView = day.day
        return true
    }

    /**
     * Handles callbacks when the user clicks on a time object.
     */
    interface OnDayClickListener {
        fun onDayClick(view: MonthView?, day: CalendarDay?)
    }

    /**
     * Provides a virtual view hierarchy for interfacing with an accessibility
     * service.
     */
    protected inner class MonthViewTouchHelper(host: View?) :
        ExploreByTouchHelper(host!!) {
        private val mTempRect = Rect()
        private val mTempCalendar =
            Calendar.getInstance(mController!!.timeZone)

        fun setFocusedVirtualView(virtualViewId: Int) {
            getAccessibilityNodeProvider(this@MonthView).performAction(
                virtualViewId, AccessibilityNodeInfoCompat.ACTION_ACCESSIBILITY_FOCUS, null
            )
        }

        fun clearFocusedVirtualView() {
            val focusedVirtualView = focusedVirtualView
            if (focusedVirtualView != INVALID_ID) {
                getAccessibilityNodeProvider(this@MonthView).performAction(
                    focusedVirtualView,
                    AccessibilityNodeInfoCompat.ACTION_CLEAR_ACCESSIBILITY_FOCUS,
                    null
                )
            }
        }

        override fun getVirtualViewAt(x: Float, y: Float): Int {
            val day = getDayFromLocation(x, y)
            return if (day >= 0) {
                day
            } else INVALID_ID
        }

        override fun getVisibleVirtualViews(virtualViewIds: MutableList<Int>) {
            for (day in 1..mNumCells) {
                virtualViewIds.add(day)
            }
        }

        override fun onPopulateEventForVirtualView(
            virtualViewId: Int,
            event: AccessibilityEvent
        ) {
            event.contentDescription = getItemDescription(virtualViewId)
        }

        override fun onPopulateNodeForVirtualView(
            virtualViewId: Int,
            node: AccessibilityNodeInfoCompat
        ) {
            getItemBounds(virtualViewId, mTempRect)
            node.contentDescription = getItemDescription(virtualViewId)
            node.setBoundsInParent(mTempRect)
            node.addAction(AccessibilityNodeInfo.ACTION_CLICK)
            if (virtualViewId == mSelectedDay) {
                node.isSelected = true
            }
        }

        override fun onPerformActionForVirtualView(
            virtualViewId: Int, action: Int,
            arguments: Bundle?
        ): Boolean {
            when (action) {
                AccessibilityNodeInfo.ACTION_CLICK -> {
                    onDayClick(virtualViewId)
                    return true
                }
            }
            return false
        }

        /**
         * Calculates the bounding rectangle of a given time object.
         *
         * @param day  The day to calculate bounds for
         * @param rect The rectangle in which to store the bounds
         */
        protected fun getItemBounds(day: Int, rect: Rect) {
            val offsetX = mEdgePadding
            val offsetY = monthHeaderSize
            val cellHeight = mRowHeight
            val cellWidth = (mWidth - 2 * mEdgePadding) / mNumDays
            val index = day - 1 + findDayOffset()
            val row = index / mNumDays
            val column = index % mNumDays
            val x = offsetX + column * cellWidth
            val y = offsetY + row * cellHeight
            rect[x, y, x + cellWidth] = y + cellHeight
        }

        /**
         * Generates a description for a given time object. Since this
         * description will be spoken, the components are ordered by descending
         * specificity as DAY MONTH YEAR.
         *
         * @param day The day to generate a description for
         * @return A description of the time object
         */
        protected fun getItemDescription(day: Int): CharSequence {
            mTempCalendar[year, month] = day
            val date = DateFormat.format(
                Companion.DATE_FORMAT,
                mTempCalendar.timeInMillis
            )
            return if (day == mSelectedDay) {
                context.getString(R.string.amdp_item_is_selected, date)
            } else date
        }


    }

    companion object {
        const val DATE_FORMAT = "dd MMMM yyyy"
        protected const val DEFAULT_SELECTED_DAY = -1
        protected const val DEFAULT_WEEK_START = Calendar.SUNDAY
        protected const val DEFAULT_NUM_DAYS = 7
        protected const val DEFAULT_SHOW_WK_NUM = 0
        protected const val DEFAULT_FOCUS_MONTH = -1
        protected const val DEFAULT_NUM_ROWS = 6
        protected const val MAX_NUM_ROWS = 6
        protected const val DEFAULT_NUM_CELLS =
            DEFAULT_NUM_DAYS * DEFAULT_NUM_ROWS
        private const val SELECTED_CIRCLE_ALPHA = 255
        @JvmStatic
        protected var DEFAULT_HEIGHT = 32
        @JvmStatic
        protected var MIN_HEIGHT = 10
        @JvmStatic
        protected var DAY_SEPARATOR_WIDTH = 1
        @JvmStatic
        protected var MINI_DAY_NUMBER_TEXT_SIZE: Int = 0
        @JvmStatic
        protected var MONTH_LABEL_TEXT_SIZE: Int = 0
        @JvmStatic
        protected var MONTH_DAY_LABEL_TEXT_SIZE: Int = 0

        /**
         * A wrapper to the MonthHeaderSize to allow override it in children
         */
        @JvmStatic
        protected var monthHeaderSize: Int = 0
        @JvmStatic
        protected var DAY_SELECTED_CIRCLE_SIZE: Int = 0
        @JvmStatic
        protected var DAY_HIGHLIGHT_CIRCLE_SIZE: Int = 0
        @JvmStatic
        protected var DAY_HIGHLIGHT_CIRCLE_MARGIN: Int = 0

        // used for scaling to the device density
        @JvmStatic
        protected var mScale = 0f
    }

    init {
        val res = context.resources
        mDayLabelCalendar = Calendar.getInstance(mController!!.timeZone)
        mCalendar = Calendar.getInstance(mController!!.timeZone)
        mDayOfWeekTypeface = res.getString(R.string.amdp_day_of_week_label_typeface)
        mMonthTitleTypeface = res.getString(R.string.amdp_sans_serif)
        val darkTheme = mController != null && mController!!.isThemeDark
        if (darkTheme) {
            mDayTextColor =
                ContextCompat.getColor(context, R.color.amdp_date_picker_text_normal_dark_theme)
            mMonthDayTextColor =
                ContextCompat.getColor(context, R.color.amdp_date_picker_month_day_dark_theme)
            mDisabledDayTextColor =
                ContextCompat.getColor(context, R.color.amdp_date_picker_text_disabled_dark_theme)
            mHighlightedDayTextColor = ContextCompat.getColor(
                context,
                R.color.amdp_date_picker_text_highlighted_dark_theme
            )
        } else {
            mDayTextColor = ContextCompat.getColor(context, R.color.amdp_date_picker_text_normal)
            mMonthDayTextColor = ContextCompat.getColor(context, R.color.amdp_date_picker_month_day)
            mDisabledDayTextColor =
                ContextCompat.getColor(context, R.color.amdp_date_picker_text_disabled)
            mHighlightedDayTextColor =
                ContextCompat.getColor(context, R.color.amdp_date_picker_text_highlighted)
        }
        mSelectedDayTextColor = ContextCompat.getColor(context, R.color.amdp_white)
        mTodayNumberColor = mController!!.accentColor
        mMonthTitleColor = ContextCompat.getColor(context, R.color.amdp_white)
        mStringBuilder = StringBuilder(50)
        MINI_DAY_NUMBER_TEXT_SIZE =
            res.getDimensionPixelSize(R.dimen.amdp_day_number_size)
        MONTH_LABEL_TEXT_SIZE =
            res.getDimensionPixelSize(R.dimen.amdp_month_label_size)
        MONTH_DAY_LABEL_TEXT_SIZE =
            res.getDimensionPixelSize(R.dimen.amdp_month_day_label_text_size)
        monthHeaderSize =
            res.getDimensionPixelOffset(R.dimen.amdp_month_list_item_header_height)
        DAY_SELECTED_CIRCLE_SIZE = res
            .getDimensionPixelSize(R.dimen.amdp_day_number_select_circle_radius)
        DAY_HIGHLIGHT_CIRCLE_SIZE = res
            .getDimensionPixelSize(R.dimen.amdp_day_highlight_circle_radius)
        DAY_HIGHLIGHT_CIRCLE_MARGIN = res
            .getDimensionPixelSize(R.dimen.amdp_day_highlight_circle_margin)
        mRowHeight = (res.getDimensionPixelOffset(R.dimen.amdp_date_picker_view_animator_height)
                - monthHeaderSize) / MAX_NUM_ROWS

        // Set up accessibility components.
        mTouchHelper = monthViewTouchHelper
        ViewCompat.setAccessibilityDelegate(this, mTouchHelper)
        ViewCompat.setImportantForAccessibility(this, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
        mLockAccessibilityDelegate = true

        // Sets up any standard paints that will be used
        initView()
    }
}