package datatouch.uikit.core.duration

import org.threeten.bp.Duration

class DurationTime() {

    private var duration: Duration = Duration.ZERO

    constructor(initialDuration: DurationTime) : this(initialDuration.toMinutes())

    constructor(minutes: Int) : this() {
        duration = Duration.ofMinutes(minutes.toLong())
    }

    fun set(hours: Int, minutes: Int) {
        duration = Duration.ofHours(hours.toLong())
                .plusMinutes(minutes.toLong())
    }

    fun toMinutes(): Int = duration.toMinutes().toInt()

    fun getHours(): Int {
        return duration.toHours().toInt()
    }

    fun getMinutes(): Int {
        val minutesInHoursPart = Duration.ofHours(duration.toHours()).toMinutes().toInt()
        val totalMinutes = toMinutes()
        return totalMinutes - minutesInHoursPart
    }

    private fun isZero() = duration.isZero

    fun isNotZero() = !isZero()

    override fun toString() = "${getHours()}:${getMinutes()}"
}