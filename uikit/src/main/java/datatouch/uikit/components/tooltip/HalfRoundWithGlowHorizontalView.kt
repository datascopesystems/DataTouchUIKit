package datatouch.uikit.components.tooltip

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import datatouch.uikit.utils.ResourceUtils

class HalfRoundWithGlowHorizontalView : View {
    private var bitmap: Bitmap? = null
    private var layoutWidth = 0
    private var layoutHeight = 0
    private var color = Color.BLACK

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        parseAttributes(attrs)
        initViews()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        parseAttributes(attrs)
        initViews()
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        parseNativeAttributes(attrs)
    }

    @SuppressLint("ResourceType")
    private fun parseNativeAttributes(attrs: AttributeSet?) {
        val attrIndexes = intArrayOf(
            R.attr.layout_width,
            R.attr.layout_height,
            R.attr.paddingLeft,
            R.attr.paddingTop,
            R.attr.paddingRight,
            R.attr.paddingBottom
        )
        val typedArray = context.obtainStyledAttributes(attrs, attrIndexes, 0, 0)
        try {
            layoutWidth = typedArray.getLayoutDimension(
                0,
                DEFAULT_WIDTH
            )
            layoutHeight = typedArray.getLayoutDimension(
                1,
                DEFAULT_HEIGHT
            )
        } finally {
            typedArray.recycle()
        }
    }

    fun initViews() {
        applyNativeAttributes()
    }

    private fun applyNativeAttributes() {
        applyLayoutParams()
    }

    private fun applyLayoutParams() {
        val layoutParams = ViewGroup.LayoutParams(
            if (layoutWidth < 0) layoutWidth else ResourceUtils.convertDpToPixel(
                context,
                layoutWidth.toFloat()
            ).toInt(),
            if (layoutHeight < 0) layoutHeight else ResourceUtils.convertDpToPixel(
                context,
                layoutHeight.toFloat()
            ).toInt()
        )
        setLayoutParams(layoutParams)
    }

    override fun dispatchDraw(canvas: Canvas) {
        draw()
        canvas.drawBitmap(bitmap!!, 0f, 0f, null)
    }

    private fun draw() {
        bitmap = Bitmap.createBitmap(layoutWidth, layoutHeight, Bitmap.Config.ARGB_8888)
        bitmap?.apply {
            val canvas = Canvas(this)
            drawHemisphere(canvas)
            drawHemisphereBlur(canvas)
        }
    }

    private fun drawHemisphere(canvas: Canvas) {
        val paint =
            Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color
        paint.isAntiAlias = true
        drawAnchorArc(canvas, paint)
    }

    private fun drawAnchorArc(
        canvas: Canvas,
        paint: Paint
    ) {
        val rect = RectF(
            0F,
            (-(layoutHeight * 2)).toFloat(),
            layoutWidth.toFloat(),
            (layoutHeight / RECT_WIDTH_COEF).toFloat()
        )
        canvas.drawArc(
            rect,
            ARC_START_ANGLE.toFloat(),
            ARC_SWEEP_ANGLE.toFloat(),
            true,
            paint
        )
    }

    private fun drawHemisphereBlur(canvas: Canvas) {
        val accentPaint =
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        accentPaint.color = ColourUtils.makeColorTransparent(
            color,
            BLUR_ALPHA
        )
        accentPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DARKEN)
        accentPaint.maskFilter = BlurMaskFilter(
            ResourceUtils.convertDpToPixel(
                context,
                (layoutWidth / BLUR_RADIUS_COEF).toFloat()
            ), BlurMaskFilter.Blur.OUTER
        )
        drawAnchorArc(canvas, accentPaint)
    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {
        super.onLayout(changed, l, t, r, b)
    }

    fun setColor(color: Int) {
        this.color = color
        invalidate()
    }

    companion object {
        private const val RECT_WIDTH_COEF = 8
        private const val ARC_START_ANGLE = 20
        private const val ARC_SWEEP_ANGLE = 140
        private const val BLUR_RADIUS_COEF = 27
        private const val BLUR_ALPHA = 0x80
        private const val DEFAULT_WIDTH = 300
        private const val DEFAULT_HEIGHT = 100
    }
}