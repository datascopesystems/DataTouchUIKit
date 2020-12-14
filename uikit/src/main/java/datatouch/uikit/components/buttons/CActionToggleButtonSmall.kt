package datatouch.uikit.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.StyleableRes
import datatouch.uikit.R
import datatouch.uikit.core.callbacks.UiCallback
import kotlinx.android.synthetic.main.action_toggle_button_small.view.*

class CActionToggleButtonSmall : RelativeLayout {

    private var layoutWidth = 0
    private var layoutHeight = 0

    var callback: UiCallback<Boolean>? = null

    var checked = false
        set(value) {
            field = value
            rlRoot?.setBackgroundResource(
                if (value) R.drawable.toggle_button_background_active
                else R.drawable.toggle_button_background_inactive)
        }

    constructor(context: Context) : super(context) {
        inflateView()
        parseAttributes(null)
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        inflateView()
        parseAttributes(attrs)
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle ) {
        inflateView()
        parseAttributes(attrs)
        initViews()
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
            layoutWidth = typedArray.getLayoutDimension(widthIndex, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutHeight = typedArray.getLayoutDimension(heightIndex, ViewGroup.LayoutParams.WRAP_CONTENT)
        } finally {
            typedArray.recycle()
        }
    }


    private fun parseCustomAttributes(attrs: AttributeSet?) {
        @SuppressLint("CustomViewStyleable") val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.CActionButton, 0, 0
            )
        checked = try {
            typedArray.getBoolean(R.styleable.CActionButton_checked, false)
        } finally {
            typedArray.recycle()
        }
    }

    fun initViews() {
        rlRoot.setOnClickListener { rootView() }
        checked = !checked
    }

    private fun inflateView() = View.inflate(context, R.layout.action_toggle_button_small, this)

    fun rootView() {
        checked = !checked
        callback?.invoke(checked)
    }

}