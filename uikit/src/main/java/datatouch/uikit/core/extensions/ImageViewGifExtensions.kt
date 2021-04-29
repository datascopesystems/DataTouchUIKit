package datatouch.uikit.core.extensions

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.load.resource.gif.GifDrawable.LOOP_FOREVER
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

object ImageViewGifExtensions {

    fun ImageView.showGifAnimation(@DrawableRes resId: Int, loopsCount: Int = LOOP_FOREVER) {
        Glide.with(context.applicationContext)
            .asGif()
            .load(resId)
            .addListener(object : RequestListener<GifDrawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return true
                }

                override fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    resource?.setLoopCount(loopsCount)
                    return false
                }

            })
            .into(this)
    }

    fun ImageView.startGifAnimationFromFirstFrame(loopsCount: Int = LOOP_FOREVER) {
        kotlin.runCatching {
            val gifDrawable = (drawable as? GifDrawable?)
            gifDrawable?.setLoopCount(loopsCount)
            gifDrawable?.startFromFirstFrame()
        }
    }

    fun ImageView.startGifAnimation(loopsCount: Int = LOOP_FOREVER) {
        kotlin.runCatching {
            val gifDrawable = (drawable as? GifDrawable?)
            gifDrawable?.setLoopCount(loopsCount)
            gifDrawable?.start()
        }
    }

    fun ImageView.stopGifAnimation() {
        kotlin.runCatching { (drawable as? GifDrawable?)?.stop() }
    }

}