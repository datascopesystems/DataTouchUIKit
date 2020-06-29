package datatouch.uikit.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.RelativeLayout
import datatouch.uikit.R


class RoundedRelativeLayout : RelativeLayout {

    companion object {
        private const val CORNER_NONE_DEFAULT = 0
        private const val CORNER_TOP_LEFT = 1
        private const val CORNER_TOP_RIGHT = 2
        private const val CORNER_BOTTOM_LEFT = 4
        private const val CORNER_BOTTOM_RIGHT = 8
    }

    private var rectF: RectF = RectF()
    private val path = Path()
    private var cornerRadius = 15f
    private var roundCorners = CORNER_NONE_DEFAULT

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        parseAttributes(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        parseAttributes(context, attrs)
    }

    private fun parseAttributes(context: Context, attrs: AttributeSet) {
        val ta = getContext().obtainStyledAttributes(attrs, R.styleable.RoundedRelativeLayout)

        try {
            cornerRadius = ta.getDimensionPixelSize(R.styleable.RoundedRelativeLayout_cornerRadius, 15).toFloat()
            roundCorners = ta.getInteger(R.styleable.RoundedRelativeLayout_roundCorners, CORNER_NONE_DEFAULT);
        } finally {
            ta.recycle()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rectF.set(0f, 0f, w.toFloat(), h.toFloat())
        resetPath()
    }

    override fun draw(canvas: Canvas) {
        val save = canvas.save()
        canvas.clipPath(path)
        super.draw(canvas)
        canvas.restoreToCount(save)
    }

    override fun dispatchDraw(canvas: Canvas) {
        val save = canvas.save()
        canvas.clipPath(path)
        super.dispatchDraw(canvas)
        canvas.restoreToCount(save)
    }

    private fun resetPath() {
        path.reset()

        var rx = cornerRadius
        var ry = cornerRadius
        if (rx < 0) rx = 0f
        if (ry < 0) ry = 0f
        val width = rectF.right - rectF.left
        val height = rectF.bottom - rectF.top
        if (rx > width / 2) rx = width / 2
        if (ry > height / 2) ry = height / 2
        val widthMinusCorners = width - 2 * rx
        val heightMinusCorners = height - 2 * ry

        path.moveTo(rectF.right, rectF.top + ry)
        if (containsFlag(roundCorners, CORNER_TOP_RIGHT))
            path.rQuadTo(0f, -ry, -rx, -ry)
        else {
            path.rLineTo(0f, -ry)
            path.rLineTo(-rx, 0f)
        }
        path.rLineTo(-widthMinusCorners, 0f)
        if (containsFlag(roundCorners, CORNER_TOP_LEFT))
            path.rQuadTo(-rx, 0f, -rx, ry)
        else {
            path.rLineTo(-rx, 0f)
            path.rLineTo(0f, ry)
        }
        path.rLineTo(0f, heightMinusCorners)

        if (containsFlag(roundCorners, CORNER_BOTTOM_LEFT))
            path.rQuadTo(0f, ry, rx, ry)
        else {
            path.rLineTo(0f, ry)
            path.rLineTo(rx, 0f)
        }

        path.rLineTo(widthMinusCorners, 0f)
        if (containsFlag(roundCorners, CORNER_BOTTOM_RIGHT))
            path.rQuadTo(rx, 0f, rx, -ry)
        else {
            path.rLineTo(rx, 0f)
            path.rLineTo(0f, -ry)
        }

        path.rLineTo(0f, -heightMinusCorners)

        path.close()
    }

    private fun containsFlag(flagSet: Int, flag: Int): Boolean {
        return flagSet or flag == flagSet
    }
}
