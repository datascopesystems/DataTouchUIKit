package datatouch.uikit.components.spinner

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.content.ContextCompat
import datatouch.uikit.R
import java.util.*

open class BetterSpinner : AppCompatAutoCompleteTextView, OnItemClickListener {
    private var startClickTime: Long = 0
    private var isPopup = false
    var position = ListView.INVALID_POSITION
        private set

    constructor(context: Context) : super(context) {
        onItemClickListener = this
    }

    constructor(arg0: Context, arg1: AttributeSet?) : super(
        arg0,
        arg1
    ) {
        onItemClickListener = this
    }

    constructor(
        arg0: Context,
        arg1: AttributeSet?,
        arg2: Int
    ) : super(arg0, arg1, arg2) {
        onItemClickListener = this
    }

    override fun enoughToFilter(): Boolean {
        return true
    }

    override fun onFocusChanged(
        focused: Boolean, direction: Int,
        previouslyFocusedRect: Rect?
    ) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused) {
            performFiltering("", 0)
            val imm =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
            keyListener = null
            dismissDropDown()
        } else {
            isPopup = false
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startClickTime = Calendar.getInstance().timeInMillis
            }
            MotionEvent.ACTION_UP -> {
                val clickDuration =
                    Calendar.getInstance().timeInMillis - startClickTime
                if (clickDuration < MAX_CLICK_DURATION) {
                    isPopup = if (isPopup) {
                        dismissDropDown()
                        false
                    } else {
                        requestFocus()
                        showDropDown()
                        true
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onItemClick(
        adapterView: AdapterView<*>?,
        view: View,
        position: Int,
        id: Long
    ) {
        this.position = position
        isPopup = false
    }

    override fun setCompoundDrawablesWithIntrinsicBounds(
        left: Drawable?,
        top: Drawable?,
        right: Drawable?,
        bottom: Drawable?
    ) {
        var right = right
        val dropdownIcon =
            ContextCompat.getDrawable(context, R.drawable.ic_expand_more_black_18dp)
        if (dropdownIcon != null) {
            right = dropdownIcon
            right.mutate().alpha = 128
        }
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
    }

    companion object {
        private const val MAX_CLICK_DURATION = 200
    }
}