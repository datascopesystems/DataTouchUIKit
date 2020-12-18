package datatouch.uikit.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.annotation.StyleableRes
import androidx.core.view.isVisible
import datatouch.uikit.R
import datatouch.uikit.core.callbacks.UiCallback
import datatouch.uikit.core.callbacks.UiJustCallback
import kotlinx.android.synthetic.main.action_toggle_button_small.view.*

class ToggleButtonSmall : RelativeLayout {

    private var layoutWidth = 0
    private var layoutHeight = 0

    var onCheckChangedCallback: UiCallback<Boolean>? = null
    var onLockedCallback: UiJustCallback? = null

    var checked = false
        set(value) {
            field = value
            flRoot?.setBackgroundResource(
                if (value) R.drawable.toggle_button_background_active
                else R.drawable.toggle_button_background_inactive
            )
            refreshLockView()
        }

    var locked = false
        set(value) {
            field = value
            refreshLockView()
        }

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        inflateView()
        parseAttributes(attrs)
        afterViews()
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        parseNativeAttributes(attrs)
        parseCustomAttributes(attrs)
    }

    private fun parseNativeAttributes(attrs: AttributeSet?) {
        val attrIndexes = intArrayOf(
            android.R.attr.layout_width,
            android.R.attr.layout_height,
            android.R.attr.paddingLeft,
            android.R.attr.paddingTop,
            android.R.attr.paddingRight,
            android.R.attr.paddingBottom
        )

        val typedArray = context.obtainStyledAttributes(attrs, attrIndexes, 0, 0)
        try {
            @StyleableRes val widthIndex = 0
            @StyleableRes val heightIndex = 1
            layoutWidth =
                typedArray.getLayoutDimension(widthIndex, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutHeight =
                typedArray.getLayoutDimension(heightIndex, ViewGroup.LayoutParams.WRAP_CONTENT)
        } finally {
            typedArray.recycle()
        }
    }

    private fun parseCustomAttributes(attrs: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.ToggleButtonSmall, 0, 0
            )
        try {
            checked = typedArray.getBoolean(R.styleable.ToggleButtonSmall_tbs_checked, false)
            locked = typedArray.getBoolean(R.styleable.ToggleButtonSmall_tbs_locked, false)
        } finally {
            typedArray.recycle()
        }
    }

    fun afterViews() {
        flRoot.setOnClickListener { onButtonClick() }
        checked = checked
        refreshLockView()
    }

    private fun inflateView() = View.inflate(context, R.layout.action_toggle_button_small, this)

    private fun onButtonClick() {
        if (locked) {
            onLockedCallback?.invoke()
        } else {
            checked = !checked
            refreshLockView()
            onCheckChangedCallback?.invoke(checked)
        }
    }

    private fun refreshLockView() {
        if (!locked) {
            ivLocked?.isVisible = false
            return
        }

        ivLocked?.isVisible = true
        val lp = ivLocked.layoutParams as FrameLayout.LayoutParams
        lp.gravity = Gravity.CENTER_VERTICAL or if (checked) Gravity.END else Gravity.START
    }

}