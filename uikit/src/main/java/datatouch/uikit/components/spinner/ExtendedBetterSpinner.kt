package datatouch.uikit.components.spinner

import android.content.Context
import android.util.AttributeSet

class ExtendedBetterSpinner : BetterSpinner {
    constructor(context: Context) : super(context) {}
    constructor(arg0: Context, arg1: AttributeSet?) : super(
        arg0,
        arg1
    ) {
    }

    constructor(
        arg0: Context,
        arg1: AttributeSet?,
        arg2: Int
    ) : super(arg0, arg1, arg2) {
    }

    override fun performFiltering(text: CharSequence, keyCode: Int) {
        try {
            super.performFiltering(text, keyCode)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}