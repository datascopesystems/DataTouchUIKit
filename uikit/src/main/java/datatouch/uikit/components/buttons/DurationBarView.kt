package datatouch.uikit.components.buttons

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import datatouch.uikit.R
import datatouch.uikit.core.callbacks.UiJustCallback
import datatouch.uikit.core.extensions.GenericExtensions.default
import kotlinx.android.synthetic.main.duration_bar_view.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

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
            dateDiffInDays(tvDateFrom?.text.toString(), tvDateTo?.text.toString())
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

    companion object {
        private const val dateFormat = "dd/MM/yyyy"
        private val formatter = createFormatter(dateFormat)

        @JvmStatic
        fun createFormatter(format: String?, setDefTimezone: Boolean = false): SimpleDateFormat {
            return SimpleDateFormat(
                format.default(dateFormat),
                Locale.getDefault()
            ).also { if (setDefTimezone) it.timeZone = TimeZone.getDefault() }
        }

        @JvmStatic
        fun dateDiffInDays(from: String?, to: String?): Int {
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
    }
}
