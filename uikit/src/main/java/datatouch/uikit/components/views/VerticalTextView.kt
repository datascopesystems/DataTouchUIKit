package datatouch.uikit.components.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView

class VerticalTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    private var topDown = false

    init {
        val gravity = gravity
        topDown =
            if (Gravity.isVertical(gravity) && gravity and Gravity.VERTICAL_GRAVITY_MASK == Gravity.BOTTOM) {
                setGravity(gravity and Gravity.HORIZONTAL_GRAVITY_MASK or Gravity.TOP)
                false
            } else {
                true
            }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    override fun onDraw(canvas: Canvas) {
        try {
            val textPaint = paint
            textPaint.color = currentTextColor
            textPaint.drawableState = drawableState
            canvas.save()
            if (topDown) {
                canvas.translate(width.toFloat(), 0f)
                canvas.rotate(DEGREES90)
            } else {
                canvas.translate(0f, height.toFloat())
                canvas.rotate(-DEGREES90)
            }
            canvas.translate(compoundPaddingLeft.toFloat(), extendedPaddingTop.toFloat())
            layout.draw(canvas)
            canvas.restore()
        } catch (e: Exception) {
        }
    }

    companion object {
        const val DEGREES90 = 90f
    }
}