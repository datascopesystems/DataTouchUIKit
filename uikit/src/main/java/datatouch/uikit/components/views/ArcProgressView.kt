package datatouch.uikit.components.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import datatouch.uikit.R
import datatouch.uikit.utils.ResourceUtils.convertDpToPixel

class ArcProgressView : View {
    private var bitmap: Bitmap? = null
    private var startAngle =
        DEFAULT_ARC_START_ANGLE.toFloat()
    private var glowRadius = DEFAULT_GLOW_RADIUS.toFloat()
    private var strokeWidth = DEFAULT_STROKE_WIDTH.toFloat()
    private var progressStrokeWidth =
        DEFAULT_STROKE_WIDTH.toFloat()
    private var layoutWidth = 0
    private var layoutHeight = 0
    private var progressColor = Color.YELLOW
    private var backgroundClor = Color.GRAY
    private var maxProgress = 100
    private var progress = 0
    private var drawProgressGlow = false

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
        parseCustomAttributes(attrs)
        initViews()
    }

    @SuppressLint("ResourceType")
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
            layoutWidth = typedArray.getLayoutDimension(0, DEFAULT_WIDTH)
            layoutHeight =
                typedArray.getLayoutDimension(1, DEFAULT_HEIGHT)
        } finally {
            typedArray.recycle()
        }
    }

    private fun parseCustomAttributes(attrs: AttributeSet?) {
        @SuppressLint("CustomViewStyleable") val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.ArcProgressView, 0, 0
            )
        try {
            progressStrokeWidth = typedArray.getDimensionPixelSize(
                R.styleable.ArcProgressView_arc_stroke_width,
                DEFAULT_STROKE_WIDTH
            ).toFloat()
            strokeWidth = progressStrokeWidth
            val tempProgressStrokeWidth = typedArray.getDimensionPixelSize(
                R.styleable.ArcProgressView_arc_progress_width_stroke,
                0
            )
            if (tempProgressStrokeWidth > 0) {
                progressStrokeWidth = tempProgressStrokeWidth.toFloat()
            }
            backgroundClor = typedArray.getColor(
                R.styleable.ArcProgressView_arc_inactive_color,
                Color.GRAY
            )
            progressColor = typedArray.getColor(
                R.styleable.ArcProgressView_arc_active_color,
                Color.GREEN
            )
            startAngle = typedArray.getInteger(
                R.styleable.ArcProgressView_arc_start_angle,
                DEFAULT_ARC_START_ANGLE
            ).toFloat()
            drawProgressGlow =
                typedArray.getBoolean(R.styleable.ArcProgressView_arc_progress_glow, false)
            glowRadius = typedArray.getDimensionPixelSize(
                R.styleable.ArcProgressView_arc_progress_glow_radius,
                DEFAULT_GLOW_RADIUS
            ).toFloat()
            progress = typedArray.getInteger(R.styleable.ArcProgressView_arc_progress_value, 0)
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
            if (layoutWidth < 0) layoutWidth else convertDpToPixel(
                context,
                layoutWidth.toFloat()
            ).toInt(),
            if (layoutHeight < 0) layoutHeight else convertDpToPixel(
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
        bitmap?.also {
            val canvas = Canvas(it)
            drawBackgroundArc(canvas)
            drawProgressArc(canvas)
            drawProgressArcGlowIfRequired(canvas)
        }
    }

    private fun drawBackgroundArc(canvas: Canvas) {
        val paint =
            Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.color = backgroundClor
        drawArc(canvas, paint, DEFAULT_SWEEP_ANGLE)
    }

    private fun drawArc(
        canvas: Canvas,
        paint: Paint,
        sweepAngle: Int
    ) {
        val rect = RectF(
            strokeWidth / 2F + if (drawProgressGlow) glowRadius / 2F + DEFAULT_RADIUS_PADDING else 0F,
            strokeWidth / 2F + if (drawProgressGlow) glowRadius / 2F + DEFAULT_RADIUS_PADDING else 0F,
            layoutWidth - strokeWidth / 2F - if (drawProgressGlow) glowRadius / 2F + DEFAULT_RADIUS_PADDING else 0F,
            layoutHeight - strokeWidth / 2F - if (drawProgressGlow) glowRadius / 2F + DEFAULT_RADIUS_PADDING else 0F
        )
        canvas.drawArc(rect, startAngle, sweepAngle.toFloat(), false, paint)
    }

    private fun drawProgressArc(canvas: Canvas) {
        val paint =
            Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = progressColor
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeWidth = progressStrokeWidth
        drawArc(canvas, paint, sweepAngle)
    }

    private fun drawProgressArcGlowIfRequired(canvas: Canvas) {
        if (!drawProgressGlow) return
        val paint =
            Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = progressColor
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeWidth = progressStrokeWidth
        paint.maskFilter = BlurMaskFilter(glowRadius, BlurMaskFilter.Blur.OUTER)
        drawArc(canvas, paint, sweepAngle)
    }

    private val sweepAngle: Int
        private get() = DEFAULT_SWEEP_ANGLE * progress / maxProgress

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {
        super.onLayout(changed, l, t, r, b)
    }

    fun setMaxProgress(maxProgress: Int) {
        this.maxProgress = maxProgress
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }

    fun getProgress(): Int {
        return progress
    }

    companion object {
        private const val DEFAULT_RADIUS_PADDING = 3
        private const val DEFAULT_SWEEP_ANGLE = 360
        private const val DEFAULT_GLOW_RADIUS = 10
        private const val DEFAULT_STROKE_WIDTH = 5
        private const val DEFAULT_WIDTH = 100
        private const val DEFAULT_HEIGHT = 100
        private const val DEFAULT_ARC_START_ANGLE = -90
    }
}