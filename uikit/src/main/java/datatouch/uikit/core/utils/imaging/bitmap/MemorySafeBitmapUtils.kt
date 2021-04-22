package datatouch.uikit.core.utils.imaging.bitmap

import android.graphics.Bitmap
import android.util.Base64
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.File

object MemorySafeBitmapUtils {

    @JvmStatic
    fun compressImageToBase64(filePath: String?, q: BitmapQuality = BitmapQuality.Medium): String {
        if (filePath.isNullOrEmpty()) return ""

        return compressImageToBase64(File(filePath), q)
    }

    @JvmStatic
    fun compressImageToBase64(file: File, q: BitmapQuality = BitmapQuality.Medium): String =
        runCatching {
            val size = getBitmapSize(q)
            var bitmap: Bitmap? = null

            // Picasso requires this to run in background
            val backgroundThread = Thread {
                // Height is automatically calculated by passing 0
                bitmap = Picasso.get().load(file).resize(size, 0).get()
            }

            backgroundThread.start()
            backgroundThread.join()

            return bitmapToBase64(bitmap, q)
        }.onFailure { exception -> exception.printStackTrace() }.getOrDefault("")

    // Do not expose this method - no bitmap size check!!!
    private fun bitmapToBase64(bitmap: Bitmap?, q: BitmapQuality): String = bitmap?.let {
        val bao = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, getBitmapCompressQuality(q), bao)
        return Base64.encodeToString(bao.toByteArray(), Base64.DEFAULT)
    } ?: ""

    private fun getBitmapSize(q: BitmapQuality): Int {
        return when (q) {
            BitmapQuality.Poor -> LowBitmapSizePx
            BitmapQuality.Medium -> MediumBitmapSizePx
            BitmapQuality.High -> HighBitmapSizePx
        }
    }

    private fun getBitmapCompressQuality(q: BitmapQuality): Int {
        return when (q) {
            BitmapQuality.Poor -> LowBitmapCompressQuality
            BitmapQuality.Medium -> MediumBitmapCompressQuality
            BitmapQuality.High -> HighBitmapCompressQuality
        }
    }

}

private const val LowBitmapSizePx = 300
private const val MediumBitmapSizePx = 450
private const val HighBitmapSizePx = 600

private const val LowBitmapCompressQuality = 40
private const val MediumBitmapCompressQuality = 70
private const val HighBitmapCompressQuality = 90