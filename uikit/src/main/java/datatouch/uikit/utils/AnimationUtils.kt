package datatouch.uikit.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.TextView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import datatouch.uikit.interfaces.AnimationListenerWrapper

object AnimationUtils {
    private const val NORMAL = 500L
    private const val FAST = 300L

    @JvmStatic
    fun animate(delayInMillis: Int, target: View?, animationTechniques: AnimationTechniques) {
        if (target == null) return
        YoYo.with(getActualTechniques(animationTechniques)).duration(NORMAL)
            .delay(delayInMillis.toLong()).playOn(target)
    }

    @JvmStatic
    fun animate(target: View?, animationTechniques: AnimationTechniques) {
        if (target == null) return
        YoYo.with(getActualTechniques(animationTechniques)).duration(NORMAL).playOn(target)
    }

    @JvmStatic
    fun animate(
        target: View?,
        animationTechniques: AnimationTechniques,
        listener: AnimationListenerWrapper
    ) {
        if (target == null) return
        YoYo.with(getActualTechniques(animationTechniques))
            .duration(NORMAL)
            .withListener(listener)
            .playOn(target)
    }

    @JvmStatic
    fun animate(
        target: View?,
        animationTechniques: AnimationTechniques,
        duration: Long,
        listener: AnimationListenerWrapper
    ) {
        if (target == null) return
        YoYo.with(getActualTechniques(animationTechniques))
            .duration(duration)
            .withListener(listener)
            .playOn(target)
    }

    @JvmStatic
    fun animateFast(target: View?, animationTechniques: AnimationTechniques) {
        if (target == null) return
        YoYo.with(getActualTechniques(animationTechniques)).duration(FAST).playOn(target)
    }

    @JvmStatic
    fun animate(target: View?, animationTechniques: AnimationTechniques, duration: Int) {
        if (target == null) return
        YoYo.with(getActualTechniques(animationTechniques)).duration(duration.toLong())
            .playOn(target)
    }

    private fun getActualTechniques(animationTechniques: AnimationTechniques): Techniques {
        return when (animationTechniques) {
            AnimationTechniques.FADE_OUT -> Techniques.FadeOut
            AnimationTechniques.PULSE -> Techniques.Pulse
            AnimationTechniques.DROP_OUT -> Techniques.DropOut
            AnimationTechniques.LANDING -> Techniques.Landing
            AnimationTechniques.TAKING_OFF -> Techniques.TakingOff
            AnimationTechniques.FLASH -> Techniques.Flash
            AnimationTechniques.RUBBER_BAND -> Techniques.RubberBand
            AnimationTechniques.SHAKE -> Techniques.Shake
            AnimationTechniques.SWING -> Techniques.Swing
            AnimationTechniques.WOBBLE -> Techniques.Wobble
            AnimationTechniques.BOUNCE -> Techniques.Bounce
            AnimationTechniques.TADA -> Techniques.Tada
            AnimationTechniques.STAND_UP -> Techniques.StandUp
            AnimationTechniques.WAVE -> Techniques.Wave
            AnimationTechniques.HINGE -> Techniques.Hinge
            AnimationTechniques.ROLL_IN -> Techniques.RollIn
            AnimationTechniques.ROLL_OUT -> Techniques.RollOut
            AnimationTechniques.BOUNCE_IN -> Techniques.BounceIn
            AnimationTechniques.BOUNCE_IN_DOWN -> Techniques.BounceInDown
            AnimationTechniques.BOUNCE_IN_LEFT -> Techniques.BounceInLeft
            AnimationTechniques.BOUNCE_IN_RIGHT -> Techniques.BounceInRight
            AnimationTechniques.BOUNCE_IN_UP -> Techniques.BounceInUp
            AnimationTechniques.FADE_IN_UP -> Techniques.FadeInUp
            AnimationTechniques.FADE_IN_DOWN -> Techniques.FadeInDown
            AnimationTechniques.FADE_IN_LEFT -> Techniques.FadeInLeft
            AnimationTechniques.FADE_IN_RIGHT -> Techniques.FadeInRight
            AnimationTechniques.FADE_OUT_UP -> Techniques.FadeOutRight
            AnimationTechniques.FADE_OUT_DOWN -> Techniques.FadeOutDown
            AnimationTechniques.FADE_OUT_LEFT -> Techniques.FadeOutLeft
            AnimationTechniques.FADE_OUT_RIGHT -> Techniques.FadeOutRight
            AnimationTechniques.FLIP_IN_X -> Techniques.FlipInX
            AnimationTechniques.FLIP_OUT_X -> Techniques.FlipOutX
            AnimationTechniques.FLIP_OUT_Y -> Techniques.FlipOutY
            AnimationTechniques.ROTATE_IN -> Techniques.RotateIn
            AnimationTechniques.ROTATE_IN_DOWN_LEFT -> Techniques.RotateInDownLeft
            AnimationTechniques.ROTATE_IN_DOWN_RIGHT -> Techniques.RotateInDownRight
            AnimationTechniques.SLIDE_IN_LEFT -> Techniques.SlideInLeft
            AnimationTechniques.SLIDE_IN_RIGHT -> Techniques.SlideInRight
            AnimationTechniques.SLIDE_IN_UP -> Techniques.SlideInUp
            AnimationTechniques.SLIDE_IN_DOWN -> Techniques.SlideInDown
            AnimationTechniques.SLIDE_OUT_LEFT -> Techniques.SlideOutLeft
            AnimationTechniques.SLIDE_OUT_RIGHT -> Techniques.SlideOutRight
            AnimationTechniques.SLIDE_OUT_UP -> Techniques.SlideOutUp
            AnimationTechniques.SLIDE_OUT_DOWN -> Techniques.SlideOutDown
            AnimationTechniques.ROTATE_IN_UP_LEFT -> Techniques.RotateInUpLeft
            AnimationTechniques.ROTATE_IN_UP_RIGHT -> Techniques.RotateInUpRight
            AnimationTechniques.ROTATE_OUT -> Techniques.RotateOut
            AnimationTechniques.ROTATE_OUT_DOWN_LEFT -> Techniques.RotateOutDownLeft
            AnimationTechniques.ROTATE_OUT_DOWN_RIGHT -> Techniques.RotateOutDownRight
            AnimationTechniques.ROTATE_OUT_UP_LEFT -> Techniques.RotateOutUpLeft
            AnimationTechniques.ROTATE_OUT_UP_RIGHT -> Techniques.RotateOutUpRight
            AnimationTechniques.ZOOM_IN -> Techniques.ZoomIn
            AnimationTechniques.ZOOM_IN_DOWN -> Techniques.ZoomInDown
            AnimationTechniques.ZOOM_IN_LEFT -> Techniques.ZoomInLeft
            AnimationTechniques.ZOOM_IN_RIGHT -> Techniques.ZoomInRight
            AnimationTechniques.ZOOM_IN_UP -> Techniques.ZoomInUp
            AnimationTechniques.ZOOM_OUT -> Techniques.ZoomOut
            AnimationTechniques.ZOOM_OUT_DOWN -> Techniques.ZoomOut
            AnimationTechniques.ZOOM_OUT_LEFT -> Techniques.ZoomOutLeft
            AnimationTechniques.ZOOM_OUT_RIGHT -> Techniques.ZoomOutRight
            AnimationTechniques.ZOOM_OUT_UP -> Techniques.ZoomOutUp
            else -> Techniques.FadeIn
        }
    }

    @JvmOverloads
    @JvmStatic
    fun animateTextView(
        initialValue: Int,
        finalValue: Int,
        textview: TextView?,
        animationDuration: Int = 1500
    ) {
        if (null == textview) return
        val valueAnimator = ValueAnimator.ofInt(initialValue, finalValue)
        valueAnimator.duration = animationDuration.toLong()
        valueAnimator.addUpdateListener { valueAnimator1: ValueAnimator ->
            textview.text = valueAnimator1.animatedValue.toString()
        }
        valueAnimator.start()
    }

    @JvmStatic
    fun animateSlideUp(view: View?, deltaY: Float) {
        if (view == null) return
        ObjectAnimator.ofFloat(view, "TranslationY", deltaY, 0f).start()
    }

    @JvmStatic
    fun animateSlideDown(view: View?, deltaY: Float) {
        if (view == null) return
        ObjectAnimator.ofFloat(view, "TranslationY", 0f, deltaY).start()
    }

    @JvmStatic
    fun animateCollapse(view: View?) {
        if (view == null) return

        val initialWidth = view.measuredWidth
        val a: Animation = object : Animation() {
            override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation
            ) {
                if (interpolatedTime == 1f) {
                    view.visibility = View.GONE
                } else {
                    view.layoutParams.width =
                        initialWidth - (initialWidth * interpolatedTime).toInt()
                    view.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        a.duration = NORMAL
        view.startAnimation(a)
    }

    @JvmStatic
    fun animateExpand(view: View?) {
        if (view == null) return

        val wrapContentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(wrapContentMeasureSpec, wrapContentMeasureSpec)
        val targetWidth = view.measuredWidth

        // Older versions of android (pre API 21) cancel animations for views with a width of 0.
        view.layoutParams.width = 1

        view.visibility = View.VISIBLE
        val a: Animation = object : Animation() {
            override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation?
            ) {
                view.layoutParams.width =
                    if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetWidth * interpolatedTime).toInt()
                view.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        a.duration = NORMAL
        view.startAnimation(a)
    }

    enum class AnimationTechniques {
        FADE_IN,
        FADE_OUT,
        PULSE,
        DROP_OUT,
        LANDING,
        TAKING_OFF,
        FLASH,
        RUBBER_BAND,
        SHAKE,
        SWING,
        WOBBLE,
        BOUNCE,
        TADA,
        STAND_UP,
        WAVE,
        HINGE,
        ROLL_IN,
        ROLL_OUT,
        BOUNCE_IN,
        BOUNCE_IN_DOWN,
        BOUNCE_IN_LEFT,
        BOUNCE_IN_RIGHT,
        BOUNCE_IN_UP,
        FADE_IN_UP,
        FADE_IN_DOWN,
        FADE_IN_LEFT,
        FADE_IN_RIGHT,
        FADE_OUT_UP,
        FADE_OUT_DOWN,
        FADE_OUT_LEFT,
        FADE_OUT_RIGHT,
        FLIP_IN_X,
        FLIP_OUT_X,
        FLIP_OUT_Y,
        ROTATE_IN,
        ROTATE_IN_DOWN_LEFT,
        ROTATE_IN_DOWN_RIGHT,
        SLIDE_IN_LEFT,
        SLIDE_IN_RIGHT,
        SLIDE_IN_UP,
        SLIDE_IN_DOWN,
        SLIDE_OUT_LEFT,
        SLIDE_OUT_RIGHT,
        SLIDE_OUT_UP,
        SLIDE_OUT_DOWN,
        ROTATE_IN_UP_LEFT,
        ROTATE_IN_UP_RIGHT,
        ROTATE_OUT,
        ROTATE_OUT_DOWN_LEFT,
        ROTATE_OUT_DOWN_RIGHT,
        ROTATE_OUT_UP_LEFT,
        ROTATE_OUT_UP_RIGHT,
        ZOOM_IN,
        ZOOM_IN_DOWN,
        ZOOM_IN_LEFT,
        ZOOM_IN_RIGHT,
        ZOOM_IN_UP,
        ZOOM_OUT,
        ZOOM_OUT_DOWN,
        ZOOM_OUT_LEFT,
        ZOOM_OUT_RIGHT,
        ZOOM_OUT_UP
    }
}