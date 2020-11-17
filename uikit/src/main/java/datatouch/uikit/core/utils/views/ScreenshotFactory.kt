package datatouch.uikit.core.utils.views

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.view.View
import kotlin.math.min

class ScreenshotFactory(thumbSize: IntSize, bitmapConfig: Bitmap.Config) {

    private val canvas = Canvas()
    private val matrix = Matrix()
    private val bitmapConfig: Bitmap.Config
    private val size = IntSize()

    init {
        this.size.set(thumbSize)
        this.bitmapConfig = bitmapConfig
    }

    fun drop() = canvas.setBitmap(null)

    private fun createScaledBitmap(requestedSize: IntSize, sourceSize: FloatSize): Bitmap? {
        if (sourceSize.isPositive) {
            val aspectW = requestedSize.w / sourceSize.w
            val aspectH = requestedSize.h / sourceSize.h
            val aspectRatio = min(aspectW, aspectH)

            val bitmapW = sourceSize.w * aspectRatio
            val bitmapH = sourceSize.h * aspectRatio

            return Bitmap.createBitmap(bitmapW.toInt(), bitmapH.toInt(), bitmapConfig)
        }

        return null
    }

    private fun drawViewToBitmap(view: View, viewSize: FloatSize, bitmap: Bitmap) {
        canvas.setBitmap(bitmap)
        matrix.setScale(bitmap.width / viewSize.w, bitmap.height / viewSize.h)
        canvas.setMatrix(matrix) // Matrix must be set after setBitmap() for correct scaling in Android 9
        view.draw(canvas)
    }

    fun fromView(view: View): Screenshot {
        val viewSize = FloatSize(view.width, view.height)

        val bitmap = createScaledBitmap(size, viewSize) ?: return Screenshot.EmptyScreenshot

        drawViewToBitmap(view, viewSize, bitmap)
        return Screenshot(bitmap, size)
    }
}