package datatouch.uikit.core.extensions

object LongExtensions {

    inline fun Long?.isValidId() = this != null && this.isValidId()

    inline fun Long.isValidId() = this > 0

    inline fun Long?.orZero() = this ?: 0L
}