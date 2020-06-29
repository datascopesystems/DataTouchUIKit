package datatouch.uikit.components.datapicker

import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import datatouch.uikit.R
import java.util.*

object DatePickerUtils {
    private const val PULSE_ANIMATOR_DURATION = 544
    private const val START_DATE_RANGE = 2012
    private const val END_DATE_RANGE = 2030
    val yearsRange: ArrayList<String>
        get() {
            val years = ArrayList<String>()
            for (i in START_DATE_RANGE until END_DATE_RANGE) years.add(i.toString())
            return years
        }

    @JvmStatic
    fun getMonthsAsStringArray(context: Context): List<String> {
        return listOf(*context.resources.getStringArray(R.array.months))
    }

    @JvmStatic
    @SuppressLint("NewApi")
    fun tryAccessibilityAnnounce(view: View?, text: CharSequence?) {
        if (view != null && text != null) {
            view.announceForAccessibility(text)
        }
    }

    @JvmStatic
    fun getPulseAnimator(
        labelToAnimate: View?, decreaseRatio: Float,
        increaseRatio: Float
    ): ObjectAnimator {
        val k0 = Keyframe.ofFloat(0f, 1f)
        val k1 = Keyframe.ofFloat(0.275f, decreaseRatio)
        val k2 = Keyframe.ofFloat(0.69f, increaseRatio)
        val k3 = Keyframe.ofFloat(1f, 1f)
        val scaleX = PropertyValuesHolder.ofKeyframe("scaleX", k0, k1, k2, k3)
        val scaleY = PropertyValuesHolder.ofKeyframe("scaleY", k0, k1, k2, k3)
        val pulseAnimator = ObjectAnimator.ofPropertyValuesHolder(labelToAnimate, scaleX, scaleY)
        pulseAnimator.duration = PULSE_ANIMATOR_DURATION.toLong()
        return pulseAnimator
    }

    fun dpToPx(dp: Float, resources: Resources): Int {
        val px =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
        return px.toInt()
    }

    @JvmStatic
    fun darkenColor(color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] = hsv[2] * 0.8f // value component
        return Color.HSVToColor(hsv)
    }

    @JvmStatic
    fun getAccentColorFromThemeIfAvailable(context: Context): Int {
        val typedValue = TypedValue()
        // First, try the android:colorAccent
        if (Build.VERSION.SDK_INT >= 21) {
            context.theme.resolveAttribute(android.R.attr.colorAccent, typedValue, true)
            return typedValue.data
        }
        // Next, try colorAccent from support lib
        val colorAccentResId =
            context.resources.getIdentifier("colorAccent", "attr", context.packageName)
        return if (colorAccentResId != 0 && context.theme.resolveAttribute(
                colorAccentResId,
                typedValue,
                true
            )
        ) {
            typedValue.data
        } else ContextCompat.getColor(context, R.color.amdp_accent_color)
        // Return the value in amdp_accent_color
    }

    @JvmStatic
    fun isDarkTheme(context: Context, current: Boolean): Boolean {
        return resolveBoolean(context, R.attr.amdp_theme_dark, current)
    }

    private fun resolveBoolean(context: Context, @AttrRes attr: Int, fallback: Boolean): Boolean {
        val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
        return try {
            a.getBoolean(0, fallback)
        } finally {
            a.recycle()
        }
    }

    @JvmStatic
    fun trimToMidnight(calendar: Calendar): Calendar {
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar
    }
    
}