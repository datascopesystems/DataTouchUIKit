package datatouch.uikit.components.buttons

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import datatouch.uikit.R
import datatouch.uikit.interfaces.UiJustCallback
import datatouch.uikit.utils.Dates
import kotlinx.android.synthetic.main.duration_bar_view.view.*

class DurationBarView : LinearLayout {


    var onDateFromClickCallback: UiJustCallback? = null
    var onDateToClickCallback: UiJustCallback? = null
    var onDurationClickCallback: UiJustCallback? = null

    constructor(context: Context) : super(context) {
        inflateView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        inflateView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        inflateView()
    }

    var dateFrom: String
        get() = tvDateFrom?.text.toString()
        set(value) {
            tvDateFrom?.text = value
            updateDurationDays()
        }

    var dateTo: String
        get() = tvDateTo?.text.toString()
        set(value) {
            tvDateTo?.text = value
            updateDurationDays()
        }

    fun afterViews() {
        tvDateFrom.setOnClickListener { onDateFromClick() }
        tvDateFromLabel.setOnClickListener { onDateFromClick() }
        ivDateFrom.setOnClickListener { onDateFromClick() }

        tvDateTo.setOnClickListener { onDateToClick() }
        tvDateToLabel.setOnClickListener { onDateToClick() }
        ivDateTo.setOnClickListener { onDateToClick() }

        tvDuration.setOnClickListener { onDurationClick() }
    }

    private fun updateDurationDays() {
        val dayDifference =
            Dates.dateDiffInDays(tvDateFrom?.text.toString(), tvDateTo?.text.toString())
        val durationString =
            resources.getQuantityString(R.plurals.duration_days, dayDifference, dayDifference)
        tvDuration?.text = durationString
    }

    protected fun inflateView() {
        View.inflate(context, R.layout.duration_bar_view, this)
    }

    fun setTextToDurationDays(text: String) {
        tvDuration?.text = text
    }


    protected fun onDateFromClick() {
        onDateFromClickCallback?.invoke()
    }

    protected fun onDateToClick() {
        onDateToClickCallback?.invoke()
    }


    protected fun onDurationClick() {
        onDurationClickCallback?.invoke()
    }

}
