package datatouch.uikit.core.extensions

import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import datatouch.uikit.core.utils.Conditions


object TextViewExtensions {

    inline fun TextView.setText(text: String?, @StringRes defaultResId: Int) =
        if (Conditions.isNotNullOrEmpty(text)) this.text = text
        else setText(defaultResId)

    inline fun TextView.setTextColorRes(@ColorRes colorRes: Int) =
        setTextColor(ContextCompat.getColor(context, colorRes))

    inline fun TextView.setHintTextColorRes(@ColorRes colorRes: Int) =
        setHintTextColor(ContextCompat.getColor(context, colorRes))

}