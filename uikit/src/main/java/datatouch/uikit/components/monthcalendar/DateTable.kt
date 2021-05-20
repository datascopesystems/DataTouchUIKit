package datatouch.uikit.components.monthcalendar

import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter.ofPattern
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoUnit
import org.threeten.bp.temporal.WeekFields
import java.util.*
import kotlin.collections.ArrayList


class DateTable {

    private val daysArray: Array<Array<DateCell?>> =  Array(ROW_COUNT) {
        Array<DateCell?>(COL_COUNT) {
            null
        }
    }

    private var currentMonth: LocalDate = LocalDate.now()
    private var weekDaysArray = arrayOfNulls<DayOfWeek>(7)

    private var todayYear:Int = 0
    private var todayMonth:Int = 0
    private var todayDay:Int = 0

    init {
        setupToday()
        setupCurrentMonth()
        setupWeekDays()
        fillStartOfMonth()
        fillMonth()
    }

    private fun setupToday() {
        val date = LocalDate.now()
        todayYear = date.year
        todayMonth = date.monthValue
        todayDay = date.dayOfMonth
    }

    private fun setupCurrentMonth() {
        val date = LocalDate.now()
        val y = date.year
        val m = date.monthValue
        currentMonth = LocalDate.of(y, m, 1)
    }

    private fun setupWeekDays() {
        val weekFields: WeekFields = WeekFields.of(Locale.getDefault())
        var dayOfWeek: DayOfWeek = weekFields.firstDayOfWeek
        for (i in 0 until 7) {
            weekDaysArray[i] = dayOfWeek
            dayOfWeek = dayOfWeek.plus(1)
        }
    }

    private fun getWeekDayIndex(dayOfWeek: DayOfWeek): Int {
        for (i in 0 until weekDaysArray.size) {
            if (weekDaysArray[i] == dayOfWeek) {
                return i
            }
        }
        return 0
    }

    private fun setDateCell(row: Int, col: Int, date: LocalDate) {
        var cell = daysArray[row][col]
        if (cell == null) {
            cell = DateCell(date, false)
            daysArray[row][col] = cell
        } else {
            cell.date = date
            cell.isSelected = false
        }
    }

    private fun fillStartOfMonth() {
        val dayOfWeek = currentMonth.dayOfWeek
        val endIndex = getWeekDayIndex(dayOfWeek)
        if (endIndex <= 0) {
            return
        }

        var prevMonth = currentMonth.minusMonths(1)
        prevMonth = LocalDate.of(prevMonth.year, prevMonth.month, prevMonth.lengthOfMonth())
        for (i in endIndex - 1 downTo 0) {
            setDateCell(0, i, prevMonth)
            prevMonth = prevMonth.minusDays(1)
        }
    }

    private fun fillMonth() {
        val dayOfWeek = currentMonth.dayOfWeek
        var startIndex = getWeekDayIndex(dayOfWeek)

        var month = LocalDate.from(currentMonth)
        for (i in 0 until ROW_COUNT) {
            for (j in startIndex until COL_COUNT) {
                setDateCell(i, j, month)
                month = month.plusDays(1)
            }
            startIndex = 0
        }
    }

    fun getMonthStartDate(): LocalDate {
        return LocalDate.of(currentMonth.year , currentMonth.monthValue, 1)
    }

    fun getMonthEndDate(): LocalDate {
        val startDate = getMonthStartDate()
        val daysCount = startDate.lengthOfMonth() - 1
        return startDate.plusDays(daysCount.toLong())
    }

    fun getCurrentMonth(): LocalDate {
        return currentMonth
    }

    fun setCurrentMonth(date: LocalDate) {
        val y = date.year
        val m = date.monthValue

        if (currentMonth.year == y && currentMonth.monthValue == m) {
            return
        }

        currentMonth = LocalDate.of(y, m, 1)
        fillStartOfMonth()
        fillMonth()
    }

    fun moveNextMonth() {
        currentMonth = currentMonth.plusMonths(1)
        fillStartOfMonth()
        fillMonth()
    }

    fun movePrevMonth() {
        currentMonth = currentMonth.minusMonths(1)
        fillStartOfMonth()
        fillMonth()
    }

    fun getRowCount(): Int {
        return ROW_COUNT
    }

    fun getColCount(): Int {
        return COL_COUNT
    }

    fun getDate(row: Int, col: Int): LocalDate {
        return daysArray[row][col]!!.date
    }

    fun isSelectedDate(row: Int, col: Int): Boolean {
        return daysArray[row][col]!!.isSelected
    }

    fun isToday(row: Int, col: Int): Boolean {
        val date = daysArray[row][col]!!.date
        return date.dayOfMonth == todayDay && date.monthValue == todayMonth && date.year == todayYear
    }

    fun getDisplayYear(): String {
        return currentMonth.format(ofPattern("yyyy", Locale.getDefault()))
    }

    fun getDisplayMonth(): String {
        return currentMonth.format(ofPattern("MMMM", Locale.getDefault()))
    }

    fun getDisplayWeekDays(): Array<String> {
        val locale = Locale.getDefault()
        return Array(weekDaysArray.size) {
            weekDaysArray[it]!!.getDisplayName(TextStyle.SHORT_STANDALONE, locale)
        }
    }

    fun isDayBelongsToCurrentMonth(date: LocalDate): Boolean {
        val y = currentMonth.year
        val m = currentMonth.monthValue
        return date.year == y && date.monthValue == m
    }

    fun isDateBelongsToCalendarGrid(date: LocalDate): Boolean {
        val leftTop = getDate(0, 0)
        val rightBottom = getDate(getRowCount() - 1, getColCount() - 1)
        if (date.isAfter(leftTop) || date.isEqual(leftTop)) {
            if (date.isBefore(rightBottom) || date.isEqual(rightBottom)) {
                return true
            }
        }
        return false
    }

    fun getIndexFromDate(date: LocalDate?): Int {
        if (date != null && isDateBelongsToCalendarGrid(date)) {
            val leftTop = getDate(0, 0)
            return ChronoUnit.DAYS.between(leftTop, date).toInt()
        }
        return -1
    }

    fun setSelected(index: Int, selected: Boolean) {
        if (index < 0) {
            return
        }
        val col = index % getColCount()
        val row = index / getColCount()
        val cell = daysArray[row][col]
        cell?.isSelected = selected
    }

    fun setSelected(date: LocalDate, selected: Boolean) {
        val index = getIndexFromDate(date)
        setSelected(index, selected)
    }

    fun getCellType(row: Int, col: Int): ButtonCellType {
        val lastCol = getColCount() - 1
        val lastRow = getRowCount() - 1

        if (row == 0 && col == 0) {
            return ButtonCellType.TOP_LEFT
        }
        if (row == 0 && col == lastCol) {
            return ButtonCellType.TOP_RIGHT
        }
        if (row == lastRow && col == 0) {
            return ButtonCellType.BOTTOM_LEFT
        }
        if (row == lastRow && col == lastCol) {
            return ButtonCellType.BOTTOM_RIGHT
        }

        return ButtonCellType.MIDDLE
    }

    fun getMonthList(): List<String> {
        val list = ArrayList<String>()
        val formatter = ofPattern("MMMM", Locale.getDefault())
        for (i in 1..12) {
            val month = LocalDate.of(currentMonth.year, i, 1)
            list.add(month.format(formatter))
        }
        return list
    }


    fun getYearList(): List<String> {
        val list = ArrayList<String>()
        for (i in MIN_YEAR..MAX_YEAR) {
            list.add(i.toString())
        }
        return list
    }

    fun createDateFromMonth(monthValue: Int): LocalDate {
        return LocalDate.of(currentMonth.year, monthValue, 1)
    }

    fun createDateFromYear(yearValue: Int): LocalDate {
        return LocalDate.of(yearValue, currentMonth.monthValue, 1)
    }

    fun getMinYear(): Int {
        return MIN_YEAR
    }

    private class DateCell(var date: LocalDate, var isSelected:Boolean)

    companion object {
        private const val ROW_COUNT = 6
        private const val COL_COUNT = 7

        private const val MIN_YEAR = 2012
        private const val MAX_YEAR = 2030
    }
}