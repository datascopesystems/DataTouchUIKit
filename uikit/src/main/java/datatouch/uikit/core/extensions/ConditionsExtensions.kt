package datatouch.uikit.core.extensions

object ConditionsExtensions {

    inline fun Any?.isNull() = this == null

    inline fun Any?.isNotNull() = !isNull()

    inline fun Collection<*>?.isNotNullOrEmpty() = this.isNotNull() && this?.isEmpty() == false

}