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
 * limitations under the License
 */
package datatouch.uikit.components.timepicker

import android.app.ActionBar
import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import datatouch.uikit.R
import datatouch.uikit.utils.AnimationUtils.getPulseAnimator
import datatouch.uikit.utils.ColorUtils
import datatouch.uikit.utils.ColorUtils.darkenColor
import datatouch.uikit.utils.datetime.DatePickerUtils.tryAccessibilityAnnounce
import datatouch.uikit.utils.datetime.TimePickerCallback
import java.text.DateFormatSymbols
import java.util.*

/**
 * Dialog to set a time.
 */
typealias OnTimeSetListener = (timePoint: TimePoint) -> Unit

class TimePickerDialog : AppCompatDialogFragment(), RadialPickerLayout.OnValueSelectedListener, TimePickerController {
    enum class Version {
        VERSION_1, VERSION_2
    }

    /**
     * Get a reference to the OnTimeSetListener callback
     * @return OnTimeSetListener the callback
     */
    var onTimeSetListener: OnTimeSetListener? = null
    private var mOnCancelListener: DialogInterface.OnCancelListener? = null
    private var mOnDismissListener: DialogInterface.OnDismissListener? = null
    private var mHapticFeedbackController: HapticFeedbackController? = null
    private var mCancelButton: Button? = null
    private var mOkButton: Button? = null
    private var mHourView: TextView? = null
    private var mHourSpaceView: TextView? = null
    private var mMinuteView: TextView? = null
    private var mMinuteSpaceView: TextView? = null
    private var mSecondView: TextView? = null
    private var mSecondSpaceView: TextView? = null
    private var mAmTextView: TextView? = null
    private var mPmTextView: TextView? = null
    private var mAmPmLayout: View? = null
    private var mTimePicker: RadialPickerLayout? = null
    private var mSelectedColor = 0
    private var mUnselectedColor = 0
    private var mAmText: String? = null
    private var mPmText: String? = null
    private var mAllowAutoAdvance = false
    private var mInitialTime: TimePoint? = null
    private var mIs24HourMode = false

    /**
     * Set a title. NOTE: this will only take effect with the next onCreateView
     */
    var title: String? = null
    private var mThemeDark = false
    private var mThemeDarkChanged = false
    private var mVibrate = false
    private var mAccentColor: Int? = null
    private var mDismissOnPause = false
    private var mEnableSeconds = false
    private var mEnableMinutes = false
    private var mOkResid = 0
    private var mOkString: String? = null
    private var mOkColor: Int? = null
    private var mCancelResid = 0
    private var mCancelString: String? = null
    private var mCancelColor: Int? = null

    /**
     * Set which layout version the picker should use
     * @param version The version to use
     */
    override var version: Version? = null
    private var mDefaultLimiter = DefaultTimepointLimiter()
    private var mLimiter: TimePointLimiter? = mDefaultLimiter
    private var mLocale = Locale.getDefault()

    // For hardware IME input.
    private var mPlaceholderText = 0.toChar()
    private var mDoublePlaceholderText: String? = null
    private var mDeletedKeyFormat: String? = null
    private var mInKbMode = false
    private var mTypedTimes: ArrayList<Int>? = null
    private var mLegalTimesTree: Node? = null
    private var mAmKeyCode = 0
    private var mPmKeyCode = 0

    // Accessibility strings.
    private var mHourPickerDescription: String? = null
    private var mSelectHours: String? = null
    private var mMinutePickerDescription: String? = null
    private var mSelectMinutes: String? = null
    private var mSecondPickerDescription: String? = null
    private var mSelectSeconds: String? = null

    /**
     * The callback interface used to indicate the user is done filling in
     * the time (they clicked on the 'Set' button).
     */


    fun initialize(callback: OnTimeSetListener,
                   timeData: TimePoint, is24HourMode: Boolean) {
        onTimeSetListener = callback
        mInitialTime = TimePoint(timeData.hour, timeData.minute, timeData.second)
        mIs24HourMode = is24HourMode
        mInKbMode = false
        title = ""
        mThemeDark = false
        mThemeDarkChanged = false
        mVibrate = true
        mDismissOnPause = false
        mEnableSeconds = false
        mEnableMinutes = true
        mOkResid = R.string.mdtp_ok
        mCancelResid = R.string.mdtp_cancel
        version = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) Version.VERSION_1 else Version.VERSION_2
        // Throw away the current TimePicker, which might contain old state if the dialog instance is reused
        mTimePicker = null
    }

    /**
     * Set the accent color of this dialog
     * @param color the accent color you want
     */
    fun setAccentColor(color: String?) {
        mAccentColor = Color.parseColor(color)
    }

    /**
     * Set the accent color of this dialog
     * @param color the accent color you want
     */
    fun setAccentColor(@ColorInt color: Int) {
        mAccentColor = Color.argb(255, Color.red(color), Color.green(color), Color.blue(color))
    }

    /**
     * Set the text color of the OK button
     * @param color the color you want
     */
    fun setOkColor(color: String?) {
        mOkColor = Color.parseColor(color)
    }

    /**
     * Set the text color of the OK button
     * @param color the color you want
     */
    fun setOkColor(@ColorInt color: Int) {
        mOkColor = Color.argb(255, Color.red(color), Color.green(color), Color.blue(color))
    }

    /**
     * Set the text color of the Cancel button
     * @param color the color you want
     */
    fun setCancelColor(color: String?) {
        mCancelColor = Color.parseColor(color)
    }

    /**
     * Set the text color of the Cancel button
     * @param color the color you want
     */
    fun setCancelColor(@ColorInt color: Int) {
        mCancelColor = Color.argb(255, Color.red(color), Color.green(color), Color.blue(color))
    }

    /**
     * Set a dark or light theme. NOTE: this will only take effect for the next onCreateView.
     */
    override var isThemeDark: Boolean
        get() = mThemeDark
        set(dark) {
            mThemeDark = dark
            mThemeDarkChanged = true
        }

    override fun is24HourMode(): Boolean {
        return mIs24HourMode
    }

    override val accentColor: Int
        get() = mAccentColor!!

    /**
     * Set whether the device should vibrate when touching fields
     * @param vibrate true if the device should vibrate when touching a field
     */
    fun vibrate(vibrate: Boolean) {
        mVibrate = vibrate
    }

    /**
     * Set whether the picker should dismiss itself when it's pausing or whether it should try to survive an orientation change
     * @param dismissOnPause true if the picker should dismiss itself
     */
    fun dismissOnPause(dismissOnPause: Boolean) {
        mDismissOnPause = dismissOnPause
    }

    /**
     * Set whether an additional picker for seconds should be shown
     * Will enable minutes picker as well if seconds picker should be shown
     * @param enableSeconds true if the seconds picker should be shown
     */
    fun enableSeconds(enableSeconds: Boolean) {
        if (enableSeconds) mEnableMinutes = true
        mEnableSeconds = enableSeconds
    }

    /**
     * Set whether the picker for minutes should be shown
     * Will disable seconds if minutes are disbled
     * @param enableMinutes true if minutes picker should be shown
     */
    fun enableMinutes(enableMinutes: Boolean) {
        if (!enableMinutes) mEnableSeconds = false
        mEnableMinutes = enableMinutes
    }

    fun setMinTime(hour: Int, minute: Int, second: Int) {
        setMinTime(TimePoint(hour, minute, second))
    }

    fun setMinTime(minTime: TimePoint?) {
        mDefaultLimiter.setMinTime(minTime!!)
    }

    fun setMaxTime(hour: Int, minute: Int, second: Int) {
        setMaxTime(TimePoint(hour, minute, second))
    }

    fun setMaxTime(maxTime: TimePoint?) {
        mDefaultLimiter.setMaxTime(maxTime!!)
    }

    /**
     * Pass in an array of Timepoints which are the only possible selections.
     * Try to specify Timepoints only up to the resolution of your picker (i.e. do not add seconds
     * if the resolution of the picker is minutes)
     * @param selectableTimes Array of Timepoints which are the only valid selections in the picker
     */
    fun setSelectableTimes(selectableTimes: Array<TimePoint>?) {
        mDefaultLimiter.setSelectableTimes(selectableTimes!!)
    }

    /**
     * Pass in an array of Timepoints that cannot be selected. These take precedence over
     * [TimePickerDialog.setSelectableTimes]
     * Be careful when using this without selectableTimes: rounding to a valid Timepoint is a
     * very expensive operation if a lot of consecutive Timepoints are disabled
     * Try to specify Timepoints only up to the resolution of your picker (i.e. do not add seconds
     * if the resolution of the picker is minutes)
     * @param disabledTimes Array of Timepoints which are disabled in the resulting picker
     */
    fun setDisabledTimes(disabledTimes: Array<TimePoint>?) {
        mDefaultLimiter.setDisabledTimes(disabledTimes!!)
    }

    /**
     * Set the interval for selectable times in the TimePickerDialog
     * This is a convenience wrapper around [TimePickerDialog.setSelectableTimes]
     * The interval for all three time components can be set independently
     * If you are not using the seconds / minutes picker, set the respective item to 60 for
     * better performance.
     * @param hourInterval The interval between 2 selectable hours ([1,24])
     * @param minuteInterval The interval between 2 selectable minutes ([1,60])
     * @param secondInterval The interval between 2 selectable seconds ([1,60])
     */
    fun setTimeInterval(@IntRange(from = 1, to = 24) hourInterval: Int,
                        @IntRange(from = 1, to = 60) minuteInterval: Int,
                        @IntRange(from = 1, to = 60) secondInterval: Int) {
        val timePoints: MutableList<TimePoint> = ArrayList()
        var hour = 0
        while (hour < 24) {
            var minute = 0
            while (minute < 60) {
                var second = 0
                while (second < 60) {
                    timePoints.add(TimePoint(hour, minute, second))
                    second += secondInterval
                }
                minute += minuteInterval
            }
            hour += hourInterval
        }
        setSelectableTimes(timePoints.toTypedArray())
    }

    /**
     * Set the interval for selectable times in the TimePickerDialog
     * This is a convenience wrapper around setSelectableTimes
     * The interval for all three time components can be set independently
     * If you are not using the seconds / minutes picker, set the respective item to 60 for
     * better performance.
     * @param hourInterval The interval between 2 selectable hours ([1,24])
     * @param minuteInterval The interval between 2 selectable minutes ([1,60])
     */
    fun setTimeInterval(@IntRange(from = 1, to = 24) hourInterval: Int,
                        @IntRange(from = 1, to = 60) minuteInterval: Int) {
        setTimeInterval(hourInterval, minuteInterval, 60)
    }

    /**
     * Set the interval for selectable times in the TimePickerDialog
     * This is a convenience wrapper around setSelectableTimes
     * The interval for all three time components can be set independently
     * If you are not using the seconds / minutes picker, set the respective item to 60 for
     * better performance.
     * @param hourInterval The interval between 2 selectable hours ([1,24])
     */
    fun setTimeInterval(@IntRange(from = 1, to = 24) hourInterval: Int) {
        setTimeInterval(hourInterval, 60)
    }

    fun setOnCancelListener(onCancelListener: DialogInterface.OnCancelListener?) {
        mOnCancelListener = onCancelListener
    }

    fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener?) {
        mOnDismissListener = onDismissListener
    }

    /**
     * Set the time that will be shown when the picker opens for the first time
     * Overrides the value given in newInstance()
     *
     * @param hourOfDay the hour of the day
     * @param minute the minute of the hour
     * @param second the second of the minute
     */
    @Deprecated("""in favor of {@link #setInitialSelection(int, int, int)}
      """)
    fun setStartTime(hourOfDay: Int, minute: Int, second: Int) {
        mInitialTime = roundToNearest(TimePoint(hourOfDay, minute, second))
        mInKbMode = false
    }

    /**
     * Set the time that will be shown when the picker opens for the first time
     * Overrides the value given in newInstance
     *
     * @param hourOfDay the hour of the day
     * @param minute the minute of the hour
     */
    @Deprecated("""in favor of {@link #setInitialSelection(int, int)}
      """)
    fun setStartTime(hourOfDay: Int, minute: Int) {
        setStartTime(hourOfDay, minute, 0)
    }

    /**
     * Set the time that will be shown when the picker opens for the first time
     * Overrides the value given in newInstance()
     * @param hourOfDay the hour of the day
     * @param minute the minute of the hour
     * @param second the second of the minute
     */
    fun setInitialSelection(hourOfDay: Int, minute: Int, second: Int) {
        setInitialSelection(TimePoint(hourOfDay, minute, second))
    }

    /**
     * Set the time that will be shown when the picker opens for the first time
     * Overrides the value given in newInstance
     * @param hourOfDay the hour of the day
     * @param minute the minute of the hour
     */
    fun setInitialSelection(hourOfDay: Int, minute: Int) {
        setInitialSelection(hourOfDay, minute, 0)
    }

    /**
     * Set the time that will be shown when the picker opens for the first time
     * Overrides the value given in newInstance()
     * @param time the Timepoint selected when the Dialog opens
     */
    fun setInitialSelection(time: TimePoint) {
        mInitialTime = roundToNearest(time)
        mInKbMode = false
    }

    /**
     * Set the label for the Ok button (max 12 characters)
     * @param okString A literal String to be used as the Ok button label
     */
    fun setOkText(okString: String?) {
        mOkString = okString
    }

    /**
     * Set the label for the Ok button (max 12 characters)
     * @param okResid A resource ID to be used as the Ok button label
     */
    fun setOkText(@StringRes okResid: Int) {
        mOkString = null
        mOkResid = okResid
    }

    /**
     * Set the label for the Cancel button (max 12 characters)
     * @param cancelString A literal String to be used as the Cancel button label
     */
    fun setCancelText(cancelString: String?) {
        mCancelString = cancelString
    }

    /**
     * Set the label for the Cancel button (max 12 characters)
     * @param cancelResid A resource ID to be used as the Cancel button label
     */
    fun setCancelText(@StringRes cancelResid: Int) {
        mCancelString = null
        mCancelResid = cancelResid
    }

    /**
     * Pass in a custom implementation of TimeLimiter
     * Disables setSelectableTimes, setDisabledTimes, setTimeInterval, setMinTime and setMaxTime
     * @param limiter A custom implementation of TimeLimiter
     */
    fun setTimepointLimiter(limiter: TimePointLimiter?) {
        mLimiter = limiter
    }

    /**
     * Set the Locale which will be used to generate various strings throughout the picker
     * @param locale Locale
     */
    fun setLocale(locale: Locale?) {
        mLocale = locale
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_INITIAL_TIME)
                && savedInstanceState.containsKey(KEY_IS_24_HOUR_VIEW)) {
            mInitialTime = savedInstanceState.getParcelable(KEY_INITIAL_TIME)
            mIs24HourMode = savedInstanceState.getBoolean(KEY_IS_24_HOUR_VIEW)
            mInKbMode = savedInstanceState.getBoolean(KEY_IN_KB_MODE)
            title = savedInstanceState.getString(KEY_TITLE)
            mThemeDark = savedInstanceState.getBoolean(KEY_THEME_DARK)
            mThemeDarkChanged = savedInstanceState.getBoolean(KEY_THEME_DARK_CHANGED)
            if (savedInstanceState.containsKey(KEY_ACCENT)) mAccentColor = savedInstanceState.getInt(KEY_ACCENT)
            mVibrate = savedInstanceState.getBoolean(KEY_VIBRATE)
            mDismissOnPause = savedInstanceState.getBoolean(KEY_DISMISS)
            mEnableSeconds = savedInstanceState.getBoolean(KEY_ENABLE_SECONDS)
            mEnableMinutes = savedInstanceState.getBoolean(KEY_ENABLE_MINUTES)
            mOkResid = savedInstanceState.getInt(KEY_OK_RESID)
            mOkString = savedInstanceState.getString(KEY_OK_STRING)
            if (savedInstanceState.containsKey(KEY_OK_COLOR)) mOkColor = savedInstanceState.getInt(KEY_OK_COLOR)
            if (mOkColor == Int.MAX_VALUE) mOkColor = null
            mCancelResid = savedInstanceState.getInt(KEY_CANCEL_RESID)
            mCancelString = savedInstanceState.getString(KEY_CANCEL_STRING)
            if (savedInstanceState.containsKey(KEY_CANCEL_COLOR)) mCancelColor = savedInstanceState.getInt(KEY_CANCEL_COLOR)
            version = savedInstanceState.getSerializable(KEY_VERSION) as Version?
            mLimiter = savedInstanceState.getParcelable(KEY_TIMEPOINTLIMITER)
            mLocale = savedInstanceState.getSerializable(KEY_LOCALE) as Locale?

            /*
            If the user supplied a custom limiter, we need to create a new default one to prevent
            null pointer exceptions on the configuration methods
            If the user did not supply a custom limiter we need to ensure both mDefaultLimiter
            and mLimiter are the same reference, so that the config methods actually
            affect the behaviour of the picker (in the unlikely event the user reconfigures
            the picker when it is shown)
             */mDefaultLimiter = if (mLimiter is DefaultTimepointLimiter) mLimiter as DefaultTimepointLimiter else DefaultTimepointLimiter()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val viewRes = if (version == Version.VERSION_1) R.layout.mdtp_time_picker_dialog else R.layout.mdtp_time_picker_dialog_v2
        val view = inflater.inflate(viewRes, container, false)
        val keyboardListener = KeyboardListener()
        view.findViewById<View>(R.id.mdtp_time_picker_dialog).setOnKeyListener(keyboardListener)

        // If an accent color has not been set manually, get it from the context
        if (mAccentColor == null) {
            mAccentColor = ColorUtils.getAccentColorFromThemeIfAvailable(activity!!)
        }

        // if theme mode has not been set by java code, check if it is specified in Style.xml
        if (!mThemeDarkChanged) {
            mThemeDark = ColorUtils.isDarkTheme(activity!!, mThemeDark)
        }
        val res = resources
        val context: Context = requireActivity()
        mHourPickerDescription = res.getString(R.string.mdtp_hour_picker_description)
        mSelectHours = res.getString(R.string.mdtp_select_hours)
        mMinutePickerDescription = res.getString(R.string.mdtp_minute_picker_description)
        mSelectMinutes = res.getString(R.string.mdtp_select_minutes)
        mSecondPickerDescription = res.getString(R.string.mdtp_second_picker_description)
        mSelectSeconds = res.getString(R.string.mdtp_select_seconds)
        mSelectedColor = ContextCompat.getColor(context, R.color.mdtp_white)
        mUnselectedColor = ContextCompat.getColor(context, R.color.mdtp_accent_color_focused)
        mHourView = view.findViewById(R.id.mdtp_hours)
        mHourView?.setOnKeyListener(keyboardListener)
        mHourSpaceView = view.findViewById(R.id.mdtp_hour_space)
        mMinuteSpaceView = view.findViewById(R.id.mdtp_minutes_space)
        mMinuteView = view.findViewById(R.id.mdtp_minutes)
        mMinuteView?.setOnKeyListener(keyboardListener)
        mSecondSpaceView = view.findViewById(R.id.mdtp_seconds_space)
        mSecondView = view.findViewById(R.id.mdtp_seconds)
        mSecondView?.setOnKeyListener(keyboardListener)
        mAmTextView = view.findViewById(R.id.mdtp_am_label)
        mAmTextView?.setOnKeyListener(keyboardListener)
        mPmTextView = view.findViewById(R.id.mdtp_pm_label)
        mPmTextView?.setOnKeyListener(keyboardListener)
        mAmPmLayout = view.findViewById(R.id.mdtp_ampm_layout)
        val amPmTexts = DateFormatSymbols(mLocale).amPmStrings
        mAmText = amPmTexts[0]
        mPmText = amPmTexts[1]
        mHapticFeedbackController = HapticFeedbackController(activity!!)
        if (mTimePicker != null) {
            mInitialTime = TimePoint(mTimePicker!!.hours, mTimePicker!!.minutes, mTimePicker!!.seconds)
        }
        mInitialTime = roundToNearest(mInitialTime!!)
        mTimePicker = view.findViewById(R.id.mdtp_time_picker)
        mTimePicker?.setOnValueSelectedListener(this)
        mTimePicker?.setOnKeyListener(keyboardListener)
        mTimePicker?.initialize(activity, mLocale, this, mInitialTime!!, mIs24HourMode)
        var currentItemShowing = HOUR_INDEX
        if (savedInstanceState != null &&
                savedInstanceState.containsKey(KEY_CURRENT_ITEM_SHOWING)) {
            currentItemShowing = savedInstanceState.getInt(KEY_CURRENT_ITEM_SHOWING)
        }
        setCurrentItemShowing(currentItemShowing, false, true, true)
        mTimePicker?.invalidate()
        mHourView?.setOnClickListener(View.OnClickListener { v: View? ->
            setCurrentItemShowing(HOUR_INDEX, true, false, true)
            tryVibrate()
        })
        mMinuteView?.setOnClickListener { v: View? ->
            setCurrentItemShowing(MINUTE_INDEX, true, false, true)
            tryVibrate()
        }
        mSecondView?.setOnClickListener { view1: View? ->
            setCurrentItemShowing(SECOND_INDEX, true, false, true)
            tryVibrate()
        }
        mOkButton = view.findViewById(R.id.mdtp_ok)
        mOkButton?.setOnClickListener { v: View? ->
            if (mInKbMode && isTypedTimeFullyLegal) {
                finishKbMode(false)
            } else {
                tryVibrate()
            }
            notifyOnDateListener()
            dismiss()
        }
        mOkButton?.setOnKeyListener(keyboardListener)
        mOkButton?.typeface = ResourcesCompat.getFont(context, R.font.robotomedium)
        if (mOkString != null) mOkButton?.text = mOkString else mOkButton?.setText(mOkResid)
        mCancelButton = view.findViewById(R.id.mdtp_cancel)
        mCancelButton?.setOnClickListener(View.OnClickListener { v: View? ->
            tryVibrate()
            if (dialog != null) dialog!!.cancel()
        })
        mCancelButton?.typeface = ResourcesCompat.getFont(context, R.font.robotomedium)
        if (mCancelString != null) mCancelButton?.text = mCancelString else mCancelButton?.setText(mCancelResid)
        mCancelButton?.visibility = if (isCancelable) View.VISIBLE else View.GONE

        // Enable or disable the AM/PM view.
        if (mIs24HourMode) {
            mAmPmLayout?.visibility = View.GONE
        } else {
            val listener = View.OnClickListener { v: View? ->
                // Don't do anything if either AM or PM are disabled
                if (isAmDisabled || isPmDisabled) {
                    return@OnClickListener
                }
                tryVibrate()
                var amOrPm: Int = mTimePicker!!.isCurrentlyAmOrPm
                if (amOrPm == AM) {
                    amOrPm = PM
                } else if (amOrPm == PM) {
                    amOrPm = AM
                }
                mTimePicker?.setAmOrPm(amOrPm)
            }
            mAmTextView?.visibility = View.GONE
            mPmTextView?.visibility = View.VISIBLE
            mAmPmLayout?.setOnClickListener(listener)
            if (version == Version.VERSION_2) {
                mAmTextView?.text = mAmText
                mPmTextView?.text = mPmText
                mAmTextView?.visibility = View.VISIBLE
            }
            updateAmPmDisplay(if (mInitialTime!!.isAM) AM else PM)
        }

        // Disable seconds picker
        if (!mEnableSeconds) {
            mSecondView?.visibility = View.GONE
            view.findViewById<View>(R.id.mdtp_separator_seconds).visibility = View.GONE
        }

        // Disable minutes picker
        if (!mEnableMinutes) {
            mMinuteSpaceView?.visibility = View.GONE
            view.findViewById<View>(R.id.mdtp_separator).visibility = View.GONE
        }

        // Center stuff depending on what's visible
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        // Landscape layout is radically different
        if (isLandscape) {
            if (!mEnableMinutes && !mEnableSeconds) {
                // Just the hour
                // Put the hour above the center
                val paramsHour = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                paramsHour.addRule(RelativeLayout.ABOVE, R.id.mdtp_center_view)
                paramsHour.addRule(RelativeLayout.CENTER_HORIZONTAL)
                mHourSpaceView?.layoutParams = paramsHour
                if (mIs24HourMode) {
                    // Hour + Am/Pm indicator
                    // Put the am / pm indicator next to the hour
                    val paramsAmPm = RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    paramsAmPm.addRule(RelativeLayout.RIGHT_OF, R.id.mdtp_hour_space)
                    mAmPmLayout?.layoutParams = paramsAmPm
                }
            } else if (!mEnableSeconds && mIs24HourMode) {
                // Hour + Minutes
                // Put the separator above the center
                val paramsSeparator = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                paramsSeparator.addRule(RelativeLayout.CENTER_HORIZONTAL)
                paramsSeparator.addRule(RelativeLayout.ABOVE, R.id.mdtp_center_view)
                val separatorView = view.findViewById<TextView>(R.id.mdtp_separator)
                separatorView.layoutParams = paramsSeparator
            } else if (!mEnableSeconds) {
                // Hour + Minutes + Am/Pm indicator
                // Put separator above the center
                val paramsSeparator = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                paramsSeparator.addRule(RelativeLayout.CENTER_HORIZONTAL)
                paramsSeparator.addRule(RelativeLayout.ABOVE, R.id.mdtp_center_view)
                val separatorView = view.findViewById<TextView>(R.id.mdtp_separator)
                separatorView.layoutParams = paramsSeparator
                // Put the am/pm indicator below the separator
                val paramsAmPm = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                paramsAmPm.addRule(RelativeLayout.CENTER_IN_PARENT)
                paramsAmPm.addRule(RelativeLayout.BELOW, R.id.mdtp_center_view)
                mAmPmLayout?.layoutParams = paramsAmPm
            } else if (mIs24HourMode) {
                // Hour + Minutes + Seconds
                // Put the separator above the center
                val paramsSeparator = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                paramsSeparator.addRule(RelativeLayout.CENTER_HORIZONTAL)
                paramsSeparator.addRule(RelativeLayout.ABOVE, R.id.mdtp_seconds_space)
                val separatorView = view.findViewById<TextView>(R.id.mdtp_separator)
                separatorView.layoutParams = paramsSeparator
                // Center the seconds
                val paramsSeconds = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                paramsSeconds.addRule(RelativeLayout.CENTER_IN_PARENT)
                mSecondSpaceView?.layoutParams = paramsSeconds
            } else {
                // Hour + Minutes + Seconds + Am/Pm Indicator
                // Put the seconds on the center
                val paramsSeconds = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                paramsSeconds.addRule(RelativeLayout.CENTER_IN_PARENT)
                mSecondSpaceView?.layoutParams = paramsSeconds
                // Put the separator above the seconds
                val paramsSeparator = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                paramsSeparator.addRule(RelativeLayout.CENTER_HORIZONTAL)
                paramsSeparator.addRule(RelativeLayout.ABOVE, R.id.mdtp_seconds_space)
                val separatorView = view.findViewById<TextView>(R.id.mdtp_separator)
                separatorView.layoutParams = paramsSeparator
                // Put the Am/Pm indicator below the seconds
                val paramsAmPm = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                paramsAmPm.addRule(RelativeLayout.CENTER_HORIZONTAL)
                paramsAmPm.addRule(RelativeLayout.BELOW, R.id.mdtp_seconds_space)
                mAmPmLayout?.layoutParams = paramsAmPm
            }
        } else if (mIs24HourMode && !mEnableSeconds && mEnableMinutes) {
            // center first separator
            val paramsSeparator = RelativeLayout.LayoutParams(
                    ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT
            )
            paramsSeparator.addRule(RelativeLayout.CENTER_IN_PARENT)
            val separatorView = view.findViewById<TextView>(R.id.mdtp_separator)
            separatorView.layoutParams = paramsSeparator
        } else if (!mEnableMinutes && !mEnableSeconds) {
            // center the hour
            val paramsHour = RelativeLayout.LayoutParams(
                    ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT
            )
            paramsHour.addRule(RelativeLayout.CENTER_IN_PARENT)
            mHourSpaceView?.layoutParams = paramsHour
            if (!mIs24HourMode) {
                val paramsAmPm = RelativeLayout.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT
                )
                paramsAmPm.addRule(RelativeLayout.RIGHT_OF, R.id.mdtp_hour_space)
                paramsAmPm.addRule(RelativeLayout.ALIGN_BASELINE, R.id.mdtp_hour_space)
                mAmPmLayout?.layoutParams = paramsAmPm
            }
        } else if (mEnableSeconds) {
            // link separator to minutes
            val separator = view.findViewById<View>(R.id.mdtp_separator)
            val paramsSeparator = RelativeLayout.LayoutParams(
                    ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT
            )
            paramsSeparator.addRule(RelativeLayout.LEFT_OF, R.id.mdtp_minutes_space)
            paramsSeparator.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
            separator.layoutParams = paramsSeparator
            if (!mIs24HourMode) {
                // center minutes
                val paramsMinutes = RelativeLayout.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT
                )
                paramsMinutes.addRule(RelativeLayout.CENTER_IN_PARENT)
                mMinuteSpaceView?.layoutParams = paramsMinutes
            } else {
                // move minutes to right of center
                val paramsMinutes = RelativeLayout.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT
                )
                paramsMinutes.addRule(RelativeLayout.RIGHT_OF, R.id.mdtp_center_view)
                mMinuteSpaceView?.layoutParams = paramsMinutes
            }
        }
        mAllowAutoAdvance = true
        setHour(mInitialTime!!.hour, true)
        setMinute(mInitialTime!!.minute)
        setSecond(mInitialTime!!.second)

        // Set up for keyboard mode.
        mDoublePlaceholderText = res.getString(R.string.mdtp_time_placeholder)
        mDeletedKeyFormat = res.getString(R.string.mdtp_deleted_key)
        mPlaceholderText = mDoublePlaceholderText!![0]
        mPmKeyCode = -1
        mAmKeyCode = mPmKeyCode
        generateLegalTimesTree()
        if (mInKbMode && savedInstanceState != null) {
            mTypedTimes = savedInstanceState.getIntegerArrayList(KEY_TYPED_TIMES)
            tryStartingKbMode(-1)
            mHourView?.invalidate()
        } else if (mTypedTimes == null) {
            mTypedTimes = ArrayList()
        }

        // Set the title (if any)
        val timePickerHeader = view.findViewById<TextView>(R.id.mdtp_time_picker_header)
        if (title!!.isNotEmpty()) {
            timePickerHeader.visibility = TextView.VISIBLE
            timePickerHeader.text = title
        }

        // Set the theme at the end so that the initialize()s above don't counteract the theme.
        timePickerHeader.setBackgroundColor(darkenColor(mAccentColor!!))
        view.findViewById<View>(R.id.mdtp_time_display_background).setBackgroundColor(mAccentColor!!)
        view.findViewById<View>(R.id.mdtp_time_display).setBackgroundColor(mAccentColor!!)

        // Button text can have a different color
        if (mOkColor == null) mOkColor = mAccentColor
        mOkButton?.setTextColor(mOkColor!!)
        if (mCancelColor == null) mCancelColor = mAccentColor
        mCancelButton?.setTextColor(mCancelColor!!)
        if (dialog == null) {
            view.findViewById<View>(R.id.mdtp_done_background).visibility = View.GONE
        }
        val circleBackground = ContextCompat.getColor(context, R.color.mdtp_circle_background)
        val backgroundColor = ContextCompat.getColor(context, R.color.mdtp_background_color)
        val darkBackgroundColor = ContextCompat.getColor(context, R.color.mdtp_light_gray)
        val lightGray = ContextCompat.getColor(context, R.color.mdtp_light_gray)
        mTimePicker?.setBackgroundColor(if (mThemeDark) lightGray else circleBackground)
        view.findViewById<View>(R.id.mdtp_time_picker_dialog).setBackgroundColor(if (mThemeDark) darkBackgroundColor else backgroundColor)
        return view
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val viewGroup = view as ViewGroup?
        if (viewGroup != null) {
            viewGroup.removeAllViewsInLayout()
            val view = onCreateView(requireActivity().layoutInflater, viewGroup, null)
            viewGroup.addView(view)
        }
    }

    override fun onResume() {
        super.onResume()
        mHapticFeedbackController?.start()
    }

    override fun onPause() {
        super.onPause()
        mHapticFeedbackController?.stop()
        if (mDismissOnPause) dismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        if (mOnCancelListener != null) mOnCancelListener?.onCancel(dialog)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (mOnDismissListener != null) mOnDismissListener?.onDismiss(dialog)
    }

    override fun tryVibrate() {
        if (mVibrate) mHapticFeedbackController!!.tryVibrate()
    }

    private fun updateAmPmDisplay(amOrPm: Int) {
        if (version == Version.VERSION_2) {
            if (amOrPm == AM) {
                mAmTextView?.setTextColor(mSelectedColor)
                mPmTextView?.setTextColor(mUnselectedColor)
                tryAccessibilityAnnounce(mTimePicker, mAmText)
            } else {
                mAmTextView?.setTextColor(mUnselectedColor)
                mPmTextView?.setTextColor(mSelectedColor)
                tryAccessibilityAnnounce(mTimePicker, mPmText)
            }
        } else {
            if (amOrPm == AM) {
                mPmTextView?.text = mAmText
                tryAccessibilityAnnounce(mTimePicker, mAmText)
                mPmTextView?.contentDescription = mAmText
            } else if (amOrPm == PM) {
                mPmTextView?.text = mPmText
                tryAccessibilityAnnounce(mTimePicker, mPmText)
                mPmTextView?.contentDescription = mPmText
            } else {
                mPmTextView?.text = mDoublePlaceholderText
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (mTimePicker != null) {
            outState.putParcelable(KEY_INITIAL_TIME, mTimePicker!!.time)
            outState.putBoolean(KEY_IS_24_HOUR_VIEW, mIs24HourMode)
            outState.putInt(KEY_CURRENT_ITEM_SHOWING, mTimePicker!!.currentItemShowing)
            outState.putBoolean(KEY_IN_KB_MODE, mInKbMode)
            if (mInKbMode) {
                outState.putIntegerArrayList(KEY_TYPED_TIMES, mTypedTimes)
            }
            outState.putString(KEY_TITLE, title)
            outState.putBoolean(KEY_THEME_DARK, mThemeDark)
            outState.putBoolean(KEY_THEME_DARK_CHANGED, mThemeDarkChanged)
            if (mAccentColor != null) outState.putInt(KEY_ACCENT, mAccentColor!!)
            outState.putBoolean(KEY_VIBRATE, mVibrate)
            outState.putBoolean(KEY_DISMISS, mDismissOnPause)
            outState.putBoolean(KEY_ENABLE_SECONDS, mEnableSeconds)
            outState.putBoolean(KEY_ENABLE_MINUTES, mEnableMinutes)
            outState.putInt(KEY_OK_RESID, mOkResid)
            outState.putString(KEY_OK_STRING, mOkString)
            if (mOkColor != null) outState.putInt(KEY_OK_COLOR, mOkColor!!)
            outState.putInt(KEY_CANCEL_RESID, mCancelResid)
            outState.putString(KEY_CANCEL_STRING, mCancelString)
            if (mCancelColor != null) outState.putInt(KEY_CANCEL_COLOR, mCancelColor!!)
            outState.putSerializable(KEY_VERSION, version)
            outState.putParcelable(KEY_TIMEPOINTLIMITER, mLimiter)
            outState.putSerializable(KEY_LOCALE, mLocale)
        }
    }

    /**
     * Called by the picker for updating the header display.
     */
    override fun onValueSelected(newValue: TimePoint?) {
        setHour(newValue!!.hour, false)
        mTimePicker?.contentDescription = mHourPickerDescription + ": " + newValue.hour
        setMinute(newValue.minute)
        mTimePicker?.contentDescription = mMinutePickerDescription + ": " + newValue.minute
        setSecond(newValue.second)
        mTimePicker?.contentDescription = mSecondPickerDescription + ": " + newValue.second
        if (!mIs24HourMode) updateAmPmDisplay(if (newValue.isAM) AM else PM)
    }

    override fun advancePicker(index: Int) {
        if (!mAllowAutoAdvance) return
        if (index == HOUR_INDEX && mEnableMinutes) {
            setCurrentItemShowing(MINUTE_INDEX, true, true, false)
            val announcement = mSelectHours + ". " + mTimePicker!!.minutes
            tryAccessibilityAnnounce(mTimePicker, announcement)
        } else if (index == MINUTE_INDEX && mEnableSeconds) {
            setCurrentItemShowing(SECOND_INDEX, true, true, false)
            val announcement = mSelectMinutes + ". " + mTimePicker!!.seconds
            tryAccessibilityAnnounce(mTimePicker, announcement)
        }
    }

    override fun enablePicker() {
        if (!isTypedTimeFullyLegal) mTypedTimes!!.clear()
        finishKbMode(true)
    }

    fun isOutOfRange(current: TimePoint?): Boolean {
        return isOutOfRange(current, SECOND_INDEX)
    }

    override fun isOutOfRange(current: TimePoint?, index: Int): Boolean {
        return mLimiter!!.isOutOfRange(current, index, pickerResolution)
    }

    override val isAmDisabled: Boolean
        get() = mLimiter!!.isAmDisabled

    override val isPmDisabled: Boolean
        get() = mLimiter!!.isPmDisabled

    /**
     * Round a given Timepoint to the nearest valid Timepoint
     * @param time Timepoint - The timepoint to round
     * @return Timepoint - The nearest valid Timepoint
     */
    private fun roundToNearest(time: TimePoint): TimePoint? {
        return roundToNearest(time, null)
    }

    override fun roundToNearest(time: TimePoint?, type: TimePoint.TYPE?): TimePoint? {
        return mLimiter!!.roundToNearest(time!!, type, pickerResolution)
    }

    /**
     * Get the configured resolution of the current picker in terms of Timepoint components
     * @return Timepoint.TYPE (hour, minute or second)
     */
    val pickerResolution: TimePoint.TYPE
        get() {
            if (mEnableSeconds) return TimePoint.TYPE.SECOND
            return if (mEnableMinutes) TimePoint.TYPE.MINUTE else TimePoint.TYPE.HOUR
        }

    private fun setHour(value: Int, announce: Boolean) {
        var value = value
        val format: String
        if (mIs24HourMode) {
            format = "%02d"
        } else {
            format = "%d"
            value = value % 12
            if (value == 0) {
                value = 12
            }
        }
        val text: CharSequence = String.format(mLocale, format, value)
        mHourView?.text = text
        mHourSpaceView?.text = text
        if (announce) {
            tryAccessibilityAnnounce(mTimePicker, text)
        }
    }

    private fun setMinute(value: Int) {
        var value = value
        if (value == 60) {
            value = 0
        }
        val text: CharSequence = String.format(mLocale, "%02d", value)
        tryAccessibilityAnnounce(mTimePicker, text)
        mMinuteView?.text = text
        mMinuteSpaceView!!.text = text
    }

    private fun setSecond(value: Int) {
        var value = value
        if (value == 60) {
            value = 0
        }
        val text: CharSequence = String.format(mLocale, "%02d", value)
        tryAccessibilityAnnounce(mTimePicker, text)
        mSecondView?.text = text
        mSecondSpaceView?.text = text
    }

    // Show either Hours or Minutes.
    private fun setCurrentItemShowing(index: Int, animateCircle: Boolean, delayLabelAnimate: Boolean,
                                      announce: Boolean) {
        mTimePicker!!.setCurrentItemShowing(index, animateCircle)
        val labelToAnimate: TextView?
        when (index) {
            HOUR_INDEX -> {
                var hours = mTimePicker!!.hours
                if (!mIs24HourMode) {
                    hours = hours % 12
                }
                mTimePicker!!.contentDescription = "$mHourPickerDescription: $hours"
                if (announce) {
                    tryAccessibilityAnnounce(mTimePicker, mSelectHours)
                }
                labelToAnimate = mHourView
            }
            MINUTE_INDEX -> {
                val minutes = mTimePicker!!.minutes
                mTimePicker!!.contentDescription = "$mMinutePickerDescription: $minutes"
                if (announce) {
                    tryAccessibilityAnnounce(mTimePicker, mSelectMinutes)
                }
                labelToAnimate = mMinuteView
            }
            else -> {
                val seconds = mTimePicker!!.seconds
                mTimePicker!!.contentDescription = "$mSecondPickerDescription: $seconds"
                if (announce) {
                    tryAccessibilityAnnounce(mTimePicker, mSelectSeconds)
                }
                labelToAnimate = mSecondView
            }
        }
        val hourColor = if (index == HOUR_INDEX) mSelectedColor else mUnselectedColor
        val minuteColor = if (index == MINUTE_INDEX) mSelectedColor else mUnselectedColor
        val secondColor = if (index == SECOND_INDEX) mSelectedColor else mUnselectedColor
        mHourView!!.setTextColor(hourColor)
        mMinuteView!!.setTextColor(minuteColor)
        mSecondView!!.setTextColor(secondColor)
        val pulseAnimator = getPulseAnimator(labelToAnimate, 0.85f, 1.1f)
        if (delayLabelAnimate) {
            pulseAnimator.startDelay = PULSE_ANIMATOR_DELAY.toLong()
        }
        pulseAnimator.start()
    }

    /**
     * For keyboard mode, processes key events.
     * @param keyCode the pressed key.
     * @return true if the key was successfully processed, false otherwise.
     */
    private fun processKeyUp(keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_TAB) {
            if (mInKbMode) {
                if (isTypedTimeFullyLegal) {
                    finishKbMode(true)
                }
                return true
            }
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (mInKbMode) {
                if (!isTypedTimeFullyLegal) {
                    return true
                }
                finishKbMode(false)
            }
            if (onTimeSetListener != null) {
                onTimeSetListener?.let {
                    it(
                            TimePoint(mTimePicker!!.hours, mTimePicker!!.minutes, mTimePicker!!.seconds))
                }
            }
            dismiss()
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (mInKbMode) {
                if (mTypedTimes!!.isNotEmpty()) {
                    val deleted = deleteLastTypedKey()
                    val deletedKeyStr: String?
                    deletedKeyStr = if (deleted == getAmOrPmKeyCode(AM)) {
                        mAmText
                    } else if (deleted == getAmOrPmKeyCode(PM)) {
                        mPmText
                    } else {
                        String.format(mLocale, "%d", getValFromKeyCode(deleted))
                    }
                    tryAccessibilityAnnounce(mTimePicker, String.format(mDeletedKeyFormat!!, deletedKeyStr))
                    updateDisplay(true)
                }
            }
        } else if (keyCode == KeyEvent.KEYCODE_0 || keyCode == KeyEvent.KEYCODE_1 || keyCode == KeyEvent.KEYCODE_2 || keyCode == KeyEvent.KEYCODE_3 || keyCode == KeyEvent.KEYCODE_4 || keyCode == KeyEvent.KEYCODE_5 || keyCode == KeyEvent.KEYCODE_6 || keyCode == KeyEvent.KEYCODE_7 || keyCode == KeyEvent.KEYCODE_8 || keyCode == KeyEvent.KEYCODE_9 || !mIs24HourMode &&
                (keyCode == getAmOrPmKeyCode(AM) || keyCode == getAmOrPmKeyCode(PM))) {
            if (!mInKbMode) {
                if (mTimePicker == null) {
                    // Something's wrong, because time picker should definitely not be null.
                    Log.e(TAG, "Unable to initiate keyboard mode, TimePicker was null.")
                    return true
                }
                mTypedTimes!!.clear()
                tryStartingKbMode(keyCode)
                return true
            }
            // We're already in keyboard mode.
            if (addKeyIfLegal(keyCode)) {
                updateDisplay(false)
            }
            return true
        }
        return false
    }

    /**
     * Try to start keyboard mode with the specified key, as long as the timepicker is not in the
     * middle of a touch-event.
     * @param keyCode The key to use as the first press. Keyboard mode will not be started if the
     * key is not legal to start with. Or, pass in -1 to get into keyboard mode without a starting
     * key.
     */
    private fun tryStartingKbMode(keyCode: Int) {
        if (mTimePicker!!.trySettingInputEnabled(false) &&
                (keyCode == -1 || addKeyIfLegal(keyCode))) {
            mInKbMode = true
            mOkButton?.isEnabled = false
            updateDisplay(false)
        }
    }

    private fun addKeyIfLegal(keyCode: Int): Boolean {
        // If we're in 24hour mode, we'll need to check if the input is full. If in AM/PM mode,
        // we'll need to see if AM/PM have been typed.
        var textSize = 6
        if (mEnableMinutes && !mEnableSeconds) textSize = 4
        if (!mEnableMinutes && !mEnableSeconds) textSize = 2
        if (mIs24HourMode && mTypedTimes!!.size == textSize ||
                !mIs24HourMode && isTypedTimeFullyLegal) {
            return false
        }
        mTypedTimes!!.add(keyCode)
        if (!isTypedTimeLegalSoFar) {
            deleteLastTypedKey()
            return false
        }
        val `val` = getValFromKeyCode(keyCode)
        tryAccessibilityAnnounce(mTimePicker, String.format(mLocale, "%d", `val`))
        // Automatically fill in 0's if AM or PM was legally entered.
        if (isTypedTimeFullyLegal) {
            if (!mIs24HourMode && mTypedTimes!!.size <= textSize - 1) {
                mTypedTimes!!.add(mTypedTimes!!.size - 1, KeyEvent.KEYCODE_0)
                mTypedTimes!!.add(mTypedTimes!!.size - 1, KeyEvent.KEYCODE_0)
            }
            mOkButton!!.isEnabled = true
        }
        return true
    }

    /**
     * Traverse the tree to see if the keys that have been typed so far are legal as is,
     * or may become legal as more keys are typed (excluding backspace).
     */
    private val isTypedTimeLegalSoFar: Boolean
        private get() {
            var node = mLegalTimesTree
            for (keyCode in mTypedTimes!!) {
                node = node!!.canReach(keyCode)
                if (node == null) {
                    return false
                }
            }
            return true
        }// For AM/PM mode, the time is legal if it contains an AM or PM, as those can only be
    // legally added at specific times based on the tree's algorithm.
// For 24-hour mode, the time is legal if the hours and minutes are each legal. Note:
    // getEnteredTime() will ONLY call isTypedTimeFullyLegal() when NOT in 24hour mode.

    /**
     * Check if the time that has been typed so far is completely legal, as is.
     */
    private val isTypedTimeFullyLegal: Boolean
        private get() = if (mIs24HourMode) {
            // For 24-hour mode, the time is legal if the hours and minutes are each legal. Note:
            // getEnteredTime() will ONLY call isTypedTimeFullyLegal() when NOT in 24hour mode.
            val enteredZeros = arrayOf(false, false, false)
            val values = getEnteredTime(enteredZeros)
            values[0] >= 0 && values[1] >= 0 && values[1] < 60 && values[2] >= 0 && values[2] < 60
        } else {
            // For AM/PM mode, the time is legal if it contains an AM or PM, as those can only be
            // legally added at specific times based on the tree's algorithm.
            mTypedTimes!!.contains(getAmOrPmKeyCode(AM)) ||
                    mTypedTimes!!.contains(getAmOrPmKeyCode(PM))
        }

    private fun deleteLastTypedKey(): Int {
        val deleted = mTypedTimes!!.removeAt(mTypedTimes!!.size - 1)
        if (!isTypedTimeFullyLegal) {
            mOkButton?.isEnabled = false
        }
        return deleted
    }

    /**
     * Get out of keyboard mode. If there is nothing in typedTimes, revert to TimePicker's time.
     * @param updateDisplays If true, update the displays with the relevant time.
     */
    private fun finishKbMode(updateDisplays: Boolean) {
        mInKbMode = false
        if (!mTypedTimes!!.isEmpty()) {
            val enteredZeros = arrayOf(false, false, false)
            val values = getEnteredTime(enteredZeros)
            mTimePicker?.time = TimePoint(values[0], values[1], values[2])
            if (!mIs24HourMode) {
                mTimePicker?.setAmOrPm(values[3])
            }
            mTypedTimes?.clear()
        }
        if (updateDisplays) {
            updateDisplay(false)
            mTimePicker?.trySettingInputEnabled(true)
        }
    }

    /**
     * Update the hours, minutes, seconds and AM/PM displays with the typed times. If the typedTimes
     * is empty, either show an empty display (filled with the placeholder text), or update from the
     * timepicker's values.
     * @param allowEmptyDisplay if true, then if the typedTimes is empty, use the placeholder text.
     * Otherwise, revert to the timepicker's values.
     */
    private fun updateDisplay(allowEmptyDisplay: Boolean) {
        if (!allowEmptyDisplay && mTypedTimes!!.isEmpty()) {
            val hour = mTimePicker!!.hours
            val minute = mTimePicker!!.minutes
            val second = mTimePicker!!.seconds
            setHour(hour, true)
            setMinute(minute)
            setSecond(second)
            if (!mIs24HourMode) {
                updateAmPmDisplay(if (hour < 12) AM else PM)
            }
            setCurrentItemShowing(mTimePicker!!.currentItemShowing, true, true, true)
            mOkButton!!.isEnabled = true
        } else {
            val enteredZeros = arrayOf(false, false, false)
            val values = getEnteredTime(enteredZeros)
            val hourFormat = if (enteredZeros[0]) "%02d" else "%2d"
            val minuteFormat = if (enteredZeros[1]) "%02d" else "%2d"
            val secondFormat = if (enteredZeros[1]) "%02d" else "%2d"
            val hourStr = if (values[0] == -1) mDoublePlaceholderText else String.format(hourFormat, values[0]).replace(' ', mPlaceholderText)
            val minuteStr = if (values[1] == -1) mDoublePlaceholderText else String.format(minuteFormat, values[1]).replace(' ', mPlaceholderText)
            val secondStr = if (values[2] == -1) mDoublePlaceholderText else String.format(secondFormat, values[1]).replace(' ', mPlaceholderText)
            mHourView!!.text = hourStr
            mHourSpaceView!!.text = hourStr
            mHourView!!.setTextColor(mUnselectedColor)
            mMinuteView!!.text = minuteStr
            mMinuteSpaceView!!.text = minuteStr
            mMinuteView!!.setTextColor(mUnselectedColor)
            mSecondView!!.text = secondStr
            mSecondSpaceView!!.text = secondStr
            mSecondView!!.setTextColor(mUnselectedColor)
            if (!mIs24HourMode) {
                updateAmPmDisplay(values[3])
            }
        }
    }

    /**
     * Get the currently-entered time, as integer values of the hours, minutes and seconds typed.
     * @param enteredZeros A size-2 boolean array, which the caller should initialize, and which
     * may then be used for the caller to know whether zeros had been explicitly entered as either
     * hours of minutes. This is helpful for deciding whether to show the dashes, or actual 0's.
     * @return A size-3 int array. The first value will be the hours, the second value will be the
     * minutes, and the third will be either TimePickerDialog.AM or TimePickerDialog.PM.
     */
    private fun getEnteredTime(enteredZeros: Array<Boolean>): IntArray {
        var amOrPm = -1
        var startIndex = 1
        if (!mIs24HourMode && isTypedTimeFullyLegal) {
            val keyCode = mTypedTimes!![mTypedTimes!!.size - 1]
            if (keyCode == getAmOrPmKeyCode(AM)) {
                amOrPm = AM
            } else if (keyCode == getAmOrPmKeyCode(PM)) {
                amOrPm = PM
            }
            startIndex = 2
        }
        var minute = -1
        var hour = -1
        var second = 0
        val shift = if (mEnableSeconds) 2 else 0
        for (i in startIndex..mTypedTimes!!.size) {
            val `val` = getValFromKeyCode(mTypedTimes!![mTypedTimes!!.size - i])
            if (mEnableSeconds) {
                if (i == startIndex) {
                    second = `val`
                } else if (i == startIndex + 1) {
                    second += 10 * `val`
                    if (`val` == 0) enteredZeros[2] = true
                }
            }
            if (mEnableMinutes) {
                if (i == startIndex + shift) {
                    minute = `val`
                } else if (i == startIndex + shift + 1) {
                    minute += 10 * `val`
                    if (`val` == 0) enteredZeros[1] = true
                } else if (i == startIndex + shift + 2) {
                    hour = `val`
                } else if (i == startIndex + shift + 3) {
                    hour += 10 * `val`
                    if (`val` == 0) enteredZeros[0] = true
                }
            } else {
                if (i == startIndex + shift) {
                    hour = `val`
                } else if (i == startIndex + shift + 1) {
                    hour += 10 * `val`
                    if (`val` == 0) enteredZeros[0] = true
                }
            }
        }
        return intArrayOf(hour, minute, second, amOrPm)
    }

    /**
     * Get the keycode value for AM and PM in the current language.
     */
    private fun getAmOrPmKeyCode(amOrPm: Int): Int {
        // Cache the codes.
        if (mAmKeyCode == -1 || mPmKeyCode == -1) {
            // Find the first character in the AM/PM text that is unique.
            val kcm = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD)
            var amChar: Char
            var pmChar: Char
            for (i in 0 until Math.max(mAmText!!.length, mPmText!!.length)) {
                amChar = mAmText!!.toLowerCase(mLocale)[i]
                pmChar = mPmText!!.toLowerCase(mLocale)[i]
                if (amChar != pmChar) {
                    val events = kcm.getEvents(charArrayOf(amChar, pmChar))
                    // There should be 4 events: a down and up for both AM and PM.
                    if (events != null && events.size == 4) {
                        mAmKeyCode = events[0].keyCode
                        mPmKeyCode = events[2].keyCode
                    } else {
                        Log.e(TAG, "Unable to find keycodes for AM and PM.")
                    }
                    break
                }
            }
        }
        if (amOrPm == AM) {
            return mAmKeyCode
        } else if (amOrPm == PM) {
            return mPmKeyCode
        }
        return -1
    }

    /**
     * Create a tree for deciding what keys can legally be typed.
     */
    private fun generateLegalTimesTree() {
        // Create a quick cache of numbers to their keycodes.
        val k0 = KeyEvent.KEYCODE_0
        val k1 = KeyEvent.KEYCODE_1
        val k2 = KeyEvent.KEYCODE_2
        val k3 = KeyEvent.KEYCODE_3
        val k4 = KeyEvent.KEYCODE_4
        val k5 = KeyEvent.KEYCODE_5
        val k6 = KeyEvent.KEYCODE_6
        val k7 = KeyEvent.KEYCODE_7
        val k8 = KeyEvent.KEYCODE_8
        val k9 = KeyEvent.KEYCODE_9

        // The root of the tree doesn't contain any numbers.
        mLegalTimesTree = Node()

        // In case we're only allowing hours
        if (!mEnableMinutes && mIs24HourMode) {
            // The first digit may be 0-1
            var firstDigit = Node(k0, k1)
            mLegalTimesTree!!.addChild(firstDigit)

            // When the first digit is 0-1, the second digit may be 0-9
            var secondDigit = Node(k0, k1, k2, k3, k4, k5, k6, k7, k8, k9)
            firstDigit.addChild(secondDigit)

            // The first digit may be 2
            firstDigit = Node(k2)
            mLegalTimesTree!!.addChild(firstDigit)

            // When the first digit is 2, the second digit may be 0-3
            secondDigit = Node(k0, k1, k2, k3)
            firstDigit.addChild(secondDigit)
            return
        }
        if (!mEnableMinutes && !mIs24HourMode) {
            // We'll need to use the AM/PM node a lot.
            // Set up AM and PM to respond to "a" and "p".
            val ampm = Node(getAmOrPmKeyCode(AM), getAmOrPmKeyCode(PM))

            // The first digit may be 1
            var firstDigit = Node(k1)
            mLegalTimesTree!!.addChild(firstDigit)

            // If the first digit is 1, the second one may be am/pm 1pm
            firstDigit.addChild(ampm)
            // If the first digit is 1, the second digit may be 0-2
            val secondDigit = Node(k0, k1, k2)
            firstDigit.addChild(secondDigit)
            secondDigit.addChild(ampm)

            // The first digit may be 2-9
            firstDigit = Node(k2, k3, k4, k5, k6, k7, k8, k9)
            mLegalTimesTree!!.addChild(firstDigit)
            firstDigit.addChild(ampm)
            return
        }

        // In case minutes are allowed
        if (mIs24HourMode) {
            // We'll be re-using these nodes, so we'll save them.
            val minuteFirstDigit = Node(k0, k1, k2, k3, k4, k5)
            val minuteSecondDigit = Node(k0, k1, k2, k3, k4, k5, k6, k7, k8, k9)
            // The first digit must be followed by the second digit.
            minuteFirstDigit.addChild(minuteSecondDigit)
            if (mEnableSeconds) {
                val secondsFirstDigit = Node(k0, k1, k2, k3, k4, k5)
                val secondsSecondDigit = Node(k0, k1, k2, k3, k4, k5, k6, k7, k8, k9)
                secondsFirstDigit.addChild(secondsSecondDigit)

                // Minutes can be followed by seconds.
                minuteSecondDigit.addChild(secondsFirstDigit)
            }

            // The first digit may be 0-1.
            var firstDigit = Node(k0, k1)
            mLegalTimesTree!!.addChild(firstDigit)

            // When the first digit is 0-1, the second digit may be 0-5.
            var secondDigit = Node(k0, k1, k2, k3, k4, k5)
            firstDigit.addChild(secondDigit)
            // We may now be followed by the first minute digit. E.g. 00:09, 15:58.
            secondDigit.addChild(minuteFirstDigit)

            // When the first digit is 0-1, and the second digit is 0-5, the third digit may be 6-9.
            val thirdDigit = Node(k6, k7, k8, k9)
            // The time must now be finished. E.g. 0:55, 1:08.
            secondDigit.addChild(thirdDigit)

            // When the first digit is 0-1, the second digit may be 6-9.
            secondDigit = Node(k6, k7, k8, k9)
            firstDigit.addChild(secondDigit)
            // We must now be followed by the first minute digit. E.g. 06:50, 18:20.
            secondDigit.addChild(minuteFirstDigit)

            // The first digit may be 2.
            firstDigit = Node(k2)
            mLegalTimesTree!!.addChild(firstDigit)

            // When the first digit is 2, the second digit may be 0-3.
            secondDigit = Node(k0, k1, k2, k3)
            firstDigit.addChild(secondDigit)
            // We must now be followed by the first minute digit. E.g. 20:50, 23:09.
            secondDigit.addChild(minuteFirstDigit)

            // When the first digit is 2, the second digit may be 4-5.
            secondDigit = Node(k4, k5)
            firstDigit.addChild(secondDigit)
            // We must now be followd by the last minute digit. E.g. 2:40, 2:53.
            secondDigit.addChild(minuteSecondDigit)

            // The first digit may be 3-9.
            firstDigit = Node(k3, k4, k5, k6, k7, k8, k9)
            mLegalTimesTree!!.addChild(firstDigit)
            // We must now be followed by the first minute digit. E.g. 3:57, 8:12.
            firstDigit.addChild(minuteFirstDigit)
        } else {
            // We'll need to use the AM/PM node a lot.
            // Set up AM and PM to respond to "a" and "p".
            val ampm = Node(getAmOrPmKeyCode(AM), getAmOrPmKeyCode(PM))

            // Seconds will be used a few times as well, if enabled.
            val secondsFirstDigit = Node(k0, k1, k2, k3, k4, k5)
            val secondsSecondDigit = Node(k0, k1, k2, k3, k4, k5, k6, k7, k8, k9)
            secondsSecondDigit.addChild(ampm)
            secondsFirstDigit.addChild(secondsSecondDigit)

            // The first hour digit may be 1.
            var firstDigit = Node(k1)
            mLegalTimesTree!!.addChild(firstDigit)
            // We'll allow quick input of on-the-hour times. E.g. 1pm.
            firstDigit.addChild(ampm)

            // When the first digit is 1, the second digit may be 0-2.
            var secondDigit = Node(k0, k1, k2)
            firstDigit.addChild(secondDigit)
            // Also for quick input of on-the-hour times. E.g. 10pm, 12am.
            secondDigit.addChild(ampm)

            // When the first digit is 1, and the second digit is 0-2, the third digit may be 0-5.
            var thirdDigit = Node(k0, k1, k2, k3, k4, k5)
            secondDigit.addChild(thirdDigit)
            // The time may be finished now. E.g. 1:02pm, 1:25am.
            thirdDigit.addChild(ampm)

            // When the first digit is 1, the second digit is 0-2, and the third digit is 0-5,
            // the fourth digit may be 0-9.
            val fourthDigit = Node(k0, k1, k2, k3, k4, k5, k6, k7, k8, k9)
            thirdDigit.addChild(fourthDigit)
            // The time must be finished now, when seconds are disabled. E.g. 10:49am, 12:40pm.
            fourthDigit.addChild(ampm)

            // When the first digit is 1, the second digit is 0-2, and the third digit is 0-5,
            // and fourth digit is 0-9, we may add seconds if enabled.
            if (mEnableSeconds) {
                // The time must be finished now. E.g. 10:49:01am, 12:40:59pm.
                fourthDigit.addChild(secondsFirstDigit)
            }

            // When the first digit is 1, and the second digit is 0-2, the third digit may be 6-9.
            thirdDigit = Node(k6, k7, k8, k9)
            secondDigit.addChild(thirdDigit)
            // The time must be finished now. E.g. 1:08am, 1:26pm.
            thirdDigit.addChild(ampm)

            // When the first digit is 1, and the second digit is 0-2, and the third digit is 6-9,
            // we may add seconds is enabled.
            if (mEnableSeconds) {
                // The time must be finished now. E.g. 1:08:01am, 1:26:59pm.
                thirdDigit.addChild(secondsFirstDigit)
            }

            // When the first digit is 1, the second digit may be 3-5.
            secondDigit = Node(k3, k4, k5)
            firstDigit.addChild(secondDigit)

            // When the first digit is 1, and the second digit is 3-5, the third digit may be 0-9.
            thirdDigit = Node(k0, k1, k2, k3, k4, k5, k6, k7, k8, k9)
            secondDigit.addChild(thirdDigit)
            // The time must be finished now if seconds are disabled. E.g. 1:39am, 1:50pm.
            thirdDigit.addChild(ampm)

            // When the first digit is 1, and the second digit is 3-5, and the third digit is 0-9,
            // we may add seconds if enabled.
            if (mEnableSeconds) {
                // The time must be finished now. E.g. 1:39:01am, 1:50:59pm.
                thirdDigit.addChild(secondsFirstDigit)
            }

            // The hour digit may be 2-9.
            firstDigit = Node(k2, k3, k4, k5, k6, k7, k8, k9)
            mLegalTimesTree!!.addChild(firstDigit)
            // We'll allow quick input of on-the-hour-times. E.g. 2am, 5pm.
            firstDigit.addChild(ampm)

            // When the first digit is 2-9, the second digit may be 0-5.
            secondDigit = Node(k0, k1, k2, k3, k4, k5)
            firstDigit.addChild(secondDigit)

            // When the first digit is 2-9, and the second digit is 0-5, the third digit may be 0-9.
            thirdDigit = Node(k0, k1, k2, k3, k4, k5, k6, k7, k8, k9)
            secondDigit.addChild(thirdDigit)
            // The time must be finished now. E.g. 2:57am, 9:30pm.
            thirdDigit.addChild(ampm)

            // When the first digit is 2-9, and the second digit is 0-5, and third digit is 0-9, we
            // may add seconds if enabled.
            if (mEnableSeconds) {
                // The time must be finished now. E.g. 2:57:01am, 9:30:59pm.
                thirdDigit.addChild(secondsFirstDigit)
            }
        }
    }

    /**
     * Simple node class to be used for traversal to check for legal times.
     * mLegalKeys represents the keys that can be typed to get to the node.
     * mChildren are the children that can be reached from this node.
     */
    private class Node(private vararg val mLegalKeys: Int) {
        private val mChildren: ArrayList<Node>?
        fun addChild(child: Node) {
            mChildren!!.add(child)
        }

        fun containsKey(key: Int): Boolean {
            for (legalKey in mLegalKeys) {
                if (legalKey == key) return true
            }
            return false
        }

        fun canReach(key: Int): Node? {
            if (mChildren == null) {
                return null
            }
            for (child in mChildren) {
                if (child.containsKey(key)) {
                    return child
                }
            }
            return null
        }

        init {
            mChildren = ArrayList()
        }
    }

    private inner class KeyboardListener : View.OnKeyListener {
        override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
            return if (event.action == KeyEvent.ACTION_UP) {
                processKeyUp(keyCode)
            } else false
        }
    }

    fun notifyOnDateListener() {
        onTimeSetListener?.let { it(TimePoint(mTimePicker!!.hours, mTimePicker!!.minutes, mTimePicker!!.seconds)) }
    }

    val selectedTime: TimePoint?
        get() = mTimePicker!!.time

    companion object {
        private const val TAG = "TimePickerDialog"
        private const val KEY_INITIAL_TIME = "initial_time"
        private const val KEY_IS_24_HOUR_VIEW = "is_24_hour_view"
        private const val KEY_TITLE = "dialog_title"
        private const val KEY_CURRENT_ITEM_SHOWING = "current_item_showing"
        private const val KEY_IN_KB_MODE = "in_kb_mode"
        private const val KEY_TYPED_TIMES = "typed_times"
        private const val KEY_THEME_DARK = "theme_dark"
        private const val KEY_THEME_DARK_CHANGED = "theme_dark_changed"
        private const val KEY_ACCENT = "accent"
        private const val KEY_VIBRATE = "vibrate"
        private const val KEY_DISMISS = "dismiss"
        private const val KEY_ENABLE_SECONDS = "enable_seconds"
        private const val KEY_ENABLE_MINUTES = "enable_minutes"
        private const val KEY_OK_RESID = "ok_resid"
        private const val KEY_OK_STRING = "ok_string"
        private const val KEY_OK_COLOR = "ok_color"
        private const val KEY_CANCEL_RESID = "cancel_resid"
        private const val KEY_CANCEL_STRING = "cancel_string"
        private const val KEY_CANCEL_COLOR = "cancel_color"
        private const val KEY_VERSION = "version"
        private const val KEY_TIMEPOINTLIMITER = "timepoint_limiter"
        private const val KEY_LOCALE = "locale"
        const val HOUR_INDEX = 0
        const val MINUTE_INDEX = 1
        const val SECOND_INDEX = 2
        const val AM = 0
        const val PM = 1

        // Delay before starting the pulse animation, in ms.
        private const val PULSE_ANIMATOR_DELAY = 300

        /**
         * Create a new TimePickerDialog instance with a given intial selection
         * @param callback     How the parent is notified that the time is set.
         * @param hourOfDay    The initial hour of the dialog.
         * @param minute       The initial minute of the dialog.
         * @param second       The initial second of the dialog.
         * @param is24HourMode True to render 24 hour mode, false to render AM / PM selectors.
         * @return a new TimePickerDialog instance.
         */
        fun newInstance(callback: OnTimeSetListener,
                        timeDate: TimePoint, is24HourMode: Boolean): TimePickerDialog {
            val ret = TimePickerDialog()
            ret.initialize(callback, timeDate, is24HourMode)
            return ret
        }

        /**
         * Create a new TimePickerDialog instance with a given initial selection
         * @param callback     How the parent is notified that the time is set.
         * @param hourOfDay    The initial hour of the dialog.
         * @param minute       The initial minute of the dialog.
         * @param is24HourMode True to render 24 hour mode, false to render AM / PM selectors.
         * @return a new TimePickerDialog instance.
         */
        fun newInstance(callback: OnTimeSetListener,
                        hourOfDay: Int, minute: Int, is24HourMode: Boolean): TimePickerDialog {
            return newInstance(callback, TimePoint(hourOfDay, minute, 0), is24HourMode)
        }

        /**
         * Create a new TimePickerDialog instance initialized to the current system time
         * @param callback     How the parent is notified that the time is set.
         * @param is24HourMode True to render 24 hour mode, false to render AM / PM selectors.
         * @return a new TimePickerDialog instance.
         */
        fun newInstance(callback: OnTimeSetListener, is24HourMode: Boolean): TimePickerDialog {
            val now = Calendar.getInstance()
            return newInstance(callback, now[Calendar.HOUR_OF_DAY], now[Calendar.MINUTE], is24HourMode)
        }

        private fun getValFromKeyCode(keyCode: Int): Int {
            return when (keyCode) {
                KeyEvent.KEYCODE_0 -> 0
                KeyEvent.KEYCODE_1 -> 1
                KeyEvent.KEYCODE_2 -> 2
                KeyEvent.KEYCODE_3 -> 3
                KeyEvent.KEYCODE_4 -> 4
                KeyEvent.KEYCODE_5 -> 5
                KeyEvent.KEYCODE_6 -> 6
                KeyEvent.KEYCODE_7 -> 7
                KeyEvent.KEYCODE_8 -> 8
                KeyEvent.KEYCODE_9 -> 9
                else -> -1
            }
        }

        fun newInstance(callback: TimePickerCallback, timeDate: TimePoint, is24HourMode: Boolean): TimePickerDialog {
            return newInstance(object : OnTimeSetListener {
                override fun invoke(timePoint: TimePoint) {
                    callback(timePoint, timePoint.toString())
                }
            }, timeDate, is24HourMode)
        }
    }
}