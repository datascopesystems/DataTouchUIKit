package datatouch.uikit.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import datatouch.uikit.R
import datatouch.uikit.databinding.NavigationButtonBinding

class NavigationButton : RelativeLayout {

    private val ui = NavigationButtonBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var layoutWidth = 0
    private var layoutHeight = 0

    private var title = ""

    constructor(context: Context?) : super(context) {
        initViews()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet
    ) : super(context, attrs) {
        parseAttributes(attrs)
        initViews()
    }


    constructor(
        context: Context?,
        attrs: AttributeSet,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        parseAttributes(attrs)
        initViews()
    }

    private fun parseAttributes(attrs: AttributeSet) {
        parseNativeAttributes(attrs)
        parseCustomAttributes(attrs)
    }

    @SuppressLint("ResourceType")
    private fun parseNativeAttributes(attrs: AttributeSet) {
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
            layoutWidth = typedArray.getLayoutDimension(
                0,
                MATCH_PARENT
            )

            layoutHeight = typedArray.getLayoutDimension(
                1,
                WRAP_CONTENT
            )
        } finally {
            typedArray.recycle()
        }
    }

    private fun parseCustomAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.NavigationButton, 0, 0
        )
        try {
            title = typedArray.getString(R.styleable.NavigationButton_nb_title).orEmpty()
        } finally {
            typedArray.recycle()
        }
    }

    fun initViews() {
        applyNativeAttributes()
        setupTitle()
    }

    private fun applyNativeAttributes() {
        applyLayoutParams()
    }

    private fun applyLayoutParams() {
        val defaultButtonSizePx =
            resources.getDimensionPixelSize(R.dimen.floating_circle_button_size)
        if (layoutWidth < 0) {
            if (LayoutParams.WRAP_CONTENT == layoutWidth) ui.rlRoot.layoutParams.width =
                defaultButtonSizePx
        } else ui.rlRoot.layoutParams.width = layoutWidth
        if (layoutHeight < 0) {
            if (LayoutParams.WRAP_CONTENT == layoutHeight) ui.rlRoot.layoutParams.height =
                defaultButtonSizePx
        } else ui.rlRoot.layoutParams.height = layoutHeight
    }

    fun setLayoutWidth(layoutWidth: Int) {
        this.layoutWidth = layoutWidth
        applyLayoutParams()
    }

    fun setLayoutHeight(layoutHeight: Int) {
        this.layoutHeight = layoutHeight
        applyLayoutParams()
    }

    fun setupTitle(title: String) {
        this.title = title
        setupTitle()
    }

    fun setupTitle() {
        ui.tvTitle.text = title
    }

}