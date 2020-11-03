package datatouch.uikit.components.timepicker

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.IntRange
import java.util.*

/**
 * Simple utility class that represents a time in the day up to second precision
 * The time input is expected to use 24 hour mode.
 * Fields are modulo'd into their correct ranges.
 * It does not handle timezones.
 *
 * Created by wdullaer on 13/10/15.
 */
class TimePoint : Parcelable, Comparable<TimePoint> {
    @get:IntRange(from = 0, to = 23)
    var hour: Int
        private set

    @get:IntRange(from = 0, to = 59)
    var minute: Int
        private set

    @get:IntRange(from = 0, to = 59)
    var second: Int
        private set

    enum class TYPE {
        HOUR, MINUTE, SECOND
    }

    constructor(time: TimePoint) : this(time.hour, time.minute, time.second) {}
    constructor() {
        val calendar = Calendar.getInstance()
        hour = calendar[Calendar.HOUR_OF_DAY]
        minute = calendar[Calendar.MINUTE]
        second = calendar[Calendar.SECOND]
    }

    @JvmOverloads
    constructor(@IntRange(from = 0, to = 23) hour: Int,
                @IntRange(from = 0, to = 59) minute: Int = 0,
                @IntRange(from = 0, to = 59) second: Int = 0) {
        this.hour = hour % 24
        this.minute = minute % 60
        this.second = second % 60
    }

    constructor(`in`: Parcel) {
        hour = `in`.readInt()
        minute = `in`.readInt()
        second = `in`.readInt()
    }

    val isAM: Boolean
        get() = hour < 12

    val isPM: Boolean
        get() = !isAM

    fun setAM() {
        if (hour >= 12) hour %= 12
    }

    fun setPM() {
        if (hour < 12) hour = (hour + 12) % 24
    }

    fun add(type: TYPE, value: Int) {
        var value = value
        if (type == TYPE.MINUTE) value *= 60
        if (type == TYPE.HOUR) value *= 3600
        value += toSeconds()
        when (type) {
            TYPE.SECOND -> {
                val secondVal = value % 3600 % 60
                if (secondVal < 0) {
                    second = 60 + secondVal
                    add(TYPE.MINUTE, -1)
                } else {
                    second = secondVal
                }
                val minuteVal = value % 3600 / 60
                if (minuteVal < 0) {
                    minute = 60 + minuteVal
                    add(TYPE.HOUR, -1)
                } else {
                    minute = minuteVal
                }
                val hourVal = value / 3600 % 24
                hour = if (hourVal < 0) {
                    24 + hourVal
                } else {
                    hourVal
                }
            }
            TYPE.MINUTE -> {
                val minuteVal = value % 3600 / 60
                if (minuteVal < 0) {
                    minute = 60 + minuteVal
                    add(TYPE.HOUR, -1)
                } else {
                    minute = minuteVal
                }
                val hourVal = value / 3600 % 24
                hour = if (hourVal < 0) {
                    24 + hourVal
                } else {
                    hourVal
                }
            }
            TYPE.HOUR -> {
                val hourVal = value / 3600 % 24
                hour = if (hourVal < 0) {
                    24 + hourVal
                } else {
                    hourVal
                }
            }
        }
    }

    operator fun get(type: TYPE): Int {
        return when (type) {
            TYPE.SECOND -> second
            TYPE.MINUTE -> minute
            TYPE.HOUR -> hour
            else -> hour
        }
    }

    fun toDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, second)
        return calendar.time
    }

    fun toSeconds(): Int {
        return 3600 * hour + 60 * minute + second
    }

    override fun hashCode(): Int {
        return toSeconds()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val timepoint = o as TimePoint
        return hashCode() == timepoint.hashCode()
    }

    fun equals(time: TimePoint?, resolution: TYPE): Boolean {
        if (time == null) return false
        var output = true
        when (resolution) {
            TYPE.SECOND -> {
                output = output && time.second == second
                output = output && time.minute == minute
                output = output && time.hour == hour
            }
            TYPE.MINUTE -> {
                output = output && time.minute == minute
                output = output && time.hour == hour
            }
            TYPE.HOUR -> output = output && time.hour == hour
        }
        return output
    }

    override fun compareTo(t: TimePoint): Int {
        return hashCode() - t.hashCode()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        out.writeInt(hour)
        out.writeInt(minute)
        out.writeInt(second)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "" + hour + "h " + minute + "m " + second + "s"
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TimePoint> = object : Parcelable.Creator<TimePoint> {
            override fun createFromParcel(`in`: Parcel): TimePoint? {
                return TimePoint(`in`)
            }

            override fun newArray(size: Int): Array<TimePoint?> {
                return arrayOfNulls(size)
            }
        }
    }
}