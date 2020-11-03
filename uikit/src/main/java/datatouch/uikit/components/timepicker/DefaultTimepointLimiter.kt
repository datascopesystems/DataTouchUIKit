package datatouch.uikit.components.timepicker

import android.os.Parcel
import android.os.Parcelable
import java.util.*
import kotlin.math.abs

/**
 * An implementation of TimepointLimiter which implements the most common ways to restrict Timepoints
 * in a TimePickerDialog
 * Created by wdullaer on 20/06/17.
 */
internal class DefaultTimepointLimiter : TimePointLimiter {
    private val mSelectableTimes = TreeSet<TimePoint>()
    private val mDisabledTimes = TreeSet<TimePoint>()
    private var exclusiveSelectableTimes = TreeSet<TimePoint>()
    var minTime: TimePoint? = null
        private set
    var maxTime: TimePoint? = null
        private set

    constructor() {}
    constructor(`in`: Parcel) {
        minTime = `in`.readParcelable(TimePoint::class.java.classLoader)
        maxTime = `in`.readParcelable(TimePoint::class.java.classLoader)
        mSelectableTimes.addAll(Arrays.asList(*`in`.createTypedArray(TimePoint.CREATOR)))
        mDisabledTimes.addAll(Arrays.asList(*`in`.createTypedArray(TimePoint.CREATOR)))
        exclusiveSelectableTimes = getExclusiveSelectableTimes(mSelectableTimes, mDisabledTimes)
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        out.writeParcelable(minTime, flags)
        out.writeParcelable(maxTime, flags)
        out.writeTypedArray(mSelectableTimes.toTypedArray(), flags)
        out.writeTypedArray(mDisabledTimes.toTypedArray(), flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun setMinTime(minTime: TimePoint) {
        require(!(maxTime != null && minTime > maxTime!!)) { "Minimum time must be smaller than the maximum time" }
        this.minTime = minTime
    }

    fun setMaxTime(maxTime: TimePoint) {
        require(!(minTime != null && maxTime < minTime!!)) { "Maximum time must be greater than the minimum time" }
        this.maxTime = maxTime
    }

    fun setSelectableTimes(selectableTimes: Array<TimePoint>) {
        mSelectableTimes.addAll(listOf(*selectableTimes))
        exclusiveSelectableTimes = getExclusiveSelectableTimes(mSelectableTimes, mDisabledTimes)
    }

    fun setDisabledTimes(disabledTimes: Array<TimePoint>) {
        mDisabledTimes.addAll(listOf(*disabledTimes))
        exclusiveSelectableTimes = getExclusiveSelectableTimes(mSelectableTimes, mDisabledTimes)
    }

    fun getSelectableTimes(): Array<TimePoint> {
        return mSelectableTimes.toTypedArray()
    }

    fun getDisabledTimes(): Array<TimePoint> {
        return mDisabledTimes.toTypedArray()
    }

    private fun getExclusiveSelectableTimes(selectable: TreeSet<TimePoint>, disabled: TreeSet<TimePoint>): TreeSet<TimePoint> {
        val output = TreeSet(selectable)
        output.removeAll(disabled)
        return output
    }

    override fun isOutOfRange(current: TimePoint?, index: Int, resolution: TimePoint.TYPE): Boolean {
        if (current == null) return false
        return if (index == TimePickerDialog.HOUR_INDEX) {
            if (minTime != null && minTime!!.hour > current.hour) return true
            if (maxTime != null && maxTime!!.hour + 1 <= current.hour) return true
            if (!exclusiveSelectableTimes.isEmpty()) {
                val ceil = exclusiveSelectableTimes.ceiling(current)
                val floor = exclusiveSelectableTimes.floor(current)
                return !(current.equals(ceil, TimePoint.TYPE.HOUR) || current.equals(floor, TimePoint.TYPE.HOUR))
            }
            if (!mDisabledTimes.isEmpty() && resolution === TimePoint.TYPE.HOUR) {
                val ceil = mDisabledTimes.ceiling(current)
                val floor = mDisabledTimes.floor(current)
                return current.equals(ceil, TimePoint.TYPE.HOUR) || current.equals(floor, TimePoint.TYPE.HOUR)
            }
            false
        } else if (index == TimePickerDialog.MINUTE_INDEX) {
            if (minTime != null) {
                val roundedMin = TimePoint(minTime!!.hour, minTime!!.minute)
                if (roundedMin > current) return true
            }
            if (maxTime != null) {
                val roundedMax = TimePoint(maxTime!!.hour, maxTime!!.minute, 59)
                if (roundedMax < current) return true
            }
            if (!exclusiveSelectableTimes.isEmpty()) {
                val ceil = exclusiveSelectableTimes.ceiling(current)
                val floor = exclusiveSelectableTimes.floor(current)
                return !(current.equals(ceil, TimePoint.TYPE.MINUTE) || current.equals(floor, TimePoint.TYPE.MINUTE))
            }
            if (!mDisabledTimes.isEmpty() && resolution === TimePoint.TYPE.MINUTE) {
                val ceil = mDisabledTimes.ceiling(current)
                val floor = mDisabledTimes.floor(current)
                val ceilExclude = current.equals(ceil, TimePoint.TYPE.MINUTE)
                val floorExclude = current.equals(floor, TimePoint.TYPE.MINUTE)
                return ceilExclude || floorExclude
            }
            false
        } else isOutOfRange(current)
    }

    fun isOutOfRange(current: TimePoint): Boolean {
        if (minTime != null && minTime!! > current) return true
        if (maxTime != null && maxTime!! < current) return true
        return if (!exclusiveSelectableTimes.isEmpty()) !exclusiveSelectableTimes.contains(current) else mDisabledTimes.contains(current)
    }

    override val isAmDisabled: Boolean
        get() {
            val midday = TimePoint(12)
            if (minTime != null && minTime!! >= midday) return true
            return if (!exclusiveSelectableTimes.isEmpty()) exclusiveSelectableTimes.first().compareTo(midday) >= 0 else false
        }

    override val isPmDisabled: Boolean
        get() {
            val midday = TimePoint(12)
            if (maxTime != null && maxTime!! < midday) return true
            return if (!exclusiveSelectableTimes.isEmpty()) exclusiveSelectableTimes.last().compareTo(midday) < 0 else false
        }

    override fun roundToNearest(time: TimePoint, type: TimePoint.TYPE?, resolution: TimePoint.TYPE): TimePoint {
        if (minTime != null && minTime!! > time) return minTime!!
        if (maxTime != null && maxTime!! < time) return maxTime!!

        // type == SECOND: cannot change anything, return input
        if (type === TimePoint.TYPE.SECOND) return time
        if (!exclusiveSelectableTimes.isEmpty()) {
            val floor = exclusiveSelectableTimes.floor(time)
            val ceil = exclusiveSelectableTimes.ceiling(time)
            if (floor == null || ceil == null) {
                val t = floor ?: ceil
                if (type == null) return t
                if (t.hour != time.hour) return time
                return if (type === TimePoint.TYPE.MINUTE && t.minute != time.minute) time else t
            }
            if (type === TimePoint.TYPE.HOUR) {
                if (floor.hour != time.hour && ceil.hour == time.hour) return ceil
                if (floor.hour == time.hour && ceil.hour != time.hour) return floor
                if (floor.hour != time.hour && ceil.hour != time.hour) return time
            }
            if (type === TimePoint.TYPE.MINUTE) {
                if (floor.hour != time.hour && ceil.hour != time.hour) return time
                if (floor.hour != time.hour && ceil.hour == time.hour) {
                    return if (ceil.minute == time.minute) ceil else time
                }
                if (floor.hour == time.hour && ceil.hour != time.hour) {
                    return if (floor.minute == time.minute) floor else time
                }
                if (floor.minute != time.minute && ceil.minute == time.minute) return ceil
                if (floor.minute == time.minute && ceil.minute != time.minute) return floor
                if (floor.minute != time.minute && ceil.minute != time.minute) return time
            }
            val floorDist = abs(time.compareTo(floor))
            val ceilDist = abs(time.compareTo(ceil))
            return if (floorDist < ceilDist) floor else ceil
        }
        if (!mDisabledTimes.isEmpty()) {
            // if type matches resolution: cannot change anything, return input
            if (type != null && type === resolution) return time
            if (resolution === TimePoint.TYPE.SECOND) {
                return if (!mDisabledTimes.contains(time)) time else searchValidTimePoint(time, type, resolution)
            }
            if (resolution === TimePoint.TYPE.MINUTE) {
                val ceil = mDisabledTimes.ceiling(time)
                val floor = mDisabledTimes.floor(time)
                val ceilDisabled = time.equals(ceil, TimePoint.TYPE.MINUTE)
                val floorDisabled = time.equals(floor, TimePoint.TYPE.MINUTE)
                return if (ceilDisabled || floorDisabled) searchValidTimePoint(time, type, resolution) else time
            }
            if (resolution === TimePoint.TYPE.HOUR) {
                val ceil = mDisabledTimes.ceiling(time)
                val floor = mDisabledTimes.floor(time)
                val ceilDisabled = time.equals(ceil, TimePoint.TYPE.HOUR)
                val floorDisabled = time.equals(floor, TimePoint.TYPE.HOUR)
                return if (ceilDisabled || floorDisabled) searchValidTimePoint(time, type, resolution) else time
            }
        }
        return time
    }

    private fun searchValidTimePoint(time: TimePoint, type: TimePoint.TYPE?, resolution: TimePoint.TYPE): TimePoint {
        val forward = TimePoint(time)
        val backward = TimePoint(time)
        var iteration = 0
        var resolutionMultiplier = 1
        if (resolution === TimePoint.TYPE.MINUTE) resolutionMultiplier = 60
        if (resolution === TimePoint.TYPE.SECOND) resolutionMultiplier = 3600
        while (iteration < 24 * resolutionMultiplier) {
            iteration++
            forward.add(resolution, 1)
            backward.add(resolution, -1)
            if (type == null || forward[type] == time[type]) {
                val forwardCeil = mDisabledTimes.ceiling(forward)
                val forwardFloor = mDisabledTimes.floor(forward)
                if (!forward.equals(forwardCeil, resolution) && !forward.equals(forwardFloor, resolution)) return forward
            }
            if (type == null || backward[type] == time[type]) {
                val backwardCeil = mDisabledTimes.ceiling(backward)
                val backwardFloor = mDisabledTimes.floor(backward)
                if (!backward.equals(backwardCeil, resolution) && !backward.equals(backwardFloor, resolution)) return backward
            }
            if (type != null && backward[type] != time[type] && forward[type] != time[type]) break
        }
        // If this step is reached, the user has disabled all timepoints
        return time
    }

    companion object {
        val CREATOR: Parcelable.Creator<DefaultTimepointLimiter?> = object : Parcelable.Creator<DefaultTimepointLimiter?> {
            override fun createFromParcel(`in`: Parcel): DefaultTimepointLimiter? {
                return DefaultTimepointLimiter(`in`)
            }

            override fun newArray(size: Int): Array<DefaultTimepointLimiter?> {
                return arrayOfNulls(size)
            }
        }
    }
}