package datatouch.uikitapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import datatouch.uikit.components.monthcalendar.DefaultDayButton
import datatouch.uikit.components.monthcalendar.MonthCalendarView


class TestActivity : AppCompatActivity() {

    private val calendarView by lazy { findViewById(R.id.monthCalendarView) as? MonthCalendarView<DefaultDayButton> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

      /*  btn?.setOnClickListener {
            ivIcon?.showGifAnimation(R.drawable.an_guide_arrow, loopsCount = 1
            ) {
                ivIcon?.setImageDrawable(null)
            }
        }*/
        setupCalendarView()
    }


    private fun setupCalendarView() = calendarView?.apply {
        setCurrentMonth("")

        createButtons { DefaultDayButton(context) }

        setOnDayClickCallback(::onDateClick)
        setOnNextMonthClickCallback(::onNextMonthClick)
        setOnPrevMonthClickCallback(::onPrevMonthClick)
        setOnMonthYearSelectedCallback(::onMonthYearSelected)
    }

    private fun onNextMonthClick() {
    }

    private fun onPrevMonthClick() {
    }

    private fun onMonthYearSelected() {
    }

    private fun onDateClick(date: String) {
    }
}