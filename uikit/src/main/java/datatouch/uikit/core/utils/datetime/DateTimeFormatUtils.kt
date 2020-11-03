package datatouch.uikit.core.utils.datetime

import datatouch.uikit.core.extensions.GenericExtensions.default
import java.text.SimpleDateFormat
import java.util.*

const val dateFormat = "dd/MM/yyyy"
const val dateHumanFormat = "dd MMMM yyyy"
const val weekdayAndDateHumanFormat = "E, dd MMMM yyyy"
const val dateWithTimeFormat = "dd/MM/yyyy HH:mm:ss"
const val timeFormat = "HH:mm:ss"
const val timeFormatWithoutSeconds = "HH:mm"

object DateTimeFormatUtils {

    private val formatter get() = createFormatter(dateFormat)

    private fun createFormatter(format: String?, setDefTimezone: Boolean = false) =
        SimpleDateFormat(format.default(dateFormat), Locale.getDefault())
            .also { if (setDefTimezone) it.timeZone = TimeZone.getDefault() }

    fun format(dateToFormat: Date): String = formatter.format(dateToFormat)

}