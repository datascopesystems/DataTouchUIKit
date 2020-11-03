package datatouch.uikit.core.extensions

object GenericExtensions {

    fun <T> T?.default(defVal: T): T {
        return this ?: defVal
    }


}