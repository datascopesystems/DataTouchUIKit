package datatouch.uikit.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleableRes
import androidx.core.content.ContextCompat
import datatouch.uikit.R

class ButtonGlow : View {

    private var bitmap: Bitmap? = null
    private var invalidate = true

    private var glowColor = 0
    private var glowRadiusPx = 0

    private var defaultGlowColor = 0
    private var defaultGlowRadiusPx = 0

    private var layoutWidth = 0
    private var layoutHeight = 0

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        initResources()
        parseAttributes(attrs)
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

    private fun initResources() {
        defaultGlowColor = ContextCompat.getColor(context, R.color.accent_end)
        defaultGlowRadiusPx =
            context.resources.getDimensionPixelSize(R.dimen.bg_default_glow_radius)
    }

    private fun parseCustomAttributes(attrs: AttributeSet?) {
        @SuppressLint("CustomViewStyleable") val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.ButtonGlow, 0, 0
            )
        try {
            glowColor = typedArray.getColor(R.styleable.ButtonGlow_bg_glow_color, defaultGlowColor)

            glowRadiusPx =
                typedArray.getDimensionPixelSize(
                    R.styleable.ButtonGlow_bg_glow_radius,
                    defaultGlowRadiusPx
                )
        } finally {
            typedArray.recycle()
        }
    }


    override fun dispatchDraw(canvas: Canvas) {
        if (invalidate || bitmap == null || bitmap?.isRecycled == true) draw()

        if (bitmap?.isRecycled == false)
            canvas.drawBitmap(bitmap!!, 0f, 0f, null)
    }

    private fun draw() {
        val width = measuredWidth
        val height = measuredHeight
        recycleBitmap()
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap!!)
        drawAccentBlurAnchorRect(canvas)
        invalidate = false
    }

    private fun recycleBitmap() = bitmap?.recycle()

    private fun drawAccentBlurAnchorRect(canvas: Canvas) {
        val glowPaint  = Paint(Paint.ANTI_ALIAS_FLAG)
        glowPaint.color = glowColor
        glowPaint.style = Paint.Style.FILL
        glowPaint.strokeWidth = 10f
        glowPaint.maskFilter = BlurMaskFilter(glowRadiusPx.toFloat(), BlurMaskFilter.Blur.INNER)
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        val rect = RectF(
            0f,
            measuredHeight.toFloat(),
            measuredWidth.toFloat(), 0f)

        canvas.drawRoundRect(rect, glowRadiusPx.toFloat(), glowRadiusPx.toFloat(), glowPaint)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        invalidate = true
    }

    fun setColor(color: Int) {
        glowColor = color
        invalidate()
    }

    fun setRadius(radiusPx: Int) {
        glowRadiusPx = radiusPx
        invalidate()
    }

}