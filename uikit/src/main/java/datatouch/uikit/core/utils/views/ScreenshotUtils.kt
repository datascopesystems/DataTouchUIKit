package datatouch.uikit.core.utils.views

import android.graphics.Bitmap
import android.view.View

object ScreenshotUtils {

    fun makeScreenshot(view: View): Screenshot {
        val screenshotFactory = ScreenshotFactory(
            IntSize.ofDP(view.context, view.width, view.height), Bitmap.Config.RGB_565)

        val thumb = screenshotFactory.fromView(view)
        screenshotFactory.drop()
        return thumb
    }

}