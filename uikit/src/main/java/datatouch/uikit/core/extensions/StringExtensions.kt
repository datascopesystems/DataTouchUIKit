package datatouch.uikit.core.extensions

import datatouch.uikit.core.extensions.GenericExtensions.default


object StringExtensions {

    inline fun String?.parseIntOrZero(): Int =
        kotlin.runCatching { this?.toInt() }.getOrDefault(0).default(0)

    inline fun String?.parseLongOrZero(): Long =
        kotlin.runCatching { this?.toLong() }.getOrDefault(0).default(0)

    inline fun String?.isNotEmptyOrZeroes(): Boolean {
        return if (this == null) false else try {
            val value = this.replace("0", "")
            return value.isNotEmpty()
        } catch (ex: java.lang.Exception) {
            false
        }
    }

    inline fun String?.or(value: String): String = if (this.isNullOrEmpty()) value else this

    inline fun String?.defValue(def: () -> String) = this.orEmpty().ifEmpty { def() }

}