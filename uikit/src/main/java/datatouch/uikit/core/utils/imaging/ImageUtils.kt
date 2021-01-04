package datatouch.uikit.core.utils.imaging

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import datatouch.uikit.R
import datatouch.uikit.core.extensions.ContextExtensions.toActivity
import datatouch.uikit.core.utils.Conditions
import java.io.File
import java.io.IOException

object ImageUtils {

    fun <T : ImageView> loadImageFile(context: Context?, imageFile: File?,
                                      imageView: T, defaultDrawableId: Int) = context?.apply {
        if (context !is Activity || !context.isFinishing) {
            Glide.with(context)
                .load(imageFile)
                .apply(RequestOptions()
                    .placeholder(R.drawable.progress_bar_animated)
                    .error(defaultDrawableId))
                .into(imageView)
        }
    }

    fun <T : ImageView> loadImageFileRoundedCorners(context: Context?,
                                                    imageFile: File?,
                                                    imageView: T,
                                                    defaultDrawableId: Int) = context?.apply {
        if (context !is Activity || !context.isFinishing) {

            val roundedCornerRadiusPx =
                context.resources.getDimensionPixelSize(R.dimen.rounded_corner_radius)

            Glide.with(context)
                .load(imageFile)
                .transform(RoundedCorners(roundedCornerRadiusPx))
                .apply(RequestOptions()
                    .placeholder(R.drawable.progress_bar_animated)
                    .error(defaultDrawableId))
                .into(imageView)
        }
    }

    fun <T : ImageView> loadImageFromByteArray(context: Context?, imageUri: String,
                                               imageView: T, defaultDrawableId: Int,
                                               noCache: Boolean = true) {
        setImageByUriOptimized(context.toActivity(), imageUri, imageView,
            ContextCompat.getDrawable(context!!, defaultDrawableId), noCache)
    }

    @JvmStatic
    fun <T : ImageView> loadImageFile(context: Context?, imageUri: String?,
                                      imageView: T, defaultDrawableId: Int) {
        setImageByUriOptimized(context.toActivity(), imageUri, imageView,
            ContextCompat.getDrawable(context!!, defaultDrawableId), false)
    }

    private fun <T : ImageView> setImageByUriOptimized(activityContext: Activity?,
                                                       imageUri: String?,
                                                       imageView: T,
                                                       errorImageDrawable: Drawable?,
                                                       noCache: Boolean) {
        if (activityContext == null) return

        var options = RequestOptions
            .noTransformation()
            .error(errorImageDrawable)
            .placeholder(R.drawable.progress_bar_animated)
        if (noCache) {
            options = options.diskCacheStrategy(DiskCacheStrategy.NONE)
        }
        if (imageUri?.isNotEmpty() == true) {
            val file = File(imageUri.orEmpty())
            if (URLUtil.isFileUrl(imageUri)) {
                val bitmap = getBitmapFromUri(imageUri.orEmpty())
                if (Conditions.isNotNull(bitmap)) {
                    imageView.setImageBitmap(bitmap)
                }
            } else if (file.exists()) {
                Glide.with(activityContext)
                    .load(file)
                    .apply(options)
                    .into(imageView)
            } else if (URLUtil.isHttpUrl(imageUri) || URLUtil.isHttpsUrl(imageUri)) {
                if (!activityContext.isFinishing) {
                    Glide.with(activityContext)
                        .load(imageUri)
                        .apply(options)
                        .into(imageView)
                }
            } else if (URLUtil.isAssetUrl(imageUri)) {
                var drawable: Drawable? = null
                try {
                    drawable = Drawable.createFromStream(
                        imageView.context.assets.open(File(imageUri.orEmpty()).name), null)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                imageView.setImageDrawable(drawable)
            }
        }
    }

    private fun getBitmapFromUri(imageUri: String): Bitmap? {
        var imageBitmap: Bitmap? = null
        if (URLUtil.isFileUrl(imageUri)) {
            imageBitmap = BitmapUtils.readBitmapFromFile(imageUri)
        }

        return imageBitmap
    }

    fun <T : ImageView> setImageResource(resId: Int, imageView: T) {
        Glide.with(imageView.context.applicationContext)
            .load(resId)
            .into(imageView)
    }
}