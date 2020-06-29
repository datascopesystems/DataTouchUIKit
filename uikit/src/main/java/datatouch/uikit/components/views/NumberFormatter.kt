package datatouch.uikit.components.views

import java.util.*

object NumberFormatter {

    fun formatToAbbreviation(number: Int): String {
        var suffix: Char? = null
        var roundedNumber: Float
        if (number < 1000) {
            // 0 - 1000
            roundedNumber = Math.round(number.toFloat()).toFloat()
        } else if (number < 900000) {
            // 0.9k-850k
            roundedNumber = Math.round((number / 1000).toFloat()).toFloat()

            if (0f == roundedNumber)
                roundedNumber = 1f

            suffix = 'K'
        } else if (number < 900000000) {
            // 0.9m-850m
            roundedNumber = Math.round((number / 1000000).toFloat()).toFloat()

            if (0f == roundedNumber)
                roundedNumber = 1f

            suffix = 'M'
        } else {
            // 0.9b-850b
            roundedNumber = Math.round((number / 1000000000).toFloat()).toFloat()

            if (0f == roundedNumber)
                roundedNumber = 1f

            suffix = 'B'
        }

        return formatFloatNumber(roundedNumber.toDouble()) + (suffix ?: "")
    }

    private fun formatFloatNumber(d: Double): String {
        return if (d == d.toLong().toDouble())
            String.format(Locale.getDefault(), "%d", d.toLong())
        else
            String.format(Locale.getDefault(), "%s", d)
    }

    @JvmOverloads
    fun toInt(number: String?, defValue: Int, callback: OnFormatExceptionCallback? = null): Int {
        return try {
            Integer.parseInt(number.orEmpty())
        } catch (ex: Exception) {
            callback?.formatException()

            defValue
        }

    }

    interface OnFormatExceptionCallback {
        fun formatException()
    }
}
