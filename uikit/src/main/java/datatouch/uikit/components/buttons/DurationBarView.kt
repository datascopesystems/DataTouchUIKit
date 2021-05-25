package datatouch.uikit.components.buttons

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import datatouch.uikit.R
import datatouch.uikit.core.callbacks.UiJustCallback
import datatouch.uikit.core.extensions.GenericExtensions.default
import datatouch.uikit.databinding.DurationBarViewBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DurationBarView : LinearLayout {

    private val ui = DurationBarViewBinding
        .inflate(LayoutInflater.from(context), this, true)

    var onDateFromClickCallback: UiJustCallback? = null
    var onDateToClickCallback: UiJustCallback? = null
    var onDurationClickCallback: UiJustCallback? = null

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    var dateFrom: String
        get() = ui.tvDateFrom.text.toString()
        set(value) {
            ui.tvDateFrom.text = value
            updateDurationDays()
        }

    var dateTo: String
        get() = ui.tvDateTo.text.toString()
        set(value) {
            ui.tvDateTo.text = value
            updateDurationDays()
        }

    override fun onFinishInflate() {
        super.onFinishInflate()
        ui.tvDateFrom.setOnClickListener { onDateFromClick() }
        ui.tvDateFromLabel.setOnClickListener { onDateFromClick() }
        ui.ivDateFrom.setOnClickListener { onDateFromClick() }

        ui.tvDateTo.setOnClickListener { onDateToClick() }
        ui.tvDateToLabel.setOnClickListener { onDateToClick() }
        ui.ivDateTo.setOnClickListener { onDateToClick() }

        ui.tvDuration.setOnClickListener { onDurationClick() }
    }

    private fun updateDurationDays() {
        val dayDifference =
            dateDiffInDays(ui.tvDateFrom.text.toString(), ui.tvDateTo.text.toString())
        val durationString =
            resources.getQuantityString(R.plurals.duration_days, dayDifference, dayDifference)
        ui.tvDuration.text = durationString
    }

    fun setTextToDurationDays(text: String) {
        ui.tvDuration.text = text
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
