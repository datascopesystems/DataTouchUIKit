package datatouch.uikit.core.utils.views

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import datatouch.uikit.core.utils.imaging.BitmapUtils

object ScreenshotUtils {

    private const val THUMB_WIDTH_DP = 300
    private const val THUMB_HEIGHT_DP = 100

    private fun getBitmapFromView(view: View) =
        Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888).also {
            view.draw(Canvas(it))
        }

    fun getScreenshotFromView(view: View): Screenshot {
        val screenshotFactory = ScreenshotFactory(
            IntSize.ofDP(view.context, THUMB_WIDTH_DP, THUMB_HEIGHT_DP), Bitmap.Config.RGB_565)

        val thumb = screenshotFactory.fromView(view)
        screenshotFactory.drop()
        return thumb
    }

    fun getImageBase64FromView(view: View): String {
        val bitmap = getBitmapFromView(view)
        val base64Image = BitmapUtils.convertBitmapToStringNoWrap(bitmap)
        bitmap.recycle()
        return base64Image
    }

}