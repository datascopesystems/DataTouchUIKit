package datatouch.uikit.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

inline fun Boolean.yes(block: () -> Unit) = also { if (it) block() }
inline fun Boolean.no(block: () -> Unit) = also { if (!it) block() }

inline fun Boolean?.ifYes(block: () -> Unit) {
    if (this == true) block()
}

inline fun Boolean.ifNo(block: () -> Unit) {
    if (!this) block()
}


infix fun <T> Boolean.then(param: T): T? = if (this) param else null

fun Context?.scanForActivity(): Activity? {
    if (this == null) return null else if (this is Activity) return this else if (this is ContextWrapper) return this.baseContext.scanForActivity()
    return null
}

fun String?.toIntOrZero(): Int {
    return try {
        this?.toInt() ?: 0
    } catch (ex: Exception) {
        0
    }
}

fun <T> T?.default(defVal: T): T {
    return this ?: defVal
}


fun Any?.isValueNotEmpty(): Boolean {
    if (null == this) return false
    if (this is Int) return this > 0
    if (this is Boolean) return this
    if (this is String) {
        return try {
            (this.toInt()) > 0
        } catch (e: Exception) {
            this.isNotEmpty()
        }
    }
    return true
}

fun Int.rotateByDeg(angleDeg: Int): Int {
    var rotation = this + angleDeg
    if (rotation < 0) {
        rotation += 360
    }
    rotation %= 360
    return rotation
}
