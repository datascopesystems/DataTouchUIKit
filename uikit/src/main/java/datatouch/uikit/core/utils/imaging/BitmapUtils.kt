package datatouch.uikit.core.utils.imaging

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object BitmapUtils {

    fun convertBitmapToStringNoWrap(source: Bitmap): String {
        return Base64.encodeToString(convertBitmapToByteArray(source), Base64.NO_WRAP)
    }

    private fun convertBitmapToByteArray(source: Bitmap): ByteArray? {
        return compressBitmap(source)?.toByteArray()
    }

    private fun compressBitmap(source: Bitmap): ByteArrayOutputStream? {
        val out = ByteArrayOutputStream()
        source.compress(Bitmap.CompressFormat.PNG, 100, out)
        return out
    }

    fun saveBitmapToFile(bitmap: Bitmap, writePath: String?): String? {
        val fos: FileOutputStream
        return try {
            val file = File(writePath.orEmpty())
            file.parentFile?.mkdirs()
            fos = FileOutputStream(writePath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
            writePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}