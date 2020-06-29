package datatouch.uikit.components.floatingrevealmenu.helper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import datatouch.uikit.components.floatingrevealmenu.enums.Direction
import datatouch.uikit.components.floatingrevealmenu.listeners.AnimationListener
import io.codetail.animation.arcanimator.ArcAnimator
import io.codetail.animation.arcanimator.Side

class AnimationHelper(private val viewHelper: ViewHelper) {
    private val FAB_ARC_DEGREES = -30
    private val FAB_SCALE_FACTOR = 0.2f
    private val OVERLAY_ANIM_DURATION = REVEAL_DURATION / 2

    //View specific fields
    private var anchorX = 0
    private var anchorY = 0
    private var centerX = 0
    private var centerY = 0
    private val fabAnimInterpolator: Interpolator
    private val revealAnimInterpolator: Interpolator
    fun moveFab(
        fabView: View,
        revealView: View,
        direction: Direction,
        isReturning: Boolean,
        listener: AnimationListener?
    ) {
        val scaleFactor = if (isReturning) -FAB_SCALE_FACTOR else FAB_SCALE_FACTOR
        val alpha = if (isReturning) 1.0f else 1.0f
        val endX: Float
        val endY: Float
        if (!isReturning) {
            val anchorPoint = viewHelper.updateFabAnchor(fabView)
            anchorX = anchorPoint.x
            anchorY = anchorPoint.y
            endX = revealView.x + centerX
            endY = revealView.y + centerY
        } else {
            endX = anchorX.toFloat()
            endY = anchorY.toFloat()
        }
        //move by arc animation
        startArcAnim(
            fabView,
            endX,
            endY,
            FAB_ARC_DEGREES.toFloat(),
            getArcSide(direction),
            FAB_ANIM_DURATION.toLong(),
            fabAnimInterpolator,
            listener
        )
        //scale fab
        fabView.animate().scaleXBy(scaleFactor).scaleYBy(scaleFactor).alpha(alpha)
            .setDuration(FAB_ANIM_DURATION.toLong()).start()
    }

    fun revealMenu(
        view: View,
        startRadius: Float,
        endRadius: Float,
        isReturning: Boolean,
        listener: AnimationListener?
    ) {
        //start circular reveal animation
        startCircularRevealAnim(
            view,
            centerX,
            centerY,
            startRadius,
            endRadius,
            REVEAL_DURATION.toLong(),
            revealAnimInterpolator,
            listener
        )
    }

    private fun startCircularRevealAnim(
        view: View,
        centerX: Int,
        centerY: Int,
        startRadius: Float,
        endRadius: Float,
        duration: Long,
        interpolator: Interpolator,
        listener: AnimationListener?
    ) {
        // Native circular reveal uses coordinates relative to the view
        val relativeCenterX = (centerX - view.x).toInt()
        val relativeCenterY = (centerY - view.y).toInt()
        // Setup animation
        var anim: Animator? = null
        anim = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewAnimationUtils.createCircularReveal(
                view, relativeCenterX,
                relativeCenterY, startRadius, endRadius
            )
        } else {
            ValueAnimator.ofFloat(startRadius, endRadius)
        }
        anim!!.duration = duration
        anim.interpolator = interpolator
        // Add listener
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                listener?.onStart()
            }

            override fun onAnimationEnd(animation: Animator) {
                listener?.onEnd()
            }
        })
        // Start animation
        anim.start()
    }

    private fun startArcAnim(
        view: View,
        endX: Float,
        endY: Float,
        degrees: Float,
        side: Side,
        duration: Long,
        interpolator: Interpolator,
        listener: AnimationListener?
    ) {
        // Setup animation
        // Cast end coordinates to ints so that the FAB will be animated to the same position even
        // when there are minute differences in the coordinates
        val anim: ArcAnimator = ArcAnimator.createArcAnimator(
            view, endX, endY, degrees,
            side
        )
        anim.duration = duration
        anim.setInterpolator(interpolator)
        // Add listener
        anim.addListener(object : com.nineoldandroids.animation.AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: com.nineoldandroids.animation.Animator) {
                listener?.onStart()
            }

            override fun onAnimationEnd(animation: com.nineoldandroids.animation.Animator) {
                listener?.onEnd()
            }
        })
        // Start animation
        anim.start()
    }

    fun showOverlay(mOverlayLayout: View) {
        mOverlayLayout.animate().alpha(1f).setDuration(OVERLAY_ANIM_DURATION.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    mOverlayLayout.visibility = View.VISIBLE
                }
            })
    }

    fun hideOverlay(mOverlayLayout: View) {
        mOverlayLayout.animate().alpha(0f).setDuration(OVERLAY_ANIM_DURATION * 2.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    mOverlayLayout.visibility = View.GONE
                }
            })
    }

    fun calculateCenterPoints(
        viewToReveal: View?,
        mDirection: Direction?
    ) {
        centerX = viewHelper.getSheetRevealCenterX(viewToReveal!!, mDirection!!)
        centerY = viewHelper.getSheetRevealCenterY(viewToReveal, mDirection)
    }

    private fun getArcSide(mDirection: Direction): Side {
        return if (mDirection === Direction.LEFT || mDirection === Direction.UP) Side.RIGHT else Side.LEFT
    }

    companion object {
        // Animation durations
        const val REVEAL_DURATION = 600
        const val FAB_ANIM_DURATION = (REVEAL_DURATION / 2.4).toInt()
    }

    init {
        fabAnimInterpolator = AccelerateDecelerateInterpolator()
        revealAnimInterpolator = FastOutLinearInInterpolator()
    }
}