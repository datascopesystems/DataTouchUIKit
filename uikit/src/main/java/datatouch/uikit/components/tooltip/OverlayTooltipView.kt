package datatouch.uikit.components.tooltip

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import datatouch.uikit.R
import datatouch.uikit.utils.ResourceUtils.calculateRectOnScreen
import datatouch.uikit.utils.ResourceUtils.convertDipToPixels
import datatouch.uikit.utils.ResourceUtils.convertDpToPixel

class OverlayTooltipView(
    context: Context?,
    private val anchorView: View,
    private val callback: OnAnchorClickCallback
) : View(context) {
    private var marginLeft = 0f
    private var marginRight = 0f
    private var marginTop = 0f
    private var marginBottom = 0f
    private var bitmap: Bitmap? = null
    private var invalidate = true
    private var rect: RectF? = null
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (null == rect) return super.onTouchEvent(event)
        if (rect!!.contains(event.x, event.y)) callback.onAnchorClicked()
        return super.onTouchEvent(event)
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (invalidate || bitmap == null || bitmap!!.isRecycled) draw()
        // The bitmap is checked again because of Android memory cleanup behavior. (See #42)
        if (bitmap != null && !bitmap!!.isRecycled) canvas.drawBitmap(bitmap!!, 0f, 0f, null)
    }

    private fun draw() {
        val width = measuredWidth
        val height = measuredHeight
        recycleBitmapIfNeeded()
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap?.let {
            val canvas = Canvas(it)
            drawBackground(canvas, width, height)
            drawTransparentAnchorRect(canvas)
            drawAccentBlurAnchorRect(canvas)
        }
        invalidate = false
    }

    private fun recycleBitmapIfNeeded() {
        if (bitmap != null && !bitmap!!.isRecycled) bitmap!!.recycle()
    }

    private fun drawBackground(
        canvas: Canvas,
        width: Int,
        height: Int
    ) {
        val backgroundRect = RectF(0F, 0F, width.toFloat(), height.toFloat())
        val backgroundPaint =
            Paint(Paint.ANTI_ALIAS_FLAG)
        backgroundPaint.color = context.resources.getColor(R.color.primary)
        backgroundPaint.isAntiAlias = true
        backgroundPaint.alpha = HALF_TRANSPARENT
        canvas.drawRect(backgroundRect, backgroundPaint)
    }

    private fun drawTransparentAnchorRect(canvas: Canvas) {
        val transparentPaint =
            Paint(Paint.ANTI_ALIAS_FLAG)
        transparentPaint.color = Color.TRANSPARENT
        transparentPaint.isAntiAlias = true
        transparentPaint.alpha = FULLY_VISIBLE
        transparentPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        drawAnchorRect(canvas, transparentPaint)
    }

    private fun drawAnchorRect(
        canvas: Canvas,
        paint: Paint
    ) {
        val anchorRect = calculateRectOnScreen(anchorView)
        val overlayRect = calculateRectOnScreen(this)
        val left = anchorRect.left - overlayRect.left
        val top = anchorRect.top - overlayRect.top
        rect = RectF(
            left - marginLeft,
            top - marginTop,
            left + anchorView.measuredWidth + marginRight,
            top + anchorView.measuredHeight + marginBottom
        )
        val cornerRadiusDip = convertDipToPixels(
            context,
            CORNER_RADIUS
        )
        canvas.drawRoundRect(rect!!, cornerRadiusDip.toFloat(), cornerRadiusDip.toFloat(), paint)
    }

    private fun drawAccentBlurAnchorRect(canvas: Canvas) {
        val accentPaint =
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        accentPaint.color = context.resources.getColor(R.color.accent_end)
        accentPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.LIGHTEN)
        accentPaint.maskFilter = BlurMaskFilter(
            convertDpToPixel(
                context,
                GLOW_RADIUS
            ), BlurMaskFilter.Blur.OUTER
        )
        drawAnchorRect(canvas, accentPaint)
    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {
        super.onLayout(changed, l, t, r, b)
        invalidate = true
    }

    fun setMarginLeft(marginLeft: Float) {
        this.marginLeft = convertDpToPixel(context, marginLeft)
    }

    fun setMarginRight(marginRight: Float) {
        this.marginRight = convertDpToPixel(context, marginRight)
    }

    fun setMarginTop(marginTop: Float) {
        this.marginTop = convertDpToPixel(context, marginTop)
    }

    fun setMarginBottom(marginBottom: Float) {
        this.marginBottom = convertDpToPixel(context, marginBottom)
    }

    interface OnAnchorClickCallback {
        fun onAnchorClicked()
    }

    companion object {
        private const val GLOW_RADIUS = 50f
        private const val FULLY_VISIBLE = 1
        private const val HALF_TRANSPARENT = 0xE6
        private const val CORNER_RADIUS = 20f
    }

}