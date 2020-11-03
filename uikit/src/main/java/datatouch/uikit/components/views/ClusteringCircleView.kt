package datatouch.uikit.components.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.AbsoluteLayout
import androidx.appcompat.widget.AppCompatTextView
import datatouch.uikit.core.utils.Conditions.isNotNull
import datatouch.uikit.core.utils.Conditions.isNull

class ClusteringCircleView : AbsoluteLayout {
    private var circlePaint: Paint? = null
    private var color = 0x5a14f700
    private var radius = 0f
    private var layoutParamsLoc: ViewGroup.LayoutParams
    private var viewX = 0
    private var viewY = 0
    private var isPositionAdjustmentRequired = true

    constructor(context: Context?, attrs: AttributeSet?) : super(
            context,
            attrs
    ) {
        initPaints()
        layoutParamsLoc = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams = layoutParamsLoc
    }

    constructor(
            context: Context?,
            circlePosition: PointF,
            radius: Float,
            circleColor: Int
    ) : super(context) {
        color = circleColor
        initPaints()
        layoutParamsLoc = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setLayoutParams(layoutParamsLoc)
        setupCircle(circlePosition, radius)
    }

    private fun initPaints() {
        setBackgroundColor(Color.TRANSPARENT)
        if (isNull(circlePaint)) {
            circlePaint = Paint()
            circlePaint!!.color = color
            circlePaint!!.style = Paint.Style.FILL
            circlePaint!!.strokeWidth = STROKE_WIDTH.toFloat()
        }
    }

    fun setRadius(newRadius: Int) {
        radius = newRadius.toFloat()
        this.invalidate()
    }

    fun setupCircle(circlePosition: PointF, radius: Float) {
        isPositionAdjustmentRequired = true
        this.radius = radius
        val diameter = Math.round(this.radius * 2)

        // Hack to avoid cutted view
        minimumWidth = diameter
        minimumHeight = diameter
        viewX = Math.round(circlePosition.x - radius)
        viewY = Math.round(circlePosition.y - radius)
        x = viewX.toFloat()
        y = viewY.toFloat()
        invalidate()
    }

    private fun adjustMarkersPositions() {
        val markerRect = RectF()
        var hotspotMarker: View
        var lp: LayoutParams
        for (i in 0 until childCount) {
            hotspotMarker = getChildAt(i)
            lp = hotspotMarker.layoutParams as LayoutParams
            markerRect[lp.x.toFloat(), lp.y.toFloat(), lp.x + hotspotMarker.width.toFloat()] =
                lp.y + hotspotMarker.height.toFloat()
            applyHotspotMarkerConstraint(markerRect, lp)
            hotspotMarker.layoutParams = lp
        }
    }

    fun addHotspotPointMarker(realHotspotPos: PointF, hotspotId: Int) {
        isPositionAdjustmentRequired = true
        realHotspotPos.x = realHotspotPos.x - viewX
        realHotspotPos.y = realHotspotPos.y - viewY
        addView(HotspotPointMarkerItem(context, realHotspotPos, hotspotId))
    }

    fun addHotspotButtonMarker(
            realHotspotPos: PointF,
            hotspotButton: View?
    ) {
        isPositionAdjustmentRequired = true
        realHotspotPos.x = realHotspotPos.x - viewX
        realHotspotPos.y = realHotspotPos.y - viewY
        addView(
                hotspotButton,
                createLayoutParams(realHotspotPos.x, realHotspotPos.y)
        )
    }

    override fun onDraw(canvas: Canvas) {
        val x = radius
        val y = radius
        canvas.drawCircle(x, y, radius, circlePaint!!)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Hack to make onDrag work
        visibility = View.GONE
        visibility = View.VISIBLE
    }

    override fun measureChild(
            child: View,
            parentWidthMeasureSpec: Int,
            parentHeightMeasureSpec: Int
    ) {
        val lp = child.layoutParams as LayoutParams
        val childWidthMeasureSpec =
            ViewGroup.getChildMeasureSpec(parentWidthMeasureSpec, 0, lp.width)
        val childHeightMeasureSpec =
            ViewGroup.getChildMeasureSpec(parentHeightMeasureSpec, 0, lp.height)
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        var maxW = 0
        var maxH = 0
        var v: View
        val count = childCount
        var right: Int
        var bottom: Int
        var scaleX: Float
        var scaleY: Float
        var width: Float
        var height: Float
        var lp: LayoutParams
        for (i in 0 until count) {
            v = getChildAt(i)
            if (v.visibility != View.GONE) {
                lp = v.layoutParams as LayoutParams
                scaleX = v.scaleX
                scaleY = v.scaleY
                width =
                    if (scaleX > 1F) v.measuredWidth * scaleX.toFloat() else v.measuredWidth.toFloat()
                height =
                    if (scaleY > 1F) v.measuredHeight * scaleY.toFloat() else v.measuredHeight.toFloat()
                right = lp.x + width.toInt()
                bottom = lp.y + height.toInt()
                maxW = Math.max(maxW, right)
                maxH = Math.max(maxH, bottom)
            }
        }
        maxW = Math.max(maxW, suggestedMinimumWidth)
        maxH = Math.max(maxH, suggestedMinimumHeight)
        setMeasuredDimension(
                View.resolveSizeAndState(maxW, widthMeasureSpec, 0),
                View.resolveSizeAndState(maxH, heightMeasureSpec, 0)
        )
    }

    override fun onLayout(
            changed: Boolean,
            l: Int,
            t: Int,
            r: Int,
            b: Int
    ) {
        if (isPositionAdjustmentRequired) {
            isPositionAdjustmentRequired = false
            adjustMarkersPositions()
        }
        var v: View
        var lp: LayoutParams
        val count = childCount
        for (i in 0 until count) {
            v = getChildAt(i)
            if (v.visibility != View.GONE) {
                lp = v.layoutParams as LayoutParams
                v.layout(lp.x, lp.y, lp.x + v.measuredWidth, lp.y + v.measuredHeight)
            }
        }
    }

    private fun applyHotspotMarkerConstraint(
            markerRect: RectF,
            markerLayoutParams: LayoutParams
    ) {
        if (radius <= markerRect.width()) {
            return
        }
        if (markerRect.left < radius) {
            var offset =
                calculateConstraintOffset(radius, radius, markerRect.left, markerRect.top)
            if (isNotNull(offset)) {
                markerRect.offset(offset!!.x, offset.y)
            }
            offset = calculateConstraintOffset(radius, radius, markerRect.left, markerRect.bottom)
            if (isNotNull(offset)) {
                markerRect.offset(offset!!.x, offset.y)
            }
        } else {
            var offset =
                calculateConstraintOffset(radius, radius, markerRect.right, markerRect.top)
            if (isNotNull(offset)) {
                markerRect.offset(offset!!.x, offset.y)
            }
            offset = calculateConstraintOffset(radius, radius, markerRect.right, markerRect.bottom)
            if (isNotNull(offset)) {
                markerRect.offset(offset!!.x, offset.y)
            }
        }
        markerLayoutParams.x = markerRect.left.toInt()
        markerLayoutParams.y = markerRect.top.toInt()
    }

    private fun calculateConstraintOffset(
            centerX: Float,
            centerY: Float,
            endX: Float,
            endY: Float
    ): PointF? {
        val distance: Float = PointMath.getDistance(endX, endY, centerX, centerY)
        if (distance <= radius) {
            return null
        }
        val lineAngle: Float = PointMath.getAngleRad(centerX, centerY, endX, endY)
        val offset = distance - radius
        val offsetPoint: PointF = PointMath.getPolarLineEndPoint(endX, endY, -offset, lineAngle)
        PointMath.sub(offsetPoint, endX, endY)
        return offsetPoint
    }

    fun removeAllMarkers() {
        if (childCount > 0) {
            removeAllViews()
        }
    }

    private class HotspotPointMarkerItem(
            context: Context?,
            hotspotPos: PointF,
            hotspotId: Int
    ) : AppCompatTextView(context) {
        private val pointMarkerPadding =
            TEXT_PADDING / 2.0f

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawCircle(
                    pointMarkerPadding,
                    height - pointMarkerPadding,
                    pointMarkerPadding,
                    paint
            )
        }

        companion object {
            private const val TEXT_HEIGHT = 14f
            private const val TEXT_Y_OFFSET = TEXT_HEIGHT
            private const val TEXT_PADDING = 5f
            private const val TEXT_COLOR = -0x1000000
        }

        init {
            text = hotspotId.toString()
            textSize = TEXT_HEIGHT
            setTextColor(TEXT_COLOR)
            compoundDrawablePadding = 0
            includeFontPadding = false
            setPaddingRelative(0, 0, 0, 0)
            setPadding(
                    TEXT_PADDING.toInt(),
                    0,
                    0,
                    TEXT_PADDING.toInt()
            )
            layoutParams = createLayoutParams(
                    hotspotPos.x - pointMarkerPadding,
                    hotspotPos.y - TEXT_Y_OFFSET + pointMarkerPadding
            )
        }
    }

    companion object {
        const val STROKE_WIDTH = 3
        private fun createLayoutParams(x: Float, y: Float): ViewGroup.LayoutParams {
            return LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    x.toInt(),
                    y.toInt()
            )
        }
    }

    object PointMath {
        fun getLength(x: Float, y: Float): Float {
            return Math.sqrt(x * x + y * y.toDouble()).toFloat()
        }

        fun getDistance(a: PointF, b: PointF): Float {
            val x = a.x - b.x
            val y = a.y - b.y
            return getLength(x, y)
        }

        fun getDistance(ax: Float, ay: Float, bx: Float, by: Float): Float {
            val x = ax - bx
            val y = ay - by
            return getLength(x, y)
        }

        fun sum(a: PointF, b: PointF): PointF {
            return PointF(a.x + b.x, a.y + b.y)
        }

        fun add(dst: PointF, src: PointF) {
            dst.x += src.x
            dst.y += src.y
        }

        fun sub(dst: PointF, src: PointF) {
            dst.x -= src.x
            dst.y -= src.y
        }

        fun sub(dst: PointF, srcX: Float, srcY: Float) {
            dst.x -= srcX
            dst.y -= srcY
        }

        fun getMiddle(a: PointF, b: PointF): PointF {
            val res = sum(a, b)
            res.x /= 2f
            res.y /= 2f
            return res
        }

        fun getAngleRad(ax: Float, ay: Float, bx: Float, by: Float): Float {
            val xd = bx - ax
            val yd = by - ay
            return Math.atan2(yd.toDouble(), xd.toDouble()).toFloat()
        }

        fun getPolarLineEndPoint(startX: Float, startY: Float, lineLen: Float, angleRad: Float): PointF {
            val result = PointF()
            result.x = startX + lineLen * Math.cos(angleRad.toDouble()).toFloat()
            result.y = startY + lineLen * Math.sin(angleRad.toDouble()).toFloat()
            return result
        }

        fun getPolarLineEndPoint(startX: Float, startY: Float, lineLen: Float, angleRad: Float, result: PointF) {
            result.x = startX + lineLen * Math.cos(angleRad.toDouble()).toFloat()
            result.y = startY + lineLen * Math.sin(angleRad.toDouble()).toFloat()
        }

        fun getPolarLineEndPoint(start: PointF, lineLen: Float, angleRad: Float, result: PointF) {
            result.x = start.x + lineLen * Math.cos(angleRad.toDouble()).toFloat()
            result.y = start.y + lineLen * Math.sin(angleRad.toDouble()).toFloat()
        }
    }
}