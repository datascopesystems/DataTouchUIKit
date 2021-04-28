package datatouch.uikit.core.utils.imaging.bitmap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.min

object MemorySafeBitmapUtils {

    @JvmStatic
    fun compressImageToBase64(filePath: String?, q: BitmapQuality = BitmapQuality.Medium): String {
        if (filePath.isNullOrEmpty()) return ""

        return compressImageToBase64(File(filePath), q)
    }

    @JvmStatic
    fun compressImageToBase64(file: File, q: BitmapQuality = BitmapQuality.Medium): String =
        runCatching {
            val originalImageWidth = getOriginalBitmapWidth(file)
            val resizedBitmapWidth = getBitmapSize(q)
            val targetBitmapWidth = min(originalImageWidth, resizedBitmapWidth) // Should be resized to suggested size OR remaining same if original is smaller
            var bitmap: Bitmap? = null

            // Picasso requires this to run in background
            val backgroundThread = Thread {
                kotlin.runCatching { // Catch in separate thread
                    // Height is automatically calculated by passing 0
                    bitmap = Picasso.get().load(file).resize(targetBitmapWidth, 0).get()
                }
            }

            backgroundThread.start()
            backgroundThread.join()

            val base64String = bitmapToBase64(bitmap, q)
            bitmap?.recycle()
            return base64String
        }.onFailure { exception -> exception.printStackTrace() }.getOrDefault("")

    // Do not expose this method - no bitmap size check!!!
    private fun bitmapToBase64(bitmap: Bitmap?, q: BitmapQuality): String = bitmap?.let {
        val bao = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, getBitmapCompressQuality(q), bao)
        return Base64.encodeToString(bao.toByteArray(), Base64.DEFAULT)
    } ?: ""

    private fun getOriginalBitmapWidth(file: File) : Int {
        val options = readBitmapDimensions(file)
        return options.outWidth
    }

    private fun readBitmapDimensions(file: File) : BitmapFactory.Options {
        val options = BitmapFactory.Options().apply {
            // This means NOT allocating space for this bitmap, just reading very basic information
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(file.path, options)
        return options
    }

    private fun getBitmapSize(q: BitmapQuality): Int {
        return when (q) {
            BitmapQuality.Poor -> LowBitmapWidthPx
            BitmapQuality.Medium -> MediumBitmapWidthPx
            BitmapQuality.High -> HighBitmapWidthPx
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

private const val LowBitmapWidthPx = 640
private const val MediumBitmapWidthPx = 800
private const val HighBitmapWidthPx = 1024

private const val LowBitmapCompressQuality = 50
private const val MediumBitmapCompressQuality = 85
private const val HighBitmapCompressQuality = 95