package datatouch.uikit.components.monthcalendar

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListPopupWindow
import androidx.core.view.*
import datatouch.uikit.R
import datatouch.uikit.core.callbacks.UiCallback
import datatouch.uikit.core.callbacks.UiJustCallback
import datatouch.uikit.core.callbacks.UiJustValue
import datatouch.uikit.core.extensions.IntExtensions.dp2Px
import datatouch.uikit.core.utils.datetime.dateFormat
import datatouch.uikit.core.utils.views.IntSize
import datatouch.uikit.databinding.MonthCalendarViewBinding
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class MonthCalendarView<T : AbstractMonthCalendarButton> : LinearLayout {

    private val ui = MonthCalendarViewBinding.inflate(LayoutInflater.from(context), this, true)

    private val weekDayNames by lazy {
        listOf(ui.tvDay1, ui.tvDay2, ui.tvDay3, ui.tvDay4, ui.tvDay5, ui.tvDay6, ui.tvDay7)
    }

    private val buttonRows by lazy {
        listOf(ui.llRow1, ui.llRow2, ui.llRow3, ui.llRow4, ui.llRow5, ui.llRow6)
    }

    init {
        setListeners()
    }

    private fun setListeners() {
        ui.btnMonthPrev.setOnClickListener { btnMonthPrev() }
        ui.btnMonthNext.setOnClickListener { btnMonthNext() }
        ui.tvHeaderMonth.setOnClickListener { tvHeaderMonth() }
        ui.tvHeaderYear.setOnClickListener { tvHeaderYear() }
        ui.root.doOnAttach { renderUi() }
    }

    private val dateTable = DateTable()
    private val buttonList = ArrayList<T>()

    private val dateFormatter = DateTimeFormatter.ofPattern(dateFormat, Locale.getDefault())

    private var onDayClickCallback: UiCallback<String>? = null
    private var onNextMonthClickCallback: UiJustCallback? = null
    private var onPrevMonthClickCallback: UiJustCallback? = null
    private var onMonthYearSelectedCallback: UiJustCallback? = null

    private val dropdownMonth: ListPopupWindow = ListPopupWindow(context)
    private val dropdownYear: ListPopupWindow = ListPopupWindow(context)

    private var mainViewWidth = 0
    private var mainViewHeight = 0

    private var isDownsizeApplied = false

    private val isButtonsAdded: Boolean
        get() = buttonRows.first().childCount > 0

    private val minButtonSize: IntSize
        get() = buttonList.first().getMinSize()

    private val gridHolderTopPos: Int
        get() = ui.flGridHolder.top


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private fun renderUi() {
        setupEmptyStateView()
        setupWeekDayNames()
        setupDropdowns()
        setupHeaderUnderline()
        hideUiComponents()
    }

    private fun forEachRows(block: (index: Int, row: ViewGroup) -> Unit) {
        var idx = 0
        buttonRows.forEachIndexed { _, row ->
            for (j in 0 until dateTable.getColCount()) {
                block.invoke(idx, row)
                idx++
            }
        }
    }

    fun createButtons(buttonFactory: UiJustValue<T>) {
        forEachRows { _, _ ->
            buttonList.add(buttonFactory.invoke())
        }

        updateCalendarMonth()
    }

    fun setOnDayClickCallback(callback: UiCallback<String>) {
        onDayClickCallback = callback
    }

    fun setOnNextMonthClickCallback(callback: UiJustCallback) {
        onNextMonthClickCallback = callback
    }

    fun setOnPrevMonthClickCallback(callback: UiJustCallback) {
        onPrevMonthClickCallback = callback
    }

    fun setOnMonthYearSelectedCallback(callback: UiJustCallback) {
        onMonthYearSelectedCallback = callback
    }

    private fun setupHeaderUnderline() {
        ui.tvHeaderMonth.paint?.apply {
            isUnderlineText = true
        }
        ui.tvHeaderYear.paint?.apply {
            isUnderlineText = true
        }
    }

    private fun setupEmptyStateView() {
        ui.esv.showLoading()
    }

    private fun addButtonsToContainer() {
        if (!isButtonsAdded) {
            forEachRows { idx, row ->
                row.addView(buttonList[idx])
            }
        }
    }

    private fun updateEmptyStateSize(w: Int, h: Int) {
        setViewSize(ui.esv, w, h)
    }

    private fun updateButtonSize(w: Int, h: Int) {
        buttonList.forEach { it.setSize(w, h) }
    }

    private fun onMainSizeReady(mainW: Int, mainH: Int) {
        if (downsizeIfRequired(mainW, mainH)) {
            resetMainViewSize()
            return
        }

        val gridSize = calculateMonthGridSize(mainW, mainH)
        updateEmptyStateSize(gridSize.w, gridSize.h)

        val buttonSize = calculateButtonSize(gridSize.w, gridSize.h)
        updateButtonSize(buttonSize.w, buttonSize.h)

        addButtonsToContainer()
        setLayoutSizeWrapContent()
        showUiComponents()
    }

    private fun getHorizontalDividersWidth(): Int {
        return GRID_DIVIDER_WIDTH_DP.dp2Px(context) * (dateTable.getColCount() + 1)
    }

    private fun getVerticalDividersWidth(): Int {
        return GRID_DIVIDER_WIDTH_DP.dp2Px(context) * (dateTable.getRowCount() + 1)
    }

    private fun calculateMonthGridSize(mainW: Int, mainH: Int): IntSize {
        val w = mainW.minus(paddingStart)
                .minus(paddingEnd)
                .minus(getHorizontalDividersWidth())

        val h = mainH.minus(paddingTop)
                .minus(paddingBottom)
                .minus(getVerticalDividersWidth())

        return IntSize(w, h - gridHolderTopPos)
    }

    private fun calculateButtonSize(gridW: Int, gridH: Int): IntSize {
        val buttonW = gridW / dateTable.getColCount()
        val buttonH = gridH / dateTable.getRowCount()
        return IntSize(buttonW, buttonH)
    }

    private fun downsizeIfRequired(mainW: Int, mainH: Int): Boolean {
        if (!isDownsizeApplied) {
            val gridSize = calculateMonthGridSize(mainW, mainH)
            val buttonSize = calculateButtonSize(gridSize.w, gridSize.h)

            val (minButtonW, minButtonH) = minButtonSize

            if (buttonSize.w < minButtonW || buttonSize.h < minButtonH) {
                disableHorizontalPadding()
                disableVerticalPadding()

                disableHorizontalMargin()
                disableVerticalMargin()

                downsizeHeader()
                downsizeWeekDayNames()

                isDownsizeApplied = true
                return true
            }
        }

        return false
    }

    private fun disableHorizontalPadding() {
        setPaddingRelative(0, paddingTop, 0, paddingBottom)
    }

    private fun disableVerticalPadding() {
        setPaddingRelative(paddingStart, 0, paddingEnd, 0)
    }

    private fun disableHorizontalMargin() {
        updateLayoutParams {
            if (this is MarginLayoutParams) {
                if (marginStart > 1) {
                    marginStart = 1.dp2Px(context)
                }
                if (marginEnd > 1) {
                    marginEnd = 1.dp2Px(context)
                }
            }
        }
    }

    private fun disableVerticalMargin() {
        updateLayoutParams {
            if (this is MarginLayoutParams) {
                if (topMargin > 1) {
                    topMargin = 1.dp2Px(context)
                }
                if (bottomMargin > 1) {
                    bottomMargin = 1.dp2Px(context)
                }
            }
        }
    }

    private fun downsizeHeader() {
        ui.btnMonthPrev.updateLayoutParams {
            width = HEADER_MIN_BUTTONS_SIZE_DP.dp2Px(context)
            height = HEADER_MIN_BUTTONS_SIZE_DP.dp2Px(context)
        }

        ui.btnMonthNext.updateLayoutParams {
            width = HEADER_MIN_BUTTONS_SIZE_DP.dp2Px(context)
            height = HEADER_MIN_BUTTONS_SIZE_DP.dp2Px(context)
        }

        ui.tvHeaderMonth.setTextSize(TypedValue.COMPLEX_UNIT_SP, HEADER_MIN_TEXT_SIZE_SP)
        ui.tvHeaderYear.setTextSize(TypedValue.COMPLEX_UNIT_SP, HEADER_MIN_TEXT_SIZE_SP)
    }

    private fun downsizeWeekDayNames() {
        weekDayNames.forEach {
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, WEEK_DAYS_MIN_TEXT_SIZE_SP)
        }
    }

    private fun setupWeekDayNames() {
        val names = dateTable.getDisplayWeekDays()
        weekDayNames.forEachIndexed { i, textView ->
            textView.text = names[i]
        }
    }

    private fun applySelectedDate(date: LocalDate) {
        setCurrentMonth(date)
        post { onMonthYearSelectedCallback?.invoke() }
    }

    private fun setupDropdowns() {
        dropdownMonth.anchorView = ui.tvHeaderMonth
        dropdownMonth.setBackgroundDrawable(ColorDrawable(DROPDOWN_BACKGROUND_COLOR))
        dropdownMonth.setAdapter(ArrayAdapter(context, R.layout.month_calendar_dropdown_item, dateTable.getMonthList()))
        dropdownMonth.setOnItemClickListener { _, _, position, _ ->
            dropdownMonth.dismiss()
            val monthValue = position + 1
            applySelectedDate(dateTable.createDateFromMonth(monthValue))
        }

        dropdownYear.anchorView = ui.tvHeaderYear
        dropdownYear.setBackgroundDrawable(ColorDrawable(DROPDOWN_BACKGROUND_COLOR))
        dropdownYear.setAdapter(ArrayAdapter(context, R.layout.month_calendar_dropdown_item, dateTable.getYearList()))
        dropdownYear.setOnItemClickListener { _, _, position, _ ->
            dropdownYear.dismiss()
            val yearValue = position + dateTable.getMinYear()
            applySelectedDate(dateTable.createDateFromYear(yearValue))
        }
    }

    private fun updateCalendarMonth() {
        ui.tvHeaderMonth.text = dateTable.getDisplayMonth()
        ui.tvHeaderYear.text = dateTable.getDisplayYear()

        if (buttonList.isEmpty()) {
            return
        }

        for (i in 0 until dateTable.getRowCount()) {
            val idx = i * dateTable.getColCount()
            for (j in 0 until dateTable.getColCount()) {
                val button = buttonList[idx + j]
                setupDateButton(button, i, j)
            }
        }
    }

    private fun setupDateButton(button: T, row: Int, col: Int) {
        button.resetButtonState()

        val cellType = dateTable.getCellType(row, col)
        button.setCellType(cellType)

        val date = dateTable.getDate(row, col)
        button.setDate(date)

        button.setTodayMarker(dateTable.isToday(row, col))

        val selected = dateTable.isSelectedDate(row, col)
        button.isSelected = selected

        val activeDay = dateTable.isDayBelongsToCurrentMonth(date)
        button.setIsActiveDay(activeDay)

        button.setOnClickCallback(this::onDateButtonClick)
    }

    private fun dateToString(date: LocalDate?): String {
        return date?.format(dateFormatter) ?: ""
    }

    private fun stringToDate(date: String): LocalDate? {
        return try {
            LocalDate.parse(date, dateFormatter)
        } catch (e: Exception) {
            null
        }
    }

    private fun onDateButtonClick(date: LocalDate?) {
        val dateStr = dateToString(date)
        post { onDayClickCallback?.invoke(dateStr) }
    }

    fun applyButtonByDate(date: String, block: UiCallback<T>) {
        val localDate = stringToDate(date)
        val index = dateTable.getIndexFromDate(localDate)
        if (index >= 0) {
            block.invoke(buttonList[index])
        }
    }

    fun setSelectedDate(date: String, selected: Boolean) {
        val localDate = stringToDate(date)
        val index = dateTable.getIndexFromDate(localDate)
        if (index >= 0) {
            dateTable.setSelected(index, selected)
            val button = buttonList[index]
            button.isSelected = selected
        }
    }

    fun setCurrentMonth(date: String) {
        if (date.isEmpty()) {
            return
        }
        setCurrentMonth(stringToDate(date))
    }

    private fun setCurrentMonth(date: LocalDate?) {
        date?.let {
            dateTable.setCurrentMonth(it)
            updateCalendarMonth()
        }
    }

    fun getTableStartDate(): String {
        return dateToString(dateTable.getDate(0, 0))
    }

    fun getTableEndDate(): String {
        val row = dateTable.getRowCount() - 1
        val col = dateTable.getColCount() - 1
        return dateToString(dateTable.getDate(row, col))
    }

    private fun moveToNextMonth() {
        dateTable.moveNextMonth()
        updateCalendarMonth()
    }

    private fun moveToPrevMonth() {
        dateTable.movePrevMonth()
        updateCalendarMonth()
    }

    fun showLoadingState() {
        ui.esv.isVisible = true
    }

    fun showCalendarState() {
        ui.esv.isGone = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setLayoutSizeMax()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (mainViewWidth <= 0 || mainViewHeight <= 0) {
            mainViewWidth = w
            mainViewHeight = h
            post {
                startGridCalculation(GRID_CALCULATION_TRY_COUNT)
            }
        }
    }

    private fun tvHeaderMonth() {
        ui.tvHeaderMonth.let {
            val width = (it.width.toFloat() / 1.5f).toInt()
            val offset = it.width - width

            dropdownMonth.horizontalOffset = offset
            dropdownMonth.width = width
            dropdownMonth.height = ui.llMonthGrid.height
            dropdownMonth.show()

            val currentMonthPos = dateTable.getCurrentMonth().monthValue - 1
            post { dropdownMonth.setSelection(currentMonthPos) }
        }
    }

    private fun tvHeaderYear() {
        ui.tvHeaderYear.let {
            dropdownYear.width = (it.width.toFloat() / 1.5f).toInt()
            dropdownYear.height = ui.llMonthGrid.height
            dropdownYear.show()

            val currentYearPos = dateTable.getCurrentMonth().year - dateTable.getMinYear()
            post { dropdownYear.setSelection(currentYearPos) }
        }
    }


    private fun btnMonthPrev() {
        moveToPrevMonth()
        post { onPrevMonthClickCallback?.invoke() }
    }

    private fun btnMonthNext() {
        moveToNextMonth()
        post { onNextMonthClickCallback?.invoke() }
    }

    private fun resetMainViewSize() {
        mainViewWidth = 0
        mainViewHeight = 0
    }

    private fun startGridCalculation(tryCount: Int) {
        if (tryCount <= 0 || gridHolderTopPos > 0) {
            onMainSizeReady(mainViewWidth, mainViewHeight)
            return
        }

        post { startGridCalculation(tryCount - 1) }
    }

    private fun setUiComponentsVisibility(isHidden: Boolean) {
        ui.llHeader.isInvisible = isHidden
        ui.llWeekDayNames.isInvisible = isHidden
        ui.flGridHolder.isInvisible = isHidden
    }

    private fun hideUiComponents() = setUiComponentsVisibility(true)

    private fun showUiComponents() = setUiComponentsVisibility(false)

    private fun setLayoutSizeMax() {
        getWindowRect().let {
            setViewSize(ui.root, it.width(), it.height())
        }
    }

    private fun setLayoutSizeWrapContent() {
        setViewSize(ui.root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun setViewSize(v: View?, w: Int, h: Int) {
        v?.updateLayoutParams {
            width = w
            height = h
        }
    }

    private fun getWindowRect(): Rect {
        val rect = Rect()

        this.display?.getRectSize(rect)
        if (!rect.isEmpty) {
            return rect
        }

        getWindowVisibleDisplayFrame(rect)
        if (!rect.isEmpty) {
            return rect
        }

        return rect
    }

    companion object {
        private const val DROPDOWN_BACKGROUND_COLOR = 0xF0636363.toInt()
        private const val GRID_DIVIDER_WIDTH_DP = 1
        private const val GRID_CALCULATION_TRY_COUNT = 4
        private const val HEADER_MIN_BUTTONS_SIZE_DP = 40
        private const val HEADER_MIN_TEXT_SIZE_SP = 25f
        private const val WEEK_DAYS_MIN_TEXT_SIZE_SP = 12f
    }
}
