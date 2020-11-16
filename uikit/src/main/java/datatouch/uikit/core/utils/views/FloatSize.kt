package datatouch.uikit.core.utils.views

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private const val DEG2RAD = Math.PI.toFloat() / 180.0f

class FloatSize(var w: Float, var h: Float = 0f) {

    constructor(w: Int, h : Int) : this(w.toFloat(), h.toFloat())

    fun set(src: FloatSize?) {
        if (src == null) {
            w = 0f
            h = 0f
        } else {
            set(src.w, src.h)
        }
    }

    operator fun set(w: Float, h: Float) {
        this.w = w
        this.h = h
    }

    val isPositive get() = w > 0 && h > 0

    operator fun set(w: Float, h: Float, rotationAngleDeg: Float) {
        val rad = rotationAngleDeg * DEG2RAD
        this.w = abs(w * cos(rad.toDouble()).toFloat()) + abs(
            h * sin(rad.toDouble()).toFloat())
        this.h = abs(w * sin(rad.toDouble()).toFloat()) + abs(h * cos(rad.toDouble()).toFloat())
    }
}
