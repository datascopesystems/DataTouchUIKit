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

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import datatouch.uikit.R
import datatouch.uikit.components.datapicker.HapticFeedbackController
import datatouch.uikit.components.datapicker.TypefaceHelper.get
import datatouch.uikit.components.datapicker.date.MonthAdapter.CalendarDay
import datatouch.uikit.utils.AnimationUtils.getPulseAnimator
import datatouch.uikit.utils.ColorUtils
import datatouch.uikit.utils.ColorUtils.getAccentColorFromThemeIfAvailable
import datatouch.uikit.utils.ColorUtils.isDarkTheme
import datatouch.uikit.utils.datetime.DatePickerUtils.trimToMidnight
import datatouch.uikit.utils.datetime.DatePickerUtils.tryAccessibilityAnnounce
import java.text.SimpleDateFormat
import java.util.*

class DatePickerFragmentDialog : DialogFragment(),
    View.OnClickListener, DatePickerController {
    /**
     * Get a reference to the callback
     *
     * @return OnDateSetListener the callback
     */
    var onDateSetListener: OnDateSetListener? =
        null
    private val mListeners =
        HashSet<OnDateChangedListener?>()
    private var mOnCancelListener: DialogInterface.OnCancelListener? = null
    private var mOnDismissListener: DialogInterface.OnDismissListener? = null
    private var mAnimator: AccessibleDateAnimator? = null
    private var mArrowLeft: ImageView? = null
    private var mArrowRight: ImageView? = null
    private var mDatePickerHeaderView: TextView? = null
    private var mMonthAndDayView: LinearLayout? = null
    private var mSelectedMonthTextView: TextView? = null
    private var mSelectedDayTextView: TextView? = null
    private var mYearView: TextView? = null
    private var mMonthPickerView: TextView? = null
    private var mDayPickerView: DayPickerView? = null
    private var mYearPickerPopup: ListPopupWindow? = null
    private var mCurrentView = UNINITIALIZED
    private var mWeekStart: Int
    private var mTitle: String? = null
    private var highlightedDays: HashSet<Calendar>? =
        HashSet()
    private var mThemeDark = false
    private var mThemeDarkChanged = false

    /**
     * Get the accent color of this dialog
     *
     * @return accent color
     */
    override var accentColor = -1
        private set
    private var mDismissOnPause = false
    private var mAutoDismiss = false
    private var mDefaultView = MONTH_AND_DAY_VIEW
    private var mOkResid = R.string.amdp_ok
    private var mOkString: String? = null
    private var mOkColor = -1
    private var mCancelResid = R.string.amdp_cancel
    private var mCancelString: String? = null
    private var mCancelColor = -1
    private var mTimezone: TimeZone? = null
    private var mCalendar =
        trimToMidnight(Calendar.getInstance(timeZone))
    private var mDefaultLimiter = DefaultDateRangeLimiter()
    private var mDateRangeLimiter: DateRangeLimiter? = mDefaultLimiter
    private var mHapticFeedbackController: HapticFeedbackController? = null
    private var mDelayAnimation = true

    // Accessibility strings.
    private var mDayPickerDescription: String? = null
    private var mSelectDay: String? = null
    private var monthYearPickersVisible = true
    fun initialize(
        callBack: OnDateSetListener?,
        year: Int,
        monthOfYear: Int,
        dayOfMonth: Int
    ) {
        onDateSetListener = callBack
        mCalendar[Calendar.YEAR] = year
        mCalendar[Calendar.MONTH] = monthOfYear
        mCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity: Activity? = activity
        activity!!.window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )
        mCurrentView = UNINITIALIZED
        if (savedInstanceState != null) {
            mCalendar[Calendar.YEAR] = savedInstanceState.getInt(KEY_SELECTED_YEAR)
            mCalendar[Calendar.MONTH] = savedInstanceState.getInt(KEY_SELECTED_MONTH)
            mCalendar[Calendar.DAY_OF_MONTH] = savedInstanceState.getInt(KEY_SELECTED_DAY)
            mDefaultView =
                savedInstanceState.getInt(KEY_DEFAULT_VIEW)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(
            KEY_SELECTED_YEAR,
            mCalendar[Calendar.YEAR]
        )
        outState.putInt(
            KEY_SELECTED_MONTH,
            mCalendar[Calendar.MONTH]
        )
        outState.putInt(
            KEY_SELECTED_DAY,
            mCalendar[Calendar.DAY_OF_MONTH]
        )
        outState.putInt(KEY_WEEK_START, mWeekStart)
        outState.putInt(KEY_CURRENT_VIEW, mCurrentView)
        var listPosition = -1
        if (mCurrentView == MONTH_AND_DAY_VIEW) {
            listPosition = mDayPickerView!!.mostVisiblePosition
        }
        outState.putInt(KEY_LIST_POSITION, listPosition)
        outState.putSerializable(
            KEY_HIGHLIGHTED_DAYS,
            highlightedDays
        )
        outState.putBoolean(KEY_THEME_DARK, mThemeDark)
        outState.putBoolean(
            KEY_THEME_DARK_CHANGED,
            mThemeDarkChanged
        )
        outState.putInt(KEY_ACCENT, accentColor)
        outState.putBoolean(KEY_DISMISS, mDismissOnPause)
        outState.putBoolean(KEY_AUTO_DISMISS, mAutoDismiss)
        outState.putInt(KEY_DEFAULT_VIEW, mDefaultView)
        outState.putString(KEY_TITLE, mTitle)
        outState.putInt(KEY_OK_RESID, mOkResid)
        outState.putString(KEY_OK_STRING, mOkString)
        outState.putInt(KEY_OK_COLOR, mOkColor)
        outState.putInt(KEY_CANCEL_RESID, mCancelResid)
        outState.putString(KEY_CANCEL_STRING, mCancelString)
        outState.putInt(KEY_CANCEL_COLOR, mCancelColor)
        outState.putSerializable(KEY_TIMEZONE, mTimezone)
        outState.putParcelable(
            KEY_DATERANGELIMITER,
            mDateRangeLimiter
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var listPosition = -1
        var currentView = mDefaultView
        if (savedInstanceState != null) {
            mWeekStart =
                savedInstanceState.getInt(KEY_WEEK_START)
            currentView =
                savedInstanceState.getInt(KEY_CURRENT_VIEW)
            listPosition =
                savedInstanceState.getInt(KEY_LIST_POSITION)
            highlightedDays =
                savedInstanceState.getSerializable(KEY_HIGHLIGHTED_DAYS) as HashSet<Calendar>?
            mThemeDark =
                savedInstanceState.getBoolean(KEY_THEME_DARK)
            mThemeDarkChanged =
                savedInstanceState.getBoolean(KEY_THEME_DARK_CHANGED)
            accentColor = savedInstanceState.getInt(KEY_ACCENT)
            mDismissOnPause =
                savedInstanceState.getBoolean(KEY_DISMISS)
            mAutoDismiss =
                savedInstanceState.getBoolean(KEY_AUTO_DISMISS)
            mTitle = savedInstanceState.getString(KEY_TITLE)
            mOkResid = savedInstanceState.getInt(KEY_OK_RESID)
            mOkString =
                savedInstanceState.getString(KEY_OK_STRING)
            mOkColor = savedInstanceState.getInt(KEY_OK_COLOR)
            mCancelResid =
                savedInstanceState.getInt(KEY_CANCEL_RESID)
            mCancelString =
                savedInstanceState.getString(KEY_CANCEL_STRING)
            mCancelColor =
                savedInstanceState.getInt(KEY_CANCEL_COLOR)
            mTimezone =
                savedInstanceState.getSerializable(KEY_TIMEZONE) as TimeZone?
            mDateRangeLimiter =
                savedInstanceState.getParcelable(KEY_DATERANGELIMITER)

            /*
            If the user supplied a custom limiter, we need to create a new default one to prevent
            null pointer exceptions on the configuration methods
            If the user did not supply a custom limiter we need to ensure both mDefaultLimiter
            and mDateRangeLimiter are the same reference, so that the config methods actually
            affect the behaviour of the picker (in the unlikely event the user reconfigures
            the picker when it is shown)
             */mDefaultLimiter = if (mDateRangeLimiter is DefaultDateRangeLimiter) {
                mDateRangeLimiter as DefaultDateRangeLimiter
            } else {
                DefaultDateRangeLimiter()
            }
        }
        mDefaultLimiter.setController(this)
        val viewRes = R.layout.amdp_date_picker_dialog
        val view = inflater.inflate(viewRes, container, false)
        // All options have been setResources at this point: round the initial selection if necessary
        mCalendar = mDateRangeLimiter!!.setToNearestDate(mCalendar)
        mArrowLeft = view.findViewById(R.id.amdp_month_arrow_left)
        mArrowRight = view.findViewById(R.id.amdp_month_arrow_right)
        mDatePickerHeaderView = view.findViewById(R.id.amdp_date_picker_header)
        mMonthAndDayView = view.findViewById(R.id.amdp_date_picker_month_and_day)
        mMonthAndDayView?.setOnClickListener(this)
        mSelectedMonthTextView = view.findViewById(R.id.amdp_date_picker_month)
        mSelectedDayTextView = view.findViewById(R.id.amdp_date_picker_day)
        mYearView = view.findViewById(R.id.amdp_date_picker_year)
        mMonthPickerView = view.findViewById(R.id.amdp_month_picker)
        mYearView?.setOnClickListener(this)
        mMonthPickerView?.setOnClickListener(this)
        val activity: Activity? = activity
        mDayPickerView = SimpleDayPickerView(activity, this)
        val yearPickerView = activity?.let { YearPickerView(it, this) }
        mYearPickerPopup = ListPopupWindow(mYearView!!.getContext())
        mYearPickerPopup!!.anchorView = mYearView
        if (Build.VERSION.SDK_INT >= 19) {
            mYearPickerPopup!!.setPromptView(yearPickerView)
        } else {
            mYearPickerPopup!!.setOnItemClickListener(yearPickerView!!.onItemClickListener)
        }
        mYearPickerPopup!!.setAdapter(yearPickerView!!.adapter)

        // if theme configurationId has not been setResources by java code, check if it is specified in Style.xml
        if (!mThemeDarkChanged) {
            mThemeDark = isDarkTheme(activity, mThemeDark)
        }
        val res = resources
        mDayPickerDescription = res.getString(R.string.amdp_day_picker_description)
        mSelectDay = res.getString(R.string.amdp_select_day)
        val bgColorResource =
            if (mThemeDark) R.color.amdp_date_picker_view_animator_dark_theme else R.color.amdp_date_picker_view_animator
        val bgColor = ContextCompat.getColor(activity!!, bgColorResource)
        view.setBackgroundColor(bgColor)
        mAnimator = view.findViewById(R.id.amdp_animator)
        mAnimator?.setBackgroundColor(bgColor)
        mAnimator?.addView(mDayPickerView)
        mAnimator?.setDateMillis(mCalendar.timeInMillis)
        // TODO: Replace with animation decided upon by the design team.
        val animation: Animation = AlphaAnimation(0.0f, 1.0f)
        animation.duration = ANIMATION_DURATION.toLong()
        mAnimator?.setInAnimation(animation)
        // TODO: Replace with animation decided upon by the design team.
        val animation2: Animation = AlphaAnimation(1.0f, 0.0f)
        animation2.duration = ANIMATION_DURATION.toLong()
        mAnimator?.setOutAnimation(animation2)
        val okButton = view.findViewById<Button>(R.id.amdp_ok)
        okButton.setOnClickListener {
            notifyOnDateListener()
            dismiss()
        }
        okButton.setTypeface(get(activity, "Roboto-Medium"))
        if (mOkString != null) {
            okButton.text = mOkString
        } else {
            okButton.setText(mOkResid)
        }
        val cancelButton =
            view.findViewById<Button>(R.id.amdp_cancel)
        cancelButton.setOnClickListener { if (dialog != null) dialog!!.cancel() }
        cancelButton.setTypeface(get(activity, "Roboto-Medium"))
        if (mCancelString != null) {
            cancelButton.text = mCancelString
        } else {
            cancelButton.setText(mCancelResid)
        }
        cancelButton.visibility = if (isCancelable) View.VISIBLE else View.GONE

        // If an accent color has not been setResources manually, get it from the context
        if (accentColor == -1) {
            accentColor = getAccentColorFromThemeIfAvailable(getActivity()!!)
        }
        if (mDatePickerHeaderView != null) {
            mDatePickerHeaderView!!.setBackgroundColor(ColorUtils.darkenColor(accentColor))
        }
        view.findViewById<View>(R.id.amdp_day_picker_selected_date_layout)
            .setBackgroundColor(accentColor)

        // Buttons can have a different color
        if (mOkColor != -1) okButton.setTextColor(mOkColor) else okButton.setTextColor(accentColor)
        if (mCancelColor != -1) cancelButton.setTextColor(mCancelColor) else cancelButton.setTextColor(
            accentColor
        )
        if (dialog == null) {
            view.findViewById<View>(R.id.amdp_done_background).visibility = View.GONE
        }
        updateDisplay(false)
        setCurrentView(currentView)
        mArrowLeft?.setOnClickListener(this)
        mArrowRight?.setOnClickListener(this)
        if (listPosition != -1 && currentView == MONTH_AND_DAY_VIEW) {
            mDayPickerView?.postSetSelection(listPosition)
        }
        mHapticFeedbackController = HapticFeedbackController(activity)
        return view
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val viewGroup = view as ViewGroup?
        if (viewGroup != null) {
            viewGroup.removeAllViewsInLayout()
            val view =
                onCreateView(activity!!.layoutInflater, viewGroup, null)
            viewGroup.addView(view)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onResume() {
        super.onResume()
        mHapticFeedbackController!!.start()
    }

    override fun onPause() {
        super.onPause()
        mHapticFeedbackController!!.stop()
        if (mDismissOnPause) dismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        if (mOnCancelListener != null) mOnCancelListener!!.onCancel(dialog)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (mOnDismissListener != null) mOnDismissListener!!.onDismiss(dialog)
    }

    private fun setCurrentView(viewIndex: Int) {
        val millis = mCalendar.timeInMillis
        val pulseAnimator = getPulseAnimator(
            mMonthAndDayView, 0.9f,
            1.05f
        )
        if (mDelayAnimation) {
            pulseAnimator.startDelay = ANIMATION_DELAY.toLong()
            mDelayAnimation = false
        }
        mDayPickerView!!.onDateChanged()
        if (mCurrentView != viewIndex) {
            mMonthAndDayView!!.isSelected = true
            mYearView!!.isSelected = false
            mAnimator!!.displayedChild = MONTH_AND_DAY_VIEW
            mCurrentView = viewIndex
        }
        pulseAnimator.start()
        val flags = DateUtils.FORMAT_SHOW_DATE
        val dayString = DateUtils.formatDateTime(activity, millis, flags)
        mAnimator!!.contentDescription = "$mDayPickerDescription: $dayString"
        tryAccessibilityAnnounce(mAnimator, mSelectDay)
    }

    private fun updateDisplay(announce: Boolean) {
        mYearView!!.text = YEAR_FORMAT.format(mCalendar.time)
        mMonthPickerView!!.text = MONTH_FORMAT_STAND_ALONE.format(
            mCalendar.time
        )
        if (mDatePickerHeaderView != null) {
            if (mTitle != null) mDatePickerHeaderView!!.text =
                mTitle!!.toUpperCase(Locale.getDefault()) else {
                mDatePickerHeaderView!!.text = mCalendar.getDisplayName(
                    Calendar.DAY_OF_WEEK, Calendar.LONG,
                    Locale.getDefault()
                ).toUpperCase(Locale.getDefault())
            }
        }
        mSelectedMonthTextView!!.text = MONTH_FORMAT.format(
            mCalendar.time
        )
        mSelectedDayTextView!!.text = DAY_FORMAT.format(mCalendar.time)
        mMonthPickerView!!.visibility =
            if (monthYearPickersVisible) View.VISIBLE else View.INVISIBLE
        mYearView!!.visibility = if (monthYearPickersVisible) View.VISIBLE else View.INVISIBLE


        // Accessibility.
        val millis = mCalendar.timeInMillis
        mAnimator!!.setDateMillis(millis)
        var flags = DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_NO_YEAR
        val monthAndDayText = DateUtils.formatDateTime(activity, millis, flags)
        mMonthAndDayView!!.contentDescription = monthAndDayText
        if (announce) {
            flags = DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
            val fullDateText = DateUtils.formatDateTime(activity, millis, flags)
            tryAccessibilityAnnounce(mAnimator, fullDateText)
        }
    }

    /**
     * Set whether the picker should dismiss itself when being paused or whether it should try to survive an orientation change
     *
     * @param dismissOnPause true if the dialog should dismiss itself when it's pausing
     */
    fun dismissOnPause(dismissOnPause: Boolean) {
        mDismissOnPause = dismissOnPause
    }

    /**
     * Set whether the picker should dismiss itself when a day is selected
     *
     * @param autoDismiss true if the dialog should dismiss itself when a day is selected
     */
    fun autoDismiss(autoDismiss: Boolean) {
        mAutoDismiss = autoDismiss
    }

    /**
     * Returns true when the dark theme should be used
     *
     * @return true if the dark theme should be used, false if the default theme should be used
     */
    /**
     * Set whether the dark theme should be used
     *
     * @param themeDark true if the dark theme should be used, false if the default theme should be used
     */
    override var isThemeDark: Boolean
        get() = mThemeDark
        set(themeDark) {
            mThemeDark = themeDark
            mThemeDarkChanged = true
        }

    /**
     * Set the accent color of this dialog
     *
     * @param color the accent color you want
     */
    fun setAccentColor(color: String?) {
        accentColor = Color.parseColor(color)
    }

    /**
     * Set the text color of the OK button
     *
     * @param color the color you want
     */
    fun setOkColor(color: String?) {
        mOkColor = Color.parseColor(color)
    }

    /**
     * Set the text color of the OK button
     *
     * @param color the color you want
     */
    fun setOkColor(@ColorInt color: Int) {
        mOkColor = Color.argb(
            255,
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }

    /**
     * Set the text color of the Cancel button
     *
     * @param color the color you want
     */
    fun setCancelColor(color: String?) {
        mCancelColor = Color.parseColor(color)
    }

    /**
     * Set the text color of the Cancel button
     *
     * @param color the color you want
     */
    fun setCancelColor(@ColorInt color: Int) {
        mCancelColor = Color.argb(
            255,
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }

    /**
     * Set the accent color of this dialog
     *
     * @param color the accent color you want
     */
    fun setAccentColor(@ColorInt color: Int) {
        accentColor = Color.argb(
            255,
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }

    /**
     * Set whether the year picker of the month and day picker is shown first
     *
     * @param yearPicker boolean
     */
    fun showYearPickerFirst(yearPicker: Boolean) {
        mDefaultView =
            if (yearPicker) YEAR_VIEW else MONTH_AND_DAY_VIEW
    }

    fun setYearRange(startYear: Int, endYear: Int) {
        mDefaultLimiter.setYearRange(startYear, endYear)
        if (mDayPickerView != null) {
            mDayPickerView!!.onChange()
        }
    }

    /**
     * @return The minimal date supported by this DatePicker. Null if it has not been setResources.
     */
    /**
     * Sets the minimal date supported by this DatePicker. Dates before (but not including) the
     * specified date will be disallowed from being selected.
     *
     * @param calendar a Calendar object setResources to the year, month, day desired as the mindate.
     */
    var minDate: Calendar?
        get() = mDefaultLimiter.minDate
        set(calendar) {
            mDefaultLimiter.setMinDate(calendar!!)
            if (mDayPickerView != null) {
                mDayPickerView!!.onChange()
            }
        }

    fun setMinDate(millis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        minDate = calendar
    }

    /**
     * @return The maximal date supported by this DatePicker. Null if it has not been setResources.
     */
    /**
     * Sets the minimal date supported by this DatePicker. Dates after (but not including) the
     * specified date will be disallowed from being selected.
     *
     * @param calendar a Calendar object setResources to the year, month, day desired as the maxdate.
     */
    var maxDate: Calendar?
        get() = mDefaultLimiter.maxDate
        set(calendar) {
            mDefaultLimiter.setMaxDate(calendar!!)
            if (mDayPickerView != null) {
                mDayPickerView!!.onChange()
            }
        }

    fun setMaxDate(millis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        maxDate = calendar
    }

    /**
     * @return The list of dates, as Calendar Objects, which should be highlighted. null is no dates should be highlighted
     */
    fun getHighlightedDays(): Array<Calendar>? {
        if (highlightedDays!!.isEmpty()) return null
        val output =
            highlightedDays!!.toTypedArray()
        Arrays.sort(output)
        return output
    }

    /**
     * Sets an array of dates which should be highlighted when the picker is drawn
     *
     * @param highlightedDays an Array of Calendar objects containing the dates to be highlighted
     */
    fun setHighlightedDays(highlightedDays: Array<Calendar?>) {
        for (highlightedDay in highlightedDays) trimToMidnight(
            highlightedDay!!
        )
        this.highlightedDays!!.addAll(listOf(*highlightedDays).map { it!! })
        if (mDayPickerView != null) mDayPickerView!!.onChange()
    }

    override fun isHighlighted(year: Int, month: Int, day: Int): Boolean {
        val date = Calendar.getInstance()
        date[Calendar.YEAR] = year
        date[Calendar.MONTH] = month
        date[Calendar.DAY_OF_MONTH] = day
        trimToMidnight(date)
        return highlightedDays!!.contains(date)
    }

    /**
     * @return an Array of Calendar objects containing the list with selectable items. null if no restriction is setResources
     */
    val selectableDays: TreeSet<Calendar>?
        get() = mDefaultLimiter.selectableDays

    /**
     * Sets a list of days which are the only valid selections.
     * Setting this value will take precedence over using setMinDate() and setMaxDate()
     *
     * @param selectableDays an Array of Calendar Objects containing the selectable dates
     */
    fun setSelectableDays(selectableDays: Array<Calendar?>?) {
        mDefaultLimiter.setSelectableDays(selectableDays!!)
        if (mDayPickerView != null) mDayPickerView!!.onChange()
    }

    /**
     * @return an Array of Calendar objects containing the list of days that are not selectable. null if no restriction is setResources
     */
    val disabledDays: HashSet<Calendar>?
        get() = mDefaultLimiter.disabledDays

    /**
     * Sets a list of days that are not selectable in the picker
     * Setting this value will take precedence over using setMinDate() and setMaxDate(), but stacks with setSelectableDays()
     *
     * @param disabledDays an Array of Calendar Objects containing the disabled dates
     */
    fun setDisabledDays(disabledDays: Array<Calendar?>?) {
        mDefaultLimiter.setDisabledDays(disabledDays!!)
        if (mDayPickerView != null) mDayPickerView!!.onChange()
    }

    /**
     * Provide a DateRangeLimiter for full control over which dates are enabled and disabled in the picker
     *
     * @param dateRangeLimiter An implementation of the DateRangeLimiter interface
     */
    fun setDateRangeLimiter(dateRangeLimiter: DateRangeLimiter?) {
        mDateRangeLimiter = dateRangeLimiter
    }

    /**
     * Set a title to be displayed instead of the weekday
     *
     * @param title String - The title to be displayed
     */
    fun setTitle(title: String?) {
        mTitle = title
    }

    /**
     * Set the label for the Ok button (max 12 characters)
     *
     * @param okString A literal String to be used as the Ok button label
     */
    fun setOkText(okString: String?) {
        mOkString = okString
    }

    /**
     * Set the label for the Ok button (max 12 characters)
     *
     * @param okResid A resource ID to be used as the Ok button label
     */
    fun setOkText(@StringRes okResid: Int) {
        mOkString = null
        mOkResid = okResid
    }

    /**
     * Set the label for the Cancel button (max 12 characters)
     *
     * @param cancelString A literal String to be used as the Cancel button label
     */
    fun setCancelText(cancelString: String?) {
        mCancelString = cancelString
    }

    /**
     * Set the label for the Cancel button (max 12 characters)
     *
     * @param cancelResid A resource ID to be used as the Cancel button label
     */
    fun setCancelText(@StringRes cancelResid: Int) {
        mCancelString = null
        mCancelResid = cancelResid
    }

    fun setOnCancelListener(onCancelListener: DialogInterface.OnCancelListener?) {
        mOnCancelListener = onCancelListener
    }

    fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener?) {
        mOnDismissListener = onDismissListener
    }

    // If the newly selected month / year does not contain the currently selected day number,
    // change the selected day number to the last day of the selected month or year.
    //      e.g. Switching from Mar to Apr when Mar 31 is selected -> Apr 30
    //      e.g. Switching from 2012 to 2013 when Feb 29, 2012 is selected -> Feb 28, 2013
    private fun adjustDayInMonthIfNeeded(calendar: Calendar): Calendar {
        val day = calendar[Calendar.DAY_OF_MONTH]
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        if (day > daysInMonth) {
            calendar[Calendar.DAY_OF_MONTH] = daysInMonth
        }
        return mDateRangeLimiter!!.setToNearestDate(calendar)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.amdp_date_picker_year) {
            mYearPickerPopup!!.show()
        } else if (v.id == R.id.amdp_date_picker_month_and_day) {
            setCurrentView(MONTH_AND_DAY_VIEW)
        } else if (v.id == R.id.amdp_month_picker) {
            val popupMenu =
                PopupMenu(mMonthPickerView!!.context, mMonthPickerView)
            val calendar = Calendar.getInstance()
            for (i in 0..11) {
                calendar[0, i + 1] = 0
                popupMenu.menu.add(
                    Menu.NONE,
                    i,
                    1,
                    MONTH_FORMAT_STAND_ALONE.format(calendar.time)
                )
            }
            popupMenu.setOnMenuItemClickListener { item ->
                mDayPickerView!!.scrollToMonth(item.itemId)
                mMonthPickerView!!.text = item.title
                false
            }
            popupMenu.show()
        } else if (v.id == R.id.amdp_month_arrow_left) {
            mDayPickerView!!.scrollToPrevMonth()
        } else if (v.id == R.id.amdp_month_arrow_right) {
            mDayPickerView!!.scrollToNextMonth()
        }
    }

    override fun onYearSelected(year: Int) {
        mYearPickerPopup!!.dismiss()
        mCalendar[Calendar.YEAR] = year
        mCalendar = adjustDayInMonthIfNeeded(mCalendar)
        updatePickers()
        setCurrentView(MONTH_AND_DAY_VIEW)
        updateDisplay(true)
    }

    override fun onDayOfMonthSelected(year: Int, month: Int, day: Int) {
        mCalendar[Calendar.YEAR] = year
        mCalendar[Calendar.MONTH] = month
        mCalendar[Calendar.DAY_OF_MONTH] = day
        updatePickers()
        updateDisplay(true)
        if (mAutoDismiss) {
            notifyOnDateListener()
            dismiss()
        }
    }

    private fun updatePickers() {
        for (listener in mListeners) listener!!.onDateChanged()
    }

    override val selectedDay: CalendarDay
        get() = CalendarDay(mCalendar, timeZone)

    override val startDate: Calendar
        get() = mDateRangeLimiter!!.startDate

    override val endDate: Calendar
        get() = mDateRangeLimiter!!.endDate

    override val minYear: Int
        get() = mDateRangeLimiter!!.minYear

    override val maxYear: Int
        get() = mDateRangeLimiter!!.maxYear

    override fun isOutOfRange(year: Int, month: Int, day: Int): Boolean {
        return mDateRangeLimiter!!.isOutOfRange(year, month, day)
    }

    override var firstDayOfWeek: Int
        get() = mWeekStart
        set(startOfWeek) {
            require(!(startOfWeek < Calendar.SUNDAY || startOfWeek > Calendar.SATURDAY)) {
                "Value must be between Calendar.SUNDAY and " +
                        "Calendar.SATURDAY"
            }
            mWeekStart = startOfWeek
            if (mDayPickerView != null) {
                mDayPickerView!!.onChange()
            }
        }

    override fun registerOnDateChangedListener(listener: OnDateChangedListener?) {
        mListeners.add(listener)
    }

    override fun unregisterOnDateChangedListener(listener: OnDateChangedListener?) {
        mListeners.remove(listener)
    }

    /**
     * Set which timezone the picker should use
     *
     * @param timeZone The timezone to use
     */
    override var timeZone: TimeZone?
        get() = if (mTimezone == null) TimeZone.getDefault() else mTimezone
        set(timeZone) {
            mTimezone = timeZone
            mCalendar.timeZone = timeZone
            YEAR_FORMAT.timeZone = timeZone
            MONTH_FORMAT.timeZone = timeZone
            DAY_FORMAT.timeZone = timeZone
        }

    fun notifyOnDateListener() {
        if (onDateSetListener != null) {
            onDateSetListener!!.onDateSet(
                this@DatePickerFragmentDialog,
                mCalendar[Calendar.YEAR],
                mCalendar[Calendar.MONTH],
                mCalendar[Calendar.DAY_OF_MONTH]
            )
        }
    }

    fun setMonthYearPickersVisible(monthYearPickersVisible: Boolean) {
        this.monthYearPickersVisible = monthYearPickersVisible
    }

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    interface OnDateSetListener {
        /**
         * @param view        The view associated with this listener.
         * @param year        The year that was setResources.
         * @param monthOfYear The month that was setResources (0-11) for compatibility
         * with [Calendar].
         * @param dayOfMonth  The day of the month that was setResources.
         */
        fun onDateSet(
            view: DatePickerFragmentDialog?,
            year: Int,
            monthOfYear: Int,
            dayOfMonth: Int
        )
    }

    /**
     * The callback used to notify other date picker components of a change in selected date.
     */
    interface OnDateChangedListener {
        fun onDateChanged()
    }

    companion object {
        private const val UNINITIALIZED = -1
        private const val MONTH_AND_DAY_VIEW = 0
        private const val YEAR_VIEW = 1
        private const val KEY_SELECTED_YEAR = "year"
        private const val KEY_SELECTED_MONTH = "month"
        private const val KEY_SELECTED_DAY = "day"
        private const val KEY_LIST_POSITION = "list_position"
        private const val KEY_WEEK_START = "week_start"
        private const val KEY_CURRENT_VIEW = "current_view"
        private const val KEY_HIGHLIGHTED_DAYS = "highlighted_days"
        private const val KEY_THEME_DARK = "theme_dark"
        private const val KEY_THEME_DARK_CHANGED = "theme_dark_changed"
        private const val KEY_ACCENT = "accent"
        private const val KEY_DISMISS = "dismiss"
        private const val KEY_AUTO_DISMISS = "auto_dismiss"
        private const val KEY_DEFAULT_VIEW = "default_view"
        private const val KEY_TITLE = "title"
        private const val KEY_OK_RESID = "ok_resid"
        private const val KEY_OK_STRING = "ok_string"
        private const val KEY_OK_COLOR = "ok_color"
        private const val KEY_CANCEL_RESID = "cancel_resid"
        private const val KEY_CANCEL_STRING = "cancel_string"
        private const val KEY_CANCEL_COLOR = "cancel_color"
        private const val KEY_TIMEZONE = "timezone"
        private const val KEY_DATERANGELIMITER = "daterangelimiter"
        private const val ANIMATION_DURATION = 300
        private const val ANIMATION_DELAY = 500
        private val YEAR_FORMAT =
            SimpleDateFormat("yyyy", Locale.getDefault())
        private val MONTH_FORMAT =
            SimpleDateFormat("MMMM", Locale.getDefault())
        private val MONTH_FORMAT_STAND_ALONE =
            SimpleDateFormat("LLLL", Locale.getDefault())
        private val DAY_FORMAT =
            SimpleDateFormat("dd", Locale.getDefault())

        /**
         * @param callBack    How the parent is notified that the date is setResources.
         * @param year        The initial year of the dialog.
         * @param monthOfYear The initial month of the dialog.
         * @param dayOfMonth  The initial day of the dialog.
         */
        fun newInstance(
            callBack: OnDateSetListener?,
            year: Int,
            monthOfYear: Int,
            dayOfMonth: Int
        ): DatePickerFragmentDialog {
            val ret = DatePickerFragmentDialog()
            ret.initialize(callBack, year, monthOfYear, dayOfMonth)
            return ret
        }

        fun newInstance(callback: OnDateSetListener?): DatePickerFragmentDialog {
            val now = Calendar.getInstance()
            return newInstance(
                callback,
                now[Calendar.YEAR],
                now[Calendar.MONTH],
                now[Calendar.DAY_OF_MONTH]
            )
        }
    }

    init {
        // Empty constructor required for dialog fragment.
        mWeekStart = mCalendar.firstDayOfWeek
    }
}