package datatouch.uikit.core.utils.views

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import datatouch.uikit.core.utils.imaging.BitmapUtils

open class Screenshot(bitmap: Bitmap?, thumbSize: IntSize) {

    object EmptyScreenshot : Screenshot(null, IntSize())

    private var bitmap: Bitmap? = null
    private var paint: Paint? = Paint(Paint.ANTI_ALIAS_FLAG)
    private var left: Float = 0.0f
    private var top: Float = 0.0f

    var width = 0
        private set

    var height = 0
        private set

    init {
        this.bitmap = bitmap
        width = thumbSize.w
        height = thumbSize.h
        calculateLeftTop()
    }

    private fun calculateLeftTop() {
        bitmap?.let {
            left = ((width - it.width) / 2).toFloat()
            top = ((height - it.height) / 2).toFloat()
        }
    }

    fun drop() {
        bitmap?.recycle()
        bitmap = null
        paint = null
    }

    fun isEmpty(): Boolean {
        return bitmap == null
    }

    fun draw(canvas: Canvas, offsetX: Int, offsetY: Int) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap!!, left + offsetX, top + offsetY, paint)
        }
    }

    fun saveToFile(fileName: String) = bitmap?.let {
            BitmapUtils.saveBitmapToFile(it, fileName)
    }
}