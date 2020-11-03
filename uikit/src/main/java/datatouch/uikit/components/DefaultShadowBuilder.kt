package datatouch.uikit.components

import android.graphics.*
import android.view.View
import android.view.View.DragShadowBuilder
import datatouch.uikit.core.utils.Conditions.isNotNull

class DefaultShadowBuilder(v: View?, shadowText: String?) :
    DragShadowBuilder(v) {
    private var p: Paint? = null
    private var shadowText: String? = null

    @Throws(Exception::class)
    private fun initPaint() {
        p = Paint()
        p!!.color = Color.BLACK
        p!!.textSize = DEFAULT_TEXT_SIZE
        p!!.isAntiAlias = true
        p!!.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
    }

    override fun onDrawShadow(canvas: Canvas) {
        super.onDrawShadow(canvas)
        try {
            canvas.drawText(
                shadowText!!,
                0f,
                DEFAULT_SHADOW_SHIFT.toFloat(),
                p!!
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onProvideShadowMetrics(
        shadowSize: Point,
        touchPoint: Point
    ) {
        shadowSize[DEFAULT_SHADOW_SHIFT] = DEFAULT_SHADOW_SHIFT
    }

    companion object {
        private const val DEFAULT_TEXT_SIZE = 8f
        private const val DEFAULT_SHADOW_SHIFT = 60
    }

    init {
        if (isNotNull(shadowText)) {
            this.shadowText = shadowText
        } else {
            this.shadowText = ""
        }
        try {
            initPaint()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}