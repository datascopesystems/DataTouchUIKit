package datatouch.uikit.core.extensions

object BooleanExtensions {

    inline fun Boolean.yes(block: () -> Unit) = also { if (it) block() }
    inline fun Boolean.no(block: () -> Unit) = also { if (!it) block() }

    inline fun Boolean?.ifYes(block: () -> Unit) {
        if (this == true) block()
    }

    inline fun Boolean.ifNo(block: () -> Unit) {
        if (!this) block()
    }
}