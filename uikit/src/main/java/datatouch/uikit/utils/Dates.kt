package datatouch.uikit.utils

import android.text.TextUtils
import android.text.format.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

const val dateFormat = "dd/MM/yyyy"

object Dates {

    @JvmStatic
    fun now() = Date()

    @JvmStatic
    fun format(dateToFormat: Date?): String {
        return formatter.format(dateToFormat)
    }

    @JvmStatic
    val formatter: SimpleDateFormat
        get() = createFormatter(dateFormat)


    val currentYear: String
        get() = createFormatter("yyyy").format(calendarTime)

    @JvmStatic
    fun parseOrToday(dateStr: String?): Date {
        if (TextUtils.isEmpty(dateStr)) {
            return calendarTime
        }
        var parsedTime: Date? = null
        try {
            parsedTime = formatter.parse(dateStr)
        } catch (e: Exception) {
        } finally {
            parsedTime = parsedTime ?: calendarTime
        }
        return parsedTime
    }

    @JvmStatic
    fun dateDiffInDays(from: String?, to: String?): Int {
        val formatter = formatter
        try {
            val fromDate = formatter.parse(from)
            val toDate = formatter.parse(to)
            var diffInDays =
                TimeUnit.DAYS.convert(toDate.time - fromDate.time, TimeUnit.MILLISECONDS) + 1
            if (diffInDays <= 0) {
                diffInDays = 1
            }
            return diffInDays.toInt()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 1
    }

    @JvmStatic
    fun dateDiffInDays(from: Date, to: Date): Int {
        return TimeUnit.DAYS.convert(to.time - from.time, TimeUnit.MILLISECONDS).toInt()
    }

    @JvmStatic
    fun asCalendar(date: String?): Calendar {
        return try {
            val cal = calendar()
            cal.time = parse(date)
            cal
        } catch (ex: Exception) {
            calendar()
        }
    }

    @JvmStatic
    fun asString(src: Calendar?): String {
        return formatDate(src?.time)
    }

    @JvmStatic
    val defaultDateLength: Int
        get() = dateFormat.length

    @JvmStatic
    fun formatDate(date: Date?): String {
        return DateFormat.format(dateFormat, date).toString()
    }

    @JvmStatic
    val currentDate: String
        get() = DateFormat.format(dateFormat, now()).toString()

    @JvmStatic
    fun getDate(year: Int, month: Int, day: Int): Date {
        val cal = calendar()
        cal[Calendar.YEAR] = year
        cal[Calendar.MONTH] = month
        cal[Calendar.DAY_OF_MONTH] = day
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        cal[Calendar.MILLISECOND] = 0
        return cal.time
    }

    @JvmStatic
    fun getDatePart(dateString: String?): String {
        return formatDate(getDatePart(parse(dateString)))
    }

    @JvmStatic
    fun getDatePart(dateTime: Date?): Date {
        val calendar = calendar()
        calendar.time = dateTime ?: now()
        return getDate(
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
    }


    @JvmStatic
    fun dateBefore(baseDateString: String?, selectedDate: Date) =
        selectedDate.before(parse(baseDateString))

    @JvmStatic
    fun dateAfter(baseDateString: String?, selectedDate: Date) =
        selectedDate.after(parse(baseDateString))

    @JvmStatic
    fun dateBeforeOrEquals(givenDateString: String?, currentDate: String?): Boolean {
        val selectedDate = parse(currentDate)
        val givenDate = parse(givenDateString)
        return givenDate.before(selectedDate) || givenDate == selectedDate
    }

    @JvmStatic
    fun dateAfterOrEquals(givenDateString: String?, currentDate: String?): Boolean {
        val selectedDate = parse(currentDate)
        val givenDate = parse(givenDateString)
        return givenDate.after(selectedDate) || givenDate == selectedDate
    }

    @JvmStatic
    fun dateBefore(givenDateString: String?, currentDate: String?) =
        parse(givenDateString).before(parse(currentDate))

    @JvmStatic
    fun areEqual(first: String?, second: String?): Boolean {
        val fmt = formatter
        return fmt.format(parse(first)) == fmt.format(parse(second))
    }

    @JvmStatic
    fun areEqual(firs: Date?, second: Date?): Boolean {
        val fmt = formatter
        return fmt.format(firs) == fmt.format(second)
    }

    @JvmStatic
    fun tryParse(date: String?): Date? {
        return try {
            formatter.parse(date)
        } catch (ex: Exception) {
            null
        }
    }

    @JvmStatic
    fun parse(date: String?) = tryParse(date) ?: now()

    @JvmStatic
    fun isDateInRange(startDate: String?, endDate: String?, selectedDate: String?): Boolean {
        val start = parse(startDate)
        val end = parse(endDate)
        val selected = parse(selectedDate)
        return selected.before(end) && selected.after(start) || startDate == selectedDate || endDate == selectedDate
    }

    @JvmStatic
    fun isDateInRange(startDate: Date, endDate: Date, selectedDate: Date): Boolean {
        val start = calendar()
        val end = calendar()
        val selected = calendar()
        start.time = startDate
        start[Calendar.DAY_OF_MONTH] = 1
        start[Calendar.HOUR_OF_DAY] = 0
        start[Calendar.MINUTE] = 0
        start[Calendar.SECOND] = 0
        start[Calendar.MILLISECOND] = 0
        end.time = endDate
        end[Calendar.DAY_OF_MONTH] = 29
        end[Calendar.HOUR_OF_DAY] = 0
        end[Calendar.MINUTE] = 0
        end[Calendar.SECOND] = 0
        end[Calendar.MILLISECOND] = 0
        selected.time = selectedDate
        selected[Calendar.DAY_OF_MONTH] = 10
        return selected.after(start) && selected.before(end)
    }

    @JvmStatic
    fun parseMonthAndYear(month: Int, year: Int): Calendar {
        val calendar = calendar()
        calendar.clear()
        calendar[year, month] = 1
        return calendar
    }

    @JvmStatic
    fun addDays(date: Date?, days: Int): Date {
        val cal = calendar()
        cal.time = date
        cal.add(Calendar.DATE, days)
        return cal.time
    }

    @JvmStatic
    fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    @JvmStatic
    fun alreadyInPastForSeconds(timeMillis: Long, secondsPassed: Int): Boolean {
        return (System.currentTimeMillis() - timeMillis) / 1000 > secondsPassed
    }

    @JvmStatic
    fun addDays(date: String?, days: Int): String {
        return try {
            var d = parse(date)
            d = addDays(d, days)
            formatDate(d)
        } catch (ex: Exception) {
            ex.printStackTrace()
            date.orEmpty()
        }
    }

    @JvmStatic
    fun createFormatter(format: String?, setDefTimezone: Boolean = false): SimpleDateFormat {
        return SimpleDateFormat(
            format.default(dateFormat),
            Locale.getDefault()
        ).also { if (setDefTimezone) it.timeZone = TimeZone.getDefault() }
    }

    @JvmStatic
    fun calendar(): Calendar = Calendar.getInstance()

    @JvmStatic
    val calendarTime: Date
        get() = calendar().time
}