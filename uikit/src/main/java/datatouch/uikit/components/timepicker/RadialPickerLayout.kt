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
package datatouch.uikit.components.timepicker

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.format.DateUtils
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import datatouch.uikit.R
import java.util.*

/**
 * The primary layout to hold the circular picker, and the am/pm buttons. This view will measure
 * itself to end up as a square. It also handles touches to be passed in to views that need to know
 * when they'd been touched.
 */
class RadialPickerLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs), OnTouchListener {
    private val TOUCH_SLOP: Int
    private val TAP_TIMEOUT: Int
    private var mLastValueSelected: TimePoint?
    private var mController: TimePickerController? = null
    private var mListener: OnValueSelectedListener? = null
    private var mTimeInitialized: Boolean
    private var mCurrentTime: TimePoint? = null
    private var mIs24HourMode = false
    private var mCurrentItemShowing = 0
    private val mCircleView: CircleView
    private val mAmPmCirclesView: AmPmCirclesView
    private val mHourRadialTextsView: RadialTextsView
    private val mMinuteRadialTextsView: RadialTextsView
    private val mSecondRadialTextsView: RadialTextsView
    private val mHourRadialSelectorView: RadialSelectorView
    private val mMinuteRadialSelectorView: RadialSelectorView
    private val mSecondRadialSelectorView: RadialSelectorView
    private val mGrayBox: View
    private var mSnapPrefer30sMap: IntArray? = IntArray(0)
    private var mInputEnabled: Boolean
    private var mIsTouchingAmOrPm = -1
    private var mDoingMove: Boolean
    private var mDoingTouch = false
    private var mDownDegrees = 0
    private var mDownX = 0f
    private var mDownY = 0f
    private val mAccessibilityManager: AccessibilityManager
    private var mTransition: AnimatorSet? = null
    private val mHandler = Handler()

    interface OnValueSelectedListener {
        fun onValueSelected(newTime: TimePoint?)
        fun enablePicker()
        fun advancePicker(index: Int)
    }

    fun setOnValueSelectedListener(listener: OnValueSelectedListener?) {
        mListener = listener
    }

    /**
     * Initialize the Layout with starting values.
     * @param context A context needed to inflate resources
     * @param locale A Locale to be used when generating strings
     * @param initialTime The initial selection of the Timepicker
     * @param is24HourMode Indicates whether we should render in 24hour mode or with AM/PM selectors
     */
    fun initialize(context: Context?, locale: Locale?, timePickerController: TimePickerController?,
                   initialTime: TimePoint, is24HourMode: Boolean) {
        if (mTimeInitialized) {
            Log.e(TAG, "Time has already been initialized.")
            return
        }
        mController = timePickerController
        mIs24HourMode = mAccessibilityManager.isTouchExplorationEnabled || is24HourMode

        // Initialize the circle and AM/PM circles if applicable.
        mCircleView.initialize(context!!, mController!!)
        mCircleView.invalidate()
        if (!mIs24HourMode && mController!!.version === TimePickerDialog.Version.VERSION_1) {
            mAmPmCirclesView.initialize(context, locale, mController!!, if (initialTime.isAM) AM else PM)
            mAmPmCirclesView.invalidate()
        }

        // Create the selection validators
        val secondValidator = object : RadialTextsView.SelectionValidator {
            override fun isValidSelection(selection: Int): Boolean {
                val newTime = TimePoint(mCurrentTime!!.hour, mCurrentTime!!.minute, selection)
                return !mController!!.isOutOfRange(newTime, SECOND_INDEX)
            }
        }
        val minuteValidator = object : RadialTextsView.SelectionValidator {
            override fun isValidSelection(selection: Int): Boolean {
                val newTime = TimePoint(mCurrentTime!!.hour, selection, mCurrentTime!!.second)
                return !mController!!.isOutOfRange(newTime, MINUTE_INDEX)
            }
        }
        val hourValidator = object : RadialTextsView.SelectionValidator {
            override fun isValidSelection(selection: Int): Boolean {
                val newTime = TimePoint(selection, mCurrentTime!!.minute, mCurrentTime!!.second)
                if (!mIs24HourMode && isCurrentlyAmOrPm == PM) newTime.setPM()
                if (!mIs24HourMode && isCurrentlyAmOrPm == AM) newTime.setAM()
                return !mController!!.isOutOfRange(newTime, HOUR_INDEX)
            }
        }

        // Initialize the hours and minutes numbers.
        val hours = intArrayOf(12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        val hours_24 = intArrayOf(0, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23)
        val minutes = intArrayOf(0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55)
        val seconds = intArrayOf(0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55)
        var hoursTexts = arrayOfNulls<String>(12)
        var innerHoursTexts = arrayOfNulls<String>(12)
        val minutesTexts = arrayOfNulls<String>(12)
        val secondsTexts = arrayOfNulls<String>(12)
        for (i in 0..11) {
            hoursTexts[i] = if (is24HourMode) String.format(locale!!, "%02d", hours_24[i]) else String.format(locale!!, "%d", hours[i])
            innerHoursTexts[i] = String.format(locale, "%d", hours[i])
            minutesTexts[i] = String.format(locale, "%02d", minutes[i])
            secondsTexts[i] = String.format(locale, "%02d", seconds[i])
        }
        // The version 2 layout has the hours > 12 on the inner circle rather than the outer circle
        // Inner circle and outer circle should be swapped (see #411)
        if (mController!!.version === TimePickerDialog.Version.VERSION_2) {
            val temp = hoursTexts
            hoursTexts = innerHoursTexts
            innerHoursTexts = temp
        }
        mHourRadialTextsView.initialize(context!!,
                hoursTexts.map { it.orEmpty() }.toTypedArray(), if (is24HourMode) innerHoursTexts.map { it.orEmpty() }.toTypedArray() else null, mController!!, hourValidator, true)
        mHourRadialTextsView.setSelection(if (is24HourMode) initialTime.hour else hours[initialTime.hour % 12])
        mHourRadialTextsView.invalidate()
        mMinuteRadialTextsView.initialize(context, minutesTexts.map { it.orEmpty() }.toTypedArray(), null, mController!!, minuteValidator, false)
        mMinuteRadialTextsView.setSelection(initialTime.minute)
        mMinuteRadialTextsView.invalidate()
        mSecondRadialTextsView.initialize(context, secondsTexts.map { it.orEmpty() }.toTypedArray(), null, mController!!, secondValidator, false)
        mSecondRadialTextsView.setSelection(initialTime.second)
        mSecondRadialTextsView.invalidate()

        // Initialize the currently-selected hour and minute.
        mCurrentTime = initialTime
        val hourDegrees = initialTime.hour % 12 * HOUR_VALUE_TO_DEGREES_STEP_SIZE
        mHourRadialSelectorView.initialize(context, mController!!, is24HourMode, true,
                hourDegrees, isHourInnerCircle(initialTime.hour))
        val minuteDegrees = initialTime.minute * MINUTE_VALUE_TO_DEGREES_STEP_SIZE
        mMinuteRadialSelectorView.initialize(context, mController!!, false, false,
                minuteDegrees, false)
        val secondDegrees = initialTime.second * SECOND_VALUE_TO_DEGREES_STEP_SIZE
        mSecondRadialSelectorView.initialize(context, mController!!, false, false,
                secondDegrees, false)
        mTimeInitialized = true
    }

    /**
     * Set either the hour, the minute or the second. Will set the internal value, and set the selection.
     */
    private fun setItem(index: Int, time: TimePoint?) {
        var time = time
        time = roundToValidTime(time, index)
        mCurrentTime = time
        reselectSelector(time, false, index)
    }

    /**
     * Check if a given hour appears in the outer circle or the inner circle
     * @return true if the hour is in the inner circle, false if it's in the outer circle.
     */
    private fun isHourInnerCircle(hourOfDay: Int): Boolean {
        // We'll have the 00 hours on the outside circle.
        var isMorning = hourOfDay <= 12 && hourOfDay != 0
        // In the version 2 layout the circles are swapped
        if (mController!!.version !== TimePickerDialog.Version.VERSION_1) isMorning = !isMorning
        return mIs24HourMode && isMorning
    }

    val hours: Int
        get() = mCurrentTime!!.hour

    val minutes: Int
        get() = mCurrentTime!!.minute

    val seconds: Int
        get() = mCurrentTime!!.second

    var time: TimePoint?
        get() = mCurrentTime
        set(time) {
            setItem(HOUR_INDEX, time)
        }

    /**
     * If the hours are showing, return the current hour. If the minutes are showing, return the
     * current minute.
     */
    private val currentlyShowingValue: Int
        private get() {
            val currentIndex = currentItemShowing
            return when (currentIndex) {
                HOUR_INDEX -> mCurrentTime!!.hour
                MINUTE_INDEX -> mCurrentTime!!.minute
                SECOND_INDEX -> mCurrentTime!!.second
                else -> -1
            }
        }

    val isCurrentlyAmOrPm: Int
        get() {
            if (mCurrentTime!!.isAM) {
                return AM
            } else if (mCurrentTime!!.isPM) {
                return PM
            }
            return -1
        }

    /**
     * Set the internal value as either AM or PM, and update the AM/PM circle displays.
     * @param amOrPm Integer representing AM of PM (use the supplied constants)
     */
    fun setAmOrPm(amOrPm: Int) {
        mAmPmCirclesView.setAmOrPm(amOrPm)
        mAmPmCirclesView.invalidate()
        var newSelection: TimePoint? = TimePoint(mCurrentTime!!)
        if (amOrPm == AM) newSelection!!.setAM() else if (amOrPm == PM) newSelection!!.setPM()
        newSelection = roundToValidTime(newSelection, HOUR_INDEX)
        reselectSelector(newSelection, false, HOUR_INDEX)
        mCurrentTime = newSelection
        mListener!!.onValueSelected(newSelection)
    }

    /**
     * Split up the 360 degrees of the circle among the 60 selectable values. Assigns a larger
     * selectable area to each of the 12 visible values, such that the ratio of space apportioned
     * to a visible value : space apportioned to a non-visible value will be 14 : 4.
     * E.g. the output of 30 degrees should have a higher range of input associated with it than
     * the output of 24 degrees, because 30 degrees corresponds to a visible number on the clock
     * circle (5 on the minutes, 1 or 13 on the hours).
     */
    private fun preparePrefer30sMap() {
        // We'll split up the visible output and the non-visible output such that each visible
        // output will correspond to a range of 14 associated input degrees, and each non-visible
        // output will correspond to a range of 4 associate input degrees, so visible numbers
        // are more than 3 times easier to get than non-visible numbers:
        // {354-359,0-7}:0, {8-11}:6, {12-15}:12, {16-19}:18, {20-23}:24, {24-37}:30, etc.
        //
        // If an output of 30 degrees should correspond to a range of 14 associated degrees, then
        // we'll need any input between 24 - 37 to snap to 30. Working out from there, 20-23 should
        // snap to 24, while 38-41 should snap to 36. This is somewhat counter-intuitive, that you
        // can be touching 36 degrees but have the selection snapped to 30 degrees; however, this
        // inconsistency isn't noticeable at such fine-grained degrees, and it affords us the
        // ability to aggressively prefer the visible values by a factor of more than 3:1, which
        // greatly contributes to the selectability of these values.

        // Our input will be 0 through 360.
        mSnapPrefer30sMap = IntArray(361)

        // The first output is 0, and each following output will increment by 6 {0, 6, 12, ...}.
        var snappedOutputDegrees = 0
        // Count of how many inputs we've designated to the specified output.
        var count = 1
        // How many input we expect for a specified output. This will be 14 for output divisible
        // by 30, and 4 for the remaining output. We'll special case the outputs of 0 and 360, so
        // the caller can decide which they need.
        var expectedCount = 8
        // Iterate through the input.
        for (degrees in 0..360) {
            // Save the input-output mapping.
            mSnapPrefer30sMap!![degrees] = snappedOutputDegrees
            // If this is the last input for the specified output, calculate the next output and
            // the next expected count.
            if (count == expectedCount) {
                snappedOutputDegrees += 6
                expectedCount = if (snappedOutputDegrees == 360) {
                    7
                } else if (snappedOutputDegrees % 30 == 0) {
                    14
                } else {
                    4
                }
                count = 1
            } else {
                count++
            }
        }
    }

    /**
     * Returns mapping of any input degrees (0 to 360) to one of 60 selectable output degrees,
     * where the degrees corresponding to visible numbers (i.e. those divisible by 30) will be
     * weighted heavier than the degrees corresponding to non-visible numbers.
     * See [.preparePrefer30sMap] documentation for the rationale and generation of the
     * mapping.
     */
    private fun snapPrefer30s(degrees: Int): Int {
        return if (mSnapPrefer30sMap == null) {
            -1
        } else mSnapPrefer30sMap!![degrees]
    }

    /**
     * Snap the input to a selectable value
     * @param newSelection Timepoint - Time which should be rounded
     * @param currentItemShowing int - The index of the current view
     * @return Timepoint - the rounded value
     */
    private fun roundToValidTime(newSelection: TimePoint?, currentItemShowing: Int): TimePoint? {
        return when (currentItemShowing) {
            HOUR_INDEX -> mController!!.roundToNearest(newSelection, null)
            MINUTE_INDEX -> mController!!.roundToNearest(newSelection, TimePoint.TYPE.HOUR)
            else -> mController!!.roundToNearest(newSelection, TimePoint.TYPE.MINUTE)
        }
    }

    /**
     * For the currently showing view (either hours, minutes or seconds), re-calculate the position
     * for the selector, and redraw it at that position. The text representing the currently
     * selected value will be redrawn if required.
     * @param newSelection Timpoint - Time which should be selected.
     * @param forceDrawDot The dot in the circle will generally only be shown when the selection
     * @param index The picker to use as a reference. Will be getCurrentItemShow() except when AM/PM is changed
     * is on non-visible values, but use this to force the dot to be shown.
     */
    private fun reselectSelector(newSelection: TimePoint?, forceDrawDot: Boolean, index: Int) {
        when (index) {
            HOUR_INDEX -> {
                // The selection might have changed, recalculate the degrees and innerCircle values
                var hour = newSelection!!.hour
                val isInnerCircle = isHourInnerCircle(hour)
                val degrees = hour % 12 * 360 / 12
                if (!mIs24HourMode) hour %= 12
                if (!mIs24HourMode && hour == 0) hour += 12
                mHourRadialSelectorView.setSelection(degrees, isInnerCircle, forceDrawDot)
                mHourRadialTextsView.setSelection(hour)
                // If we rounded the minutes, reposition the minuteSelector too.
                if (newSelection.minute != mCurrentTime!!.minute) {
                    val minDegrees = newSelection.minute * (360 / 60)
                    mMinuteRadialSelectorView.setSelection(minDegrees, isInnerCircle, forceDrawDot)
                    mMinuteRadialTextsView.setSelection(newSelection.minute)
                }
                // If we rounded the seconds, reposition the secondSelector too.
                if (newSelection.second != mCurrentTime!!.second) {
                    val secDegrees = newSelection.second * (360 / 60)
                    mSecondRadialSelectorView.setSelection(secDegrees, isInnerCircle, forceDrawDot)
                    mSecondRadialTextsView.setSelection(newSelection.second)
                }
            }
            MINUTE_INDEX -> {
                // The selection might have changed, recalculate the degrees
                val degrees = newSelection!!.minute * (360 / 60)
                mMinuteRadialSelectorView.setSelection(degrees, false, forceDrawDot)
                mMinuteRadialTextsView.setSelection(newSelection.minute)
                // If we rounded the seconds, reposition the secondSelector too.
                if (newSelection.second != mCurrentTime!!.second) {
                    val secDegrees = newSelection.second * (360 / 60)
                    mSecondRadialSelectorView.setSelection(secDegrees, false, forceDrawDot)
                    mSecondRadialTextsView.setSelection(newSelection.second)
                }
            }
            SECOND_INDEX -> {
                // The selection might have changed, recalculate the degrees
                val degrees = newSelection!!.second * (360 / 60)
                mSecondRadialSelectorView.setSelection(degrees, false, forceDrawDot)
                mSecondRadialTextsView.setSelection(newSelection.second)
            }
        }
        when (currentItemShowing) {
            HOUR_INDEX -> {
                mHourRadialSelectorView.invalidate()
                mHourRadialTextsView.invalidate()
            }
            MINUTE_INDEX -> {
                mMinuteRadialSelectorView.invalidate()
                mMinuteRadialTextsView.invalidate()
            }
            SECOND_INDEX -> {
                mSecondRadialSelectorView.invalidate()
                mSecondRadialTextsView.invalidate()
            }
        }
    }

    private fun getTimeFromDegrees(degrees: Int, isInnerCircle: Boolean, forceToVisibleValue: Boolean): TimePoint? {
        var degrees = degrees
        if (degrees == -1) {
            return null
        }
        val currentShowing = currentItemShowing
        val stepSize: Int
        val allowFineGrained = !forceToVisibleValue &&
                (currentShowing == MINUTE_INDEX || currentShowing == SECOND_INDEX)
        degrees = if (allowFineGrained) {
            snapPrefer30s(degrees)
        } else {
            snapOnly30s(degrees, 0)
        }
        stepSize = when (currentShowing) {
            HOUR_INDEX -> HOUR_VALUE_TO_DEGREES_STEP_SIZE
            MINUTE_INDEX -> MINUTE_VALUE_TO_DEGREES_STEP_SIZE
            else -> SECOND_VALUE_TO_DEGREES_STEP_SIZE
        }

        // TODO: simplify this logic. Just appending a swap of the values at the end for the v2
        // TODO: layout makes this code rather hard to read
        if (currentShowing == HOUR_INDEX) {
            if (mIs24HourMode) {
                if (degrees == 0 && isInnerCircle) {
                    degrees = 360
                } else if (degrees == 360 && !isInnerCircle) {
                    degrees = 0
                }
            } else if (degrees == 0) {
                degrees = 360
            }
        } else if (degrees == 360 && (currentShowing == MINUTE_INDEX || currentShowing == SECOND_INDEX)) {
            degrees = 0
        }
        var value = degrees / stepSize
        if (currentShowing == HOUR_INDEX && mIs24HourMode && !isInnerCircle && degrees != 0) {
            value += 12
        }
        if (currentShowing == HOUR_INDEX && mController!!.version !== TimePickerDialog.Version.VERSION_1 && mIs24HourMode) {
            value = (value + 12) % 24
        }
        val newSelection: TimePoint?
        when (currentShowing) {
            HOUR_INDEX -> {
                var hour = value
                if (!mIs24HourMode && isCurrentlyAmOrPm == PM && degrees != 360) hour += 12
                if (!mIs24HourMode && isCurrentlyAmOrPm == AM && degrees == 360) hour = 0
                newSelection = TimePoint(hour, mCurrentTime!!.minute, mCurrentTime!!.second)
            }
            MINUTE_INDEX -> newSelection = TimePoint(mCurrentTime!!.hour, value, mCurrentTime!!.second)
            SECOND_INDEX -> newSelection = TimePoint(mCurrentTime!!.hour, mCurrentTime!!.minute, value)
            else -> newSelection = mCurrentTime
        }
        return newSelection
    }

    /**
     * Calculate the degrees within the circle that corresponds to the specified coordinates, if
     * the coordinates are within the range that will trigger a selection.
     * @param pointX The x coordinate.
     * @param pointY The y coordinate.
     * @param forceLegal Force the selection to be legal, regardless of how far the coordinates are
     * from the actual numbers.
     * @param isInnerCircle If the selection may be in the inner circle, pass in a size-1 boolean
     * array here, inside which the value will be true if the selection is in the inner circle,
     * and false if in the outer circle.
     * @return Degrees from 0 to 360, if the selection was within the legal range. -1 if not.
     */
    private fun getDegreesFromCoords(pointX: Float, pointY: Float, forceLegal: Boolean,
                                     isInnerCircle: Array<Boolean?>): Int {
        return when (currentItemShowing) {
            HOUR_INDEX -> mHourRadialSelectorView.getDegreesFromCoords(
                    pointX, pointY, forceLegal, isInnerCircle)
            MINUTE_INDEX -> mMinuteRadialSelectorView.getDegreesFromCoords(
                    pointX, pointY, forceLegal, isInnerCircle)
            SECOND_INDEX -> mSecondRadialSelectorView.getDegreesFromCoords(
                    pointX, pointY, forceLegal, isInnerCircle)
            else -> -1
        }
    }

    /**
     * Get the item (hours, minutes or seconds) that is currently showing.
     */
    val currentItemShowing: Int
        get() {
            if (mCurrentItemShowing != HOUR_INDEX && mCurrentItemShowing != MINUTE_INDEX && mCurrentItemShowing != SECOND_INDEX) {
                Log.e(TAG, "Current item showing was unfortunately set to $mCurrentItemShowing")
                return -1
            }
            return mCurrentItemShowing
        }

    /**
     * Set either seconds, minutes or hours as showing.
     * @param animate True to animate the transition, false to show with no animation.
     */
    fun setCurrentItemShowing(index: Int, animate: Boolean) {
        if (index != HOUR_INDEX && index != MINUTE_INDEX && index != SECOND_INDEX) {
            Log.e(TAG, "TimePicker does not support view at index $index")
            return
        }
        val lastIndex = currentItemShowing
        mCurrentItemShowing = index
        reselectSelector(time, true, index)
        if (animate && index != lastIndex) {
            val anims = arrayOfNulls<ObjectAnimator>(4)
            if (index == MINUTE_INDEX && lastIndex == HOUR_INDEX) {
                anims[0] = mHourRadialTextsView.disappearAnimator
                anims[1] = mHourRadialSelectorView.disappearAnimator
                anims[2] = mMinuteRadialTextsView.reappearAnimator
                anims[3] = mMinuteRadialSelectorView.reappearAnimator
            } else if (index == HOUR_INDEX && lastIndex == MINUTE_INDEX) {
                anims[0] = mHourRadialTextsView.reappearAnimator
                anims[1] = mHourRadialSelectorView.reappearAnimator
                anims[2] = mMinuteRadialTextsView.disappearAnimator
                anims[3] = mMinuteRadialSelectorView.disappearAnimator
            } else if (index == MINUTE_INDEX && lastIndex == SECOND_INDEX) {
                anims[0] = mSecondRadialTextsView.disappearAnimator
                anims[1] = mSecondRadialSelectorView.disappearAnimator
                anims[2] = mMinuteRadialTextsView.reappearAnimator
                anims[3] = mMinuteRadialSelectorView.reappearAnimator
            } else if (index == HOUR_INDEX && lastIndex == SECOND_INDEX) {
                anims[0] = mSecondRadialTextsView.disappearAnimator
                anims[1] = mSecondRadialSelectorView.disappearAnimator
                anims[2] = mHourRadialTextsView.reappearAnimator
                anims[3] = mHourRadialSelectorView.reappearAnimator
            } else if (index == SECOND_INDEX && lastIndex == MINUTE_INDEX) {
                anims[0] = mSecondRadialTextsView.reappearAnimator
                anims[1] = mSecondRadialSelectorView.reappearAnimator
                anims[2] = mMinuteRadialTextsView.disappearAnimator
                anims[3] = mMinuteRadialSelectorView.disappearAnimator
            } else if (index == SECOND_INDEX && lastIndex == HOUR_INDEX) {
                anims[0] = mSecondRadialTextsView.reappearAnimator
                anims[1] = mSecondRadialSelectorView.reappearAnimator
                anims[2] = mHourRadialTextsView.disappearAnimator
                anims[3] = mHourRadialSelectorView.disappearAnimator
            }
            if (anims[0] != null && anims[1] != null && anims[2] != null && anims[3] != null) {
                if (mTransition != null && mTransition!!.isRunning) {
                    mTransition!!.end()
                }
                mTransition = AnimatorSet()
                mTransition!!.playTogether(*anims)
                mTransition!!.start()
            } else {
                transitionWithoutAnimation(index)
            }
        } else {
            transitionWithoutAnimation(index)
        }
    }

    private fun transitionWithoutAnimation(index: Int) {
        val hourAlpha = if (index == HOUR_INDEX) 1 else 0
        val minuteAlpha = if (index == MINUTE_INDEX) 1 else 0
        val secondAlpha = if (index == SECOND_INDEX) 1 else 0
        mHourRadialTextsView.alpha = hourAlpha.toFloat()
        mHourRadialSelectorView.alpha = hourAlpha.toFloat()
        mMinuteRadialTextsView.alpha = minuteAlpha.toFloat()
        mMinuteRadialSelectorView.alpha = minuteAlpha.toFloat()
        mSecondRadialTextsView.alpha = secondAlpha.toFloat()
        mSecondRadialSelectorView.alpha = secondAlpha.toFloat()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val eventX = event.x
        val eventY = event.y
        val degrees: Int
        var value: TimePoint?
        val isInnerCircle = arrayOfNulls<Boolean>(1)
        isInnerCircle[0] = false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!mInputEnabled) {
                    return true
                }
                mDownX = eventX
                mDownY = eventY
                mLastValueSelected = null
                mDoingMove = false
                mDoingTouch = true
                // If we're showing the AM/PM, check to see if the user is touching it.
                mIsTouchingAmOrPm = if (!mIs24HourMode && mController!!.version === TimePickerDialog.Version.VERSION_1) {
                    mAmPmCirclesView.getIsTouchingAmOrPm(eventX, eventY)
                } else {
                    -1
                }
                if (mIsTouchingAmOrPm == AM || mIsTouchingAmOrPm == PM) {
                    // If the touch is on AM or PM, set it as "touched" after the TAP_TIMEOUT
                    // in case the user moves their finger quickly.
                    mController!!.tryVibrate()
                    mDownDegrees = -1
                    mHandler.postDelayed({
                        mAmPmCirclesView.setAmOrPmPressed(mIsTouchingAmOrPm)
                        mAmPmCirclesView.invalidate()
                    }, TAP_TIMEOUT.toLong())
                } else {
                    // If we're in accessibility mode, force the touch to be legal. Otherwise,
                    // it will only register within the given touch target zone.
                    val forceLegal = mAccessibilityManager.isTouchExplorationEnabled
                    // Calculate the degrees that is currently being touched.
                    mDownDegrees = getDegreesFromCoords(eventX, eventY, forceLegal, isInnerCircle)
                    val selectedTime = getTimeFromDegrees(mDownDegrees, isInnerCircle[0]!!, false)
                    if (mController!!.isOutOfRange(selectedTime, currentItemShowing)) mDownDegrees = -1
                    if (mDownDegrees != -1) {
                        // If it's a legal touch, set that number as "selected" after the
                        // TAP_TIMEOUT in case the user moves their finger quickly.
                        mController!!.tryVibrate()
                        mHandler.postDelayed({
                            mDoingMove = true
                            mLastValueSelected = getTimeFromDegrees(mDownDegrees, isInnerCircle[0]!!,
                                    false)
                            mLastValueSelected = roundToValidTime(mLastValueSelected, currentItemShowing)
                            // Redraw
                            reselectSelector(mLastValueSelected, true, currentItemShowing)
                            mListener!!.onValueSelected(mLastValueSelected)
                        }, TAP_TIMEOUT.toLong())
                    }
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!mInputEnabled) {
                    // We shouldn't be in this state, because input is disabled.
                    Log.e(TAG, "Input was disabled, but received ACTION_MOVE.")
                    return true
                }
                val dY = Math.abs(eventY - mDownY)
                val dX = Math.abs(eventX - mDownX)
                if (!mDoingMove && dX <= TOUCH_SLOP && dY <= TOUCH_SLOP) {
                    // Hasn't registered down yet, just slight, accidental movement of finger.
                    return true
                }

                // If we're in the middle of touching down on AM or PM, check if we still are.
                // If so, no-op. If not, remove its pressed state. Either way, no need to check
                // for touches on the other circle.
                if (mIsTouchingAmOrPm == AM || mIsTouchingAmOrPm == PM) {
                    mHandler.removeCallbacksAndMessages(null)
                    val isTouchingAmOrPm = mAmPmCirclesView.getIsTouchingAmOrPm(eventX, eventY)
                    if (isTouchingAmOrPm != mIsTouchingAmOrPm) {
                        mAmPmCirclesView.setAmOrPmPressed(-1)
                        mAmPmCirclesView.invalidate()
                        mIsTouchingAmOrPm = -1
                    }
                    return true
                }
                if (mDownDegrees == -1) {
                    // Original down was illegal, so no movement will register.
                    return true
                }

                // We're doing a move along the circle, so move the selection as appropriate.
                mDoingMove = true
                mHandler.removeCallbacksAndMessages(null)
                degrees = getDegreesFromCoords(eventX, eventY, true, isInnerCircle)
                if (degrees != -1) {
                    value = roundToValidTime(
                            getTimeFromDegrees(degrees, isInnerCircle[0]!!, false),
                            currentItemShowing
                    )
                    reselectSelector(value, true, currentItemShowing)
                    if (value != null && (mLastValueSelected == null || !mLastValueSelected!!.equals(value))) {
                        mController!!.tryVibrate()
                        mLastValueSelected = value
                        mListener!!.onValueSelected(value)
                    }
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (!mInputEnabled) {
                    // If our touch input was disabled, tell the listener to re-enable us.
                    Log.d(TAG, "Input was disabled, but received ACTION_UP.")
                    mListener!!.enablePicker()
                    return true
                }
                mHandler.removeCallbacksAndMessages(null)
                mDoingTouch = false

                // If we're touching AM or PM, set it as selected, and tell the listener.
                if (mIsTouchingAmOrPm == AM || mIsTouchingAmOrPm == PM) {
                    val isTouchingAmOrPm = mAmPmCirclesView.getIsTouchingAmOrPm(eventX, eventY)
                    mAmPmCirclesView.setAmOrPmPressed(-1)
                    mAmPmCirclesView.invalidate()
                    if (isTouchingAmOrPm == mIsTouchingAmOrPm) {
                        mAmPmCirclesView.setAmOrPm(isTouchingAmOrPm)
                        if (isCurrentlyAmOrPm != isTouchingAmOrPm) {
                            var newSelection: TimePoint? = TimePoint(mCurrentTime!!)
                            if (mIsTouchingAmOrPm == AM) newSelection!!.setAM() else if (mIsTouchingAmOrPm == PM) newSelection!!.setPM()
                            newSelection = roundToValidTime(newSelection, HOUR_INDEX)
                            reselectSelector(newSelection, false, HOUR_INDEX)
                            mCurrentTime = newSelection
                            mListener!!.onValueSelected(newSelection)
                        }
                    }
                    mIsTouchingAmOrPm = -1
                    return true
                }

                // If we have a legal degrees selected, set the value and tell the listener.
                if (mDownDegrees != -1) {
                    degrees = getDegreesFromCoords(eventX, eventY, mDoingMove, isInnerCircle)
                    if (degrees != -1) {
                        value = getTimeFromDegrees(degrees, isInnerCircle[0]!!, !mDoingMove)
                        value = roundToValidTime(value, currentItemShowing)
                        reselectSelector(value, false, currentItemShowing)
                        mCurrentTime = value
                        mListener!!.onValueSelected(value)
                        mListener!!.advancePicker(currentItemShowing)
                    }
                }
                mDoingMove = false
                return true
            }
            else -> {
            }
        }
        return false
    }

    /**
     * Set touch input as enabled or disabled, for use with keyboard mode.
     */
    fun trySettingInputEnabled(inputEnabled: Boolean): Boolean {
        if (mDoingTouch && !inputEnabled) {
            // If we're trying to disable input, but we're in the middle of a touch event,
            // we'll allow the touch event to continue before disabling input.
            return false
        }
        mInputEnabled = inputEnabled
        mGrayBox.visibility = if (inputEnabled) View.INVISIBLE else View.VISIBLE
        return true
    }

    /**
     * Necessary for accessibility, to ensure we support "scrolling" forward and backward
     * in the circle.
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
     * Announce the currently-selected time when launched.
     */
    override fun dispatchPopulateAccessibilityEvent(event: AccessibilityEvent): Boolean {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            // Clear the event's current text so that only the current time will be spoken.
            event.text.clear()
            val time = Calendar.getInstance()
            time[Calendar.HOUR] = hours
            time[Calendar.MINUTE] = minutes
            time[Calendar.SECOND] = seconds
            val millis = time.timeInMillis
            var flags = DateUtils.FORMAT_SHOW_TIME
            if (mIs24HourMode) {
                flags = flags or DateUtils.FORMAT_24HOUR
            }
            val timeString = DateUtils.formatDateTime(context, millis, flags)
            event.text.add(timeString)
            return true
        }
        return super.dispatchPopulateAccessibilityEvent(event)
    }

    /**
     * When scroll forward/backward events are received, jump the time to the higher/lower
     * discrete, visible value on the circle.
     */
    override fun performAccessibilityAction(action: Int, arguments: Bundle): Boolean {
        if (super.performAccessibilityAction(action, arguments)) {
            return true
        }
        var changeMultiplier = 0
        val forward: Int
        val backward: Int
        if (Build.VERSION.SDK_INT >= 16) {
            forward = AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
            backward = AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
        } else {
            forward = AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD
            backward = AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD
        }
        if (action == forward) {
            changeMultiplier = 1
        } else if (action == backward) {
            changeMultiplier = -1
        }
        if (changeMultiplier != 0) {
            var value = currentlyShowingValue
            var stepSize = 0
            val currentItemShowing = currentItemShowing
            if (currentItemShowing == HOUR_INDEX) {
                stepSize = HOUR_VALUE_TO_DEGREES_STEP_SIZE
                value %= 12
            } else if (currentItemShowing == MINUTE_INDEX) {
                stepSize = MINUTE_VALUE_TO_DEGREES_STEP_SIZE
            } else if (currentItemShowing == SECOND_INDEX) {
                stepSize = SECOND_VALUE_TO_DEGREES_STEP_SIZE
            }
            var degrees = value * stepSize
            degrees = snapOnly30s(degrees, changeMultiplier)
            value = degrees / stepSize
            var maxValue = 0
            var minValue = 0
            if (currentItemShowing == HOUR_INDEX) {
                if (mIs24HourMode) {
                    maxValue = 23
                } else {
                    maxValue = 12
                    minValue = 1
                }
            } else {
                maxValue = 55
            }
            if (value > maxValue) {
                // If we scrolled forward past the highest number, wrap around to the lowest.
                value = minValue
            } else if (value < minValue) {
                // If we scrolled backward past the lowest number, wrap around to the highest.
                value = maxValue
            }
            val newSelection: TimePoint?
            newSelection = when (currentItemShowing) {
                HOUR_INDEX -> TimePoint(
                        value,
                        mCurrentTime!!.minute,
                        mCurrentTime!!.second
                )
                MINUTE_INDEX -> TimePoint(
                        mCurrentTime!!.hour,
                        value,
                        mCurrentTime!!.second
                )
                SECOND_INDEX -> TimePoint(
                        mCurrentTime!!.hour,
                        mCurrentTime!!.minute,
                        value
                )
                else -> mCurrentTime
            }
            setItem(currentItemShowing, newSelection)
            mListener!!.onValueSelected(newSelection)
            return true
        }
        return false
    }

    companion object {
        private const val TAG = "RadialPickerLayout"
        private const val VISIBLE_DEGREES_STEP_SIZE = 30
        private const val HOUR_VALUE_TO_DEGREES_STEP_SIZE = VISIBLE_DEGREES_STEP_SIZE
        private const val MINUTE_VALUE_TO_DEGREES_STEP_SIZE = 6
        private const val SECOND_VALUE_TO_DEGREES_STEP_SIZE = 6
        private const val HOUR_INDEX = TimePickerDialog.HOUR_INDEX
        private const val MINUTE_INDEX = TimePickerDialog.MINUTE_INDEX
        private const val SECOND_INDEX = TimePickerDialog.SECOND_INDEX
        private const val AM = TimePickerDialog.AM
        private const val PM = TimePickerDialog.PM

        /**
         * Returns mapping of any input degrees (0 to 360) to one of 12 visible output degrees (all
         * multiples of 30), where the input will be "snapped" to the closest visible degrees.
         * @param degrees The input degrees
         * @param forceHigherOrLower The output may be forced to either the higher or lower step, or may
         * be allowed to snap to whichever is closer. Use 1 to force strictly higher, -1 to force
         * strictly lower, and 0 to snap to the closer one.
         * @return output degrees, will be a multiple of 30
         */
        private fun snapOnly30s(degrees: Int, forceHigherOrLower: Int): Int {
            var degrees = degrees
            val stepSize = HOUR_VALUE_TO_DEGREES_STEP_SIZE
            var floor = degrees / stepSize * stepSize
            val ceiling = floor + stepSize
            if (forceHigherOrLower == 1) {
                degrees = ceiling
            } else if (forceHigherOrLower == -1) {
                if (degrees == floor) {
                    floor -= stepSize
                }
                degrees = floor
            } else {
                degrees = if (degrees - floor < ceiling - degrees) {
                    floor
                } else {
                    ceiling
                }
            }
            return degrees
        }
    }

    init {
        setOnTouchListener(this)
        val vc = ViewConfiguration.get(context)
        TOUCH_SLOP = vc.scaledTouchSlop
        TAP_TIMEOUT = ViewConfiguration.getTapTimeout()
        mDoingMove = false
        mCircleView = CircleView(context)
        addView(mCircleView)
        mAmPmCirclesView = AmPmCirclesView(context)
        addView(mAmPmCirclesView)
        mHourRadialSelectorView = RadialSelectorView(context)
        addView(mHourRadialSelectorView)
        mMinuteRadialSelectorView = RadialSelectorView(context)
        addView(mMinuteRadialSelectorView)
        mSecondRadialSelectorView = RadialSelectorView(context)
        addView(mSecondRadialSelectorView)
        mHourRadialTextsView = RadialTextsView(context)
        addView(mHourRadialTextsView)
        mMinuteRadialTextsView = RadialTextsView(context)
        addView(mMinuteRadialTextsView)
        mSecondRadialTextsView = RadialTextsView(context)
        addView(mSecondRadialTextsView)

        // Prepare mapping to snap touchable degrees to selectable degrees.
        preparePrefer30sMap()
        mLastValueSelected = null
        mInputEnabled = true
        mGrayBox = View(context)
        mGrayBox.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mGrayBox.setBackgroundColor(ContextCompat.getColor(context, R.color.mdtp_transparent_black))
        mGrayBox.visibility = View.INVISIBLE
        addView(mGrayBox)
        mAccessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        mTimeInitialized = false
    }
}