package datatouch.uikit.components.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import datatouch.uikit.R

class CLetterCircleView : View {
    // Variables
    private var circleColor = DEFAULT_CIRCLE_COLOR
    private var circlePaint: Paint? = null
    private var textPaint: Paint? = null
    var badgeLetter: String? = null
        private set
    private var centerY = 0f
    private var centerX = 0f
    private var radius = 0f
    private var ellipsizeType = 0
    private var textColor = 0
    private var textSize = 0
    private var usableHeight = 0
    private var usableWidth = 0
    private val textBounds = Rect()

    constructor(context: Context?) : super(context) {
        setupView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        parseAttributes(attrs)
        setupView()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        parseAttributes(attrs)
        setupView()
    }

    private fun setupView() {
        initDrawing()
    }

    private fun initDrawing() {
        circlePaint = Paint()
        circlePaint?.isAntiAlias = true
        circlePaint?.color = circleColor
        textPaint = Paint()
        textPaint?.isAntiAlias = true
        textPaint?.textSize = textSize.toFloat()
        textPaint?.color = textColor
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        // If not created manually from code
        if (attrs != null) {
            val a = context.obtainStyledAttributes(
                attrs,
                R.styleable.CircleLetterView, 0, 0
            )
            try {
                circleColor = a.getColor(
                    R.styleable.CircleLetterView_clvCircleColor,
                    DEFAULT_CIRCLE_COLOR
                )
                textColor = a.getColor(
                    R.styleable.CircleLetterView_clvTextColor,
                    DEFAULT_TEXT_COLOR
                )
                textSize = a.getDimensionPixelSize(
                    R.styleable.CircleLetterView_clvTextSize,
                    DEFAULT_TEXT_SIZE
                )
                badgeLetter = a.getString(R.styleable.CircleLetterView_clvLetter)
                ellipsizeType = a.getInt(
                    R.styleable.CircleLetterView_clvEllipsize,
                    ELLIPSIZE_FIT_FONT_SIZE
                )
            } finally {
                a.recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(centerX, centerY, radius, circlePaint!!)
        drawTextInsideCircle(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // Ensure width = height
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        // Respect paddings
        val pl = paddingLeft
        val pr = paddingRight
        val pt = paddingTop
        val pb = paddingBottom
        // Get real height and width
        usableWidth = width - (pl + pr)
        usableHeight = height - (pt + pb)
        // Detect center point
        centerX = pl + usableWidth / 2.toFloat()
        centerY = pt + usableHeight / 2.toFloat()
        radius = Math.min(usableWidth, usableHeight) / 2.toFloat()
        setMeasuredDimension(width, height)
    }

    fun setTextLetter(letter: String?) {
        badgeLetter = letter
        invalidate()
    }

    fun setCircleColor(circleColor: Int) {
        this.circleColor = circleColor
        circlePaint?.color = circleColor
        invalidate()
    }

    fun setTextColor(textColor: Int) {
        this.textColor = textColor
        textPaint?.color = textColor
        invalidate()
    }

    protected fun drawTextInsideCircle(canvas: Canvas) {
        when (ellipsizeType) {
            ELLIPSIZE_NONE -> ellipsizeTextNone(canvas)
            ELLIPSIZE_FIT_FONT_SIZE -> ellipsizeTextFitFontSize(canvas)
            else -> ellipsizeTextNone(canvas)
        }
    }

    // Ellipsize text methods
    private fun ellipsizeTextNone(canvas: Canvas) {
        val text = badgeLetter.toString()
        textPaint?.getTextBounds(text, 0, text.length, textBounds)
        drawTextCentred(canvas, textPaint, text)
    }

    private fun ellipsizeTextFitFontSize(canvas: Canvas) {
        val text = badgeLetter.toString()
        textPaint?.getTextBounds(text, 0, text.length, textBounds)
        val textWidth = textBounds.width().toFloat()
        val textRequiredWidth = textWidth + radius + paddingRight
        if (textRequiredWidth > usableWidth) {
            val widthPerCharacter = textWidth / text.length
            val availableCharsCount =
                Math.floor(usableWidth / widthPerCharacter.toDouble()).toInt()
            // No way to add even a dot
            if (availableCharsCount <= 1) {
                // Use default positions to draw text, handle somehow in another way
            } else {
                setTextSizeForWidth(
                    textPaint,
                    widthWithPaddings,
                    text,
                    textSize.toFloat(),
                    radius
                )
            }
        }
        drawTextCentred(canvas, textPaint, text)
    }

    private val widthWithPaddings: Float
        private get() = usableWidth - radius / 2

    fun drawTextCentred(
        canvas: Canvas,
        paint: Paint?,
        text: String
    ) {
        paint!!.getTextBounds(text, 0, text.length, textBounds)
        val xPos = (usableWidth / 2).toFloat()
        val yPos = (usableHeight / 2).toFloat()
        canvas.drawText(
            text,
            xPos - textBounds.exactCenterX(),
            yPos - textBounds.exactCenterY(),
            paint
        )
    }

    companion object {
        // Constants
        private const val ELLIPSIZE_NONE = 0
        private const val ELLIPSIZE_FIT_FONT_SIZE = 4

        // Default values
        private const val DEFAULT_CIRCLE_COLOR = Color.RED
        private const val DEFAULT_TEXT_COLOR = Color.WHITE
        const val DEFAULT_TEXT_SIZE = 18
        private const val MIN_TEXT_SIZE = 10
        private fun setTextSizeForWidth(
            paint: Paint?, desiredWidth: Float,
            text: String, defaultTextSize: Float, radius: Float
        ) {
            // Get the bounds of the text, using our testTextSize.
            paint!!.textSize = defaultTextSize
            val bounds = Rect()
            var textSize = defaultTextSize
            paint.getTextBounds(text, 0, text.length, bounds)
            while (bounds.width() > desiredWidth - desiredWidth / defaultTextSize) {
                textSize -= 2f
                paint.textSize = textSize
                paint.getTextBounds(text, 0, text.length, bounds)
            }
            // Set the paint for that size.
            paint.textSize = getNotLessThan(textSize)
        }

        private fun getNotLessThan(textSize: Float): Float {
            return if (textSize < MIN_TEXT_SIZE) MIN_TEXT_SIZE.toFloat() else textSize.toFloat()
        }
    }
}