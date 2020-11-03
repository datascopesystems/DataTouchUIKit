package datatouch.uikit.components.timepicker

/**
 * A collection of methods which need to be shared with all components of the TimePicker
 *
 * Created by wdullaer on 6/10/15.
 */
interface TimePickerController {
    /**
     * @return boolean - true if the dark theme should be used
     */
    val isThemeDark: Boolean

    /**
     * @return boolean - true if 24 hour mode is used / false if AM/PM is used
     */
    fun is24HourMode(): Boolean

    /**
     * @return int - the accent color currently in use
     */
    val accentColor: Int

    /**
     * @return Version - The current version to render
     */
    val version: TimePickerDialog.Version?

    /**
     * Request the device to vibrate
     */
    fun tryVibrate()

    /**
     * @param time Timepoint - the selected point in time
     * @param index int - The current view to consider when calculating the range
     * @return boolean - true if this is not a selectable value
     */
    fun isOutOfRange(time: TimePoint?, index: Int): Boolean

    /**
     * @return boolean - true if AM times are outside the range of valid selections
     */
    val isAmDisabled: Boolean

    /**
     * @return boolean - true if PM times are outside the range of valid selections
     */
    val isPmDisabled: Boolean

    /**
     * Will round the given Timepoint to the nearest valid Timepoint given the following restrictions:
     * - TYPE.HOUR, it will just round to the next valid point, possible adjusting minutes and seconds
     * - TYPE.MINUTE, it will round to the next valid point, without adjusting the hour, but possibly adjusting the seconds
     * - TYPE.SECOND, it will round to the next valid point, only adjusting the seconds
     * @param time Timepoint - the timepoint to validate
     * @param type Timepoint.TYPE - whether we should round the hours, minutes or seconds
     * @return timepoint - the nearest valid timepoint
     */
    fun roundToNearest(time: TimePoint?, type: TimePoint.TYPE?): TimePoint?
}