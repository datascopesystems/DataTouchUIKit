package datatouch.uikit.components.views

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import datatouch.uikit.R

class CircularProgressBarView(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs) {
    private var progress = 0f
    private var progressMax = DEFAULT_MAX_VALUE
    private var strokeWidth =
        resources.getDimension(R.dimen.default_stroke_width)
    private var backgroundStrokeWidth =
        resources.getDimension(R.dimen.default_background_stroke_width)
    private val glowRadius =
        DEFAULT_GLOW_RADIUS.toFloat()
    private var drawProgressGlow = false
    private var color = Color.BLACK
    private var backgroundColor = Color.GRAY
    private var rightToLeft = true
    var isIndeterminateMode = false
        private set
    private var startAngle = DEFAULT_START_ANGLE
    private var progressAnimator: ValueAnimator? = null
    private var indeterminateModeHandler: Handler? = null
    private var rectF: RectF = RectF()
    private val backgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val foregroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glowPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private fun init(
        context: Context,
        attrs: AttributeSet
    ) {
        val typedArray = context.theme
            .obtainStyledAttributes(attrs, R.styleable.CircularProgressBarView, 0, 0)
        try {
            progress =
                typedArray.getFloat(R.styleable.CircularProgressBarView_cpb_progress, progress)
            progressMax = typedArray.getFloat(
                R.styleable.CircularProgressBarView_cpb_progress_max,
                progressMax
            )
            isIndeterminateMode = typedArray.getBoolean(
                R.styleable.CircularProgressBarView_cpb_indeterminate_mode,
                isIndeterminateMode
            )
            strokeWidth = typedArray.getDimension(
                R.styleable.CircularProgressBarView_cpb_progressbar_width,
                strokeWidth
            )
            backgroundStrokeWidth = typedArray.getDimension(
                R.styleable.CircularProgressBarView_cpb_background_progressbar_width,
                backgroundStrokeWidth
            )
            color =
                typedArray.getInt(R.styleable.CircularProgressBarView_cpb_progressbar_color, color)
            backgroundColor = typedArray.getInt(
                R.styleable.CircularProgressBarView_cpb_background_progressbar_color,
                backgroundColor
            )
            drawProgressGlow = typedArray.getBoolean(
                R.styleable.CircularProgressBarView_cpb_show_progressbar_glow,
                false
            )
        } finally {
            typedArray.recycle()
        }

        initPaint()
    }

    private fun initPaint() {
        backgroundPaint.color = backgroundColor
        backgroundPaint.style = Paint.Style.STROKE
        backgroundPaint.strokeWidth = backgroundStrokeWidth

        foregroundPaint.color = color
        foregroundPaint.style = Paint.Style.STROKE
        foregroundPaint.strokeCap = Paint.Cap.ROUND
        foregroundPaint.strokeJoin = Paint.Join.ROUND
        foregroundPaint.strokeWidth = strokeWidth

        if (drawProgressGlow) {
            glowPaint.color = color
            glowPaint.style = Paint.Style.STROKE
            glowPaint.strokeWidth = strokeWidth
            glowPaint.maskFilter = BlurMaskFilter(glowRadius, BlurMaskFilter.Blur.OUTER)
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (isIndeterminateMode) enableIndeterminateMode(true)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (progressAnimator != null) progressAnimator?.cancel()
        if (indeterminateModeHandler != null) indeterminateModeHandler?.removeCallbacks(
            indeterminateModeRunnable
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawOval(rectF, backgroundPaint)
        val realProgress =
            progress * DEFAULT_MAX_VALUE / progressMax
        val angle = (if (rightToLeft) 360 else -360) * realProgress / 100
        canvas.drawArc(rectF, startAngle, angle, false, foregroundPaint)

        if (drawProgressGlow) {
            canvas.drawArc(rectF, startAngle, angle, false, glowPaint)
        }
    }

    private fun reDraw() {
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height =
            getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width =
            getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val min = Math.min(width, height)
        setMeasuredDimension(min, min)
        calculateRect(min)
    }

    private fun calculateRect(minSize: Int) {
        if (minSize <= 0) {
            return
        }
        var highStroke = Math.max(strokeWidth, backgroundStrokeWidth)
        if (drawProgressGlow) {
            highStroke += glowRadius * 1.4.toFloat()
        }
        rectF[0 + highStroke / 2, 0 + highStroke / 2, minSize - highStroke / 2] = minSize - highStroke / 2
    }

    fun getProgress(): Float {
        return progress
    }

    fun setProgress(progress: Float) {
        setProgress(progress, false)
    }

    private fun setProgress(progress: Float, fromAnimation: Boolean) {
        if (!fromAnimation && progressAnimator != null) {
            progressAnimator?.cancel()
            if (isIndeterminateMode) enableIndeterminateMode(false)
        }
        this.progress = Math.min(progress, progressMax)
        invalidate()
    }

    fun getColor(): Int {
        return color
    }

    fun setColor(color: Int) {
        this.color = color
        foregroundPaint.color = color
        reDraw()
    }

    fun getBackgroundColor(): Int {
        return backgroundColor
    }

    override fun setBackgroundColor(backgroundColor: Int) {
        this.backgroundColor = backgroundColor
        backgroundPaint.color = backgroundColor
        reDraw()
    }

    fun setProgressWithAnimation(progress: Float) {
        setProgressWithAnimation(
            progress,
            DEFAULT_ANIMATION_DURATION
        )
    }

    fun setProgressWithAnimation(progress: Float, duration: Int) {
        if (progressAnimator != null) {
            progressAnimator?.cancel()
        }
        progressAnimator = ValueAnimator.ofFloat(this.progress, progress)
        progressAnimator?.duration = duration.toLong()
        progressAnimator?.addUpdateListener(AnimatorUpdateListener { animation: ValueAnimator ->
            val progress1 = animation.animatedValue as Float
            setProgress(progress1, true)
            if (isIndeterminateMode) {
                val updateAngle = progress1 * 360 / 100
                startAngle =
                    DEFAULT_START_ANGLE + if (rightToLeft) updateAngle else -updateAngle
            }
        })
        progressAnimator?.start()
    }

    private val indeterminateModeRunnable: Runnable = object : Runnable {
        override fun run() {
            if (isIndeterminateMode) {
                indeterminateModeHandler?.postDelayed(
                    this,
                    DEFAULT_ANIMATION_DURATION.toLong()
                )
                rightToLeft = !rightToLeft
                if (rightToLeft) {
                    setProgressWithAnimation(0f)
                } else {
                    setProgressWithAnimation(progressMax)
                }
            }
        }
    }

    fun enableIndeterminateMode(enable: Boolean) {
        isIndeterminateMode = enable
        rightToLeft = true
        startAngle = DEFAULT_START_ANGLE
        if (indeterminateModeHandler != null) indeterminateModeHandler?.removeCallbacks(
            indeterminateModeRunnable
        )
        if (progressAnimator != null) progressAnimator?.cancel()
        indeterminateModeHandler = Handler()
        if (isIndeterminateMode) {
            indeterminateModeHandler?.post(indeterminateModeRunnable)
        } else {
            setProgress(0f, true)
        }
    }

    fun setStrokeWidth(width: Float) {
        strokeWidth = width
        initPaint()
        val min = Math.min(measuredWidth, measuredHeight)
        calculateRect(min)
    }

    fun setBackgroundStrokeWidth(width: Float) {
        backgroundStrokeWidth = width
        initPaint()
        val min = Math.min(measuredWidth, measuredHeight)
        calculateRect(min)
    }

    companion object {
        private const val DEFAULT_MAX_VALUE = 100f
        private const val DEFAULT_START_ANGLE = 270f
        private const val DEFAULT_ANIMATION_DURATION = 1500
        private const val DEFAULT_GLOW_RADIUS = 10
    }

    init {
        init(context, attrs)
    }
}