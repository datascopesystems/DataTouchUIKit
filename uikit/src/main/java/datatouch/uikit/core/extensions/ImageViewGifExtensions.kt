package datatouch.uikit.core.extensions

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.load.resource.gif.GifDrawable.LOOP_FOREVER
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import datatouch.uikit.core.callbacks.UiJustCallback

object ImageViewGifExtensions {

    fun ImageView.showGifAnimation(@DrawableRes resId: Int,
                                   loopsCount: Int = LOOP_FOREVER,
                                   animationEndCallback : UiJustCallback = {}) {
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
                    resource?.registerAnimationCallback(object :
                        Animatable2Compat.AnimationCallback() {
                        override fun onAnimationEnd(drawable: Drawable?) {
                            super.onAnimationEnd(drawable)
                            animationEndCallback()
                        }
                    })

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