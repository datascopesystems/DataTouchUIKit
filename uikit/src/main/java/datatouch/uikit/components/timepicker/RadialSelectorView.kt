/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package datatouch.uikit.components.timepicker

import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import android.view.View
import datatouch.uikit.R
import java.lang.ref.WeakReference

/**
 * View to show what number is selected. This will draw a blue circle over the number, with a blue
 * line coming from the center of the main circle to the edge of the blue selection.
 */
class RadialSelectorView(context: Context?) : View(context) {
    private val mPaint = Paint()
    private var mIsInitialized = false
    private var mDrawValuesReady = false
    private var mCircleRadiusMultiplier = 0f
    private var mAmPmCircleRadiusMultiplier = 0f
    private var mInnerNumbersRadiusMultiplier = 0f
    private var mOuterNumbersRadiusMultiplier = 0f
    private var mNumbersRadiusMultiplier = 0f
    private var mSelectionRadiusMultiplier = 0f
    private var mAnimationRadiusMultiplier = 0f
    private var mIs24HourMode = false
    private var mHasInnerCircle = false
    private var mSelectionAlpha = 0
    private var mXCenter = 0
    private var mYCenter = 0
    private var mCircleRadius = 0
    private var mTransitionMidRadiusMultiplier = 0f
    private var mTransitionEndRadiusMultiplier = 0f
    private var mLineLength = 0
    private var mSelectionRadius = 0
    private var mInvalidateUpdateListener: InvalidateUpdateListener? = null
    private var mSelectionDegrees = 0
    private var mSelectionRadians = 0.0
    private var mForceDrawDot = false

    /**
     * Initialize this selector with the state of the picker.
     *
     * @param context          Current context.
     * @param controller       Structure containing the accentColor and the 24-hour mode, which will tell us
     * whether the circle's center is moved up slightly to make room for the AM/PM circles.
     * @param hasInnerCircle   Whether we have both an inner and an outer circle of numbers
     * that may be selected. Should be true for 24-hour mode in the hours circle.
     * @param disappearsOut    Whether the numbers' animation will have them disappearing out
     * or disappearing in.
     * @param selectionDegrees The initial degrees to be selected.
     * @param isInnerCircle    Whether the initial selection is in the inner or outer circle.
     * Will be ignored when hasInnerCircle is false.
     */
    fun initialize(
        context: Context, controller: TimePickerController, hasInnerCircle: Boolean,
        disappearsOut: Boolean, selectionDegrees: Int, isInnerCircle: Boolean
    ) {
        if (mIsInitialized) {
            Log.e(RadialSelectorView::javaClass.name, "This RadialSelectorView may only be initialized once.")
            return
        }
        val res = context.resources
        val accentColor = controller.accentColor
        mPaint.color = accentColor
        mPaint.isAntiAlias = true
        mSelectionAlpha = if (controller.isThemeDark) SELECTED_ALPHA_THEME_DARK else SELECTED_ALPHA

        // Calculate values for the circle radius size.
        mIs24HourMode = controller.is24HourMode()
        if (mIs24HourMode || controller.version != TimePickerDialog.Version.VERSION_1) {
            mCircleRadiusMultiplier =
                res.getString(R.string.mdtp_circle_radius_multiplier_24HourMode).toFloat()
        } else {
            mCircleRadiusMultiplier =
                res.getString(R.string.mdtp_circle_radius_multiplier).toFloat()
            mAmPmCircleRadiusMultiplier =
                res.getString(R.string.mdtp_ampm_circle_radius_multiplier).toFloat()
        }

        // Calculate values for the radius size(s) of the numbers circle(s).
        mHasInnerCircle = hasInnerCircle
        if (hasInnerCircle) {
            mInnerNumbersRadiusMultiplier =
                res.getString(R.string.mdtp_numbers_radius_multiplier_inner).toFloat()
            mOuterNumbersRadiusMultiplier =
                res.getString(R.string.mdtp_numbers_radius_multiplier_outer).toFloat()
        } else {
            mNumbersRadiusMultiplier =
                res.getString(R.string.mdtp_numbers_radius_multiplier_normal).toFloat()
        }
        mSelectionRadiusMultiplier =
            res.getString(R.string.mdtp_selection_radius_multiplier).toFloat()

        // Calculate values for the transition mid-way states.
        mAnimationRadiusMultiplier = 1f
        mTransitionMidRadiusMultiplier = 1f + 0.05f * if (disappearsOut) -1 else 1
        mTransitionEndRadiusMultiplier = 1f + 0.3f * if (disappearsOut) 1 else -1
        mInvalidateUpdateListener = InvalidateUpdateListener(this)
        setSelection(selectionDegrees, isInnerCircle, false)
        mIsInitialized = true
    }

    /**
     * Set the selection.
     *
     * @param selectionDegrees The degrees to be selected.
     * @param isInnerCircle    Whether the selection should be in the inner circle or outer. Will be
     * ignored if hasInnerCircle was initialized to false.
     * @param forceDrawDot     Whether to force the dot in the center of the selection circle to be
     * drawn. If false, the dot will be drawn only when the degrees is not a multiple of 30, i.e.
     * the selection is not on a visible number.
     */
    fun setSelection(selectionDegrees: Int, isInnerCircle: Boolean, forceDrawDot: Boolean) {
        mSelectionDegrees = selectionDegrees
        mSelectionRadians = selectionDegrees * Math.PI / 180
        mForceDrawDot = forceDrawDot
        if (mHasInnerCircle) {
            mNumbersRadiusMultiplier = if (isInnerCircle) {
                mInnerNumbersRadiusMultiplier
            } else {
                mOuterNumbersRadiusMultiplier
            }
        }
    }

    /**
     * Allows for smoother animations.
     */
    override fun hasOverlappingRendering(): Boolean {
        return false
    }

    /**
     * Set the multiplier for the radius. Will be used during animations to move in/out.
     */
    fun setAnimationRadiusMultiplier(animationRadiusMultiplier: Float) {
        mAnimationRadiusMultiplier = animationRadiusMultiplier
    }

    fun getDegreesFromCoords(
        pointX: Float, pointY: Float, forceLegal: Boolean,
        isInnerCircle: Array<Boolean?>
    ): Int {
        if (!mDrawValuesReady) {
            return -1
        }
        val hypotenuse = Math.sqrt(
            (pointY - mYCenter) * (pointY - mYCenter) +
                    (pointX - mXCenter) * (pointX - mXCenter).toDouble()
        )
        // Check if we're outside the range
        if (mHasInnerCircle) {
            if (forceLegal) {
                // If we're told to force the coordinates to be legal, we'll set the isInnerCircle
                // boolean based based off whichever number the coordinates are closer to.
                val innerNumberRadius = (mCircleRadius * mInnerNumbersRadiusMultiplier).toInt()
                val distanceToInnerNumber = Math.abs(hypotenuse - innerNumberRadius).toInt()
                val outerNumberRadius = (mCircleRadius * mOuterNumbersRadiusMultiplier).toInt()
                val distanceToOuterNumber = Math.abs(hypotenuse - outerNumberRadius).toInt()
                isInnerCircle[0] = distanceToInnerNumber <= distanceToOuterNumber
            } else {
                // Otherwise, if we're close enough to either number (with the space between the
                // two allotted equally), set the isInnerCircle boolean as the closer one.
                // appropriately, but otherwise return -1.
                val minAllowedHypotenuseForInnerNumber =
                    (mCircleRadius * mInnerNumbersRadiusMultiplier).toInt() - mSelectionRadius
                val maxAllowedHypotenuseForOuterNumber =
                    (mCircleRadius * mOuterNumbersRadiusMultiplier).toInt() + mSelectionRadius
                val halfwayHypotenusePoint = (mCircleRadius *
                        ((mOuterNumbersRadiusMultiplier + mInnerNumbersRadiusMultiplier) / 2)).toInt()
                if (hypotenuse >= minAllowedHypotenuseForInnerNumber &&
                    hypotenuse <= halfwayHypotenusePoint
                ) {
                    isInnerCircle[0] = true
                } else if (hypotenuse <= maxAllowedHypotenuseForOuterNumber &&
                    hypotenuse >= halfwayHypotenusePoint
                ) {
                    isInnerCircle[0] = false
                } else {
                    return -1
                }
            }
        } else {
            // If there's just one circle, we'll need to return -1 if:
            // we're not told to force the coordinates to be legal, and
            // the coordinates' distance to the number is within the allowed distance.
            if (!forceLegal) {
                val distanceToNumber = Math.abs(hypotenuse - mLineLength).toInt()
                // The max allowed distance will be defined as the distance from the center of the
                // number to the edge of the circle.
                val maxAllowedDistance = (mCircleRadius * (1 - mNumbersRadiusMultiplier)).toInt()
                if (distanceToNumber > maxAllowedDistance) {
                    return -1
                }
            }
        }
        val opposite = Math.abs(pointY - mYCenter)
        val radians = Math.asin(opposite / hypotenuse)
        var degrees = (radians * 180 / Math.PI).toInt()

        // Now we have to translate to the correct quadrant.
        val rightSide = pointX > mXCenter
        val topSide = pointY < mYCenter
        if (rightSide && topSide) {
            degrees = 90 - degrees
        } else if (rightSide && !topSide) {
            degrees = 90 + degrees
        } else if (!rightSide && !topSide) {
            degrees = 270 - degrees
        } else if (!rightSide && topSide) {
            degrees = 270 + degrees
        }
        return degrees
    }

    public override fun onDraw(canvas: Canvas) {
        val viewWidth = width
        if (viewWidth == 0 || !mIsInitialized) {
            return
        }
        if (!mDrawValuesReady) {
            mXCenter = width / 2
            mYCenter = height / 2
            mCircleRadius = (Math.min(mXCenter, mYCenter) * mCircleRadiusMultiplier).toInt()
            if (!mIs24HourMode) {
                // We'll need to draw the AM/PM circles, so the main circle will need to have
                // a slightly higher center. To keep the entire view centered vertically, we'll
                // have to push it up by half the radius of the AM/PM circles.
                val amPmCircleRadius = (mCircleRadius * mAmPmCircleRadiusMultiplier).toInt()
                mYCenter -= amPmCircleRadius * 0.75.toInt()
            }
            mSelectionRadius = (mCircleRadius * mSelectionRadiusMultiplier).toInt()
            mDrawValuesReady = true
        }

        // Calculate the current radius at which to place the selection circle.
        mLineLength =
            (mCircleRadius * mNumbersRadiusMultiplier * mAnimationRadiusMultiplier).toInt()
        var pointX = mXCenter + (mLineLength * Math.sin(mSelectionRadians)).toInt()
        var pointY = mYCenter - (mLineLength * Math.cos(mSelectionRadians)).toInt()

        // Draw the selection circle.
        mPaint.alpha = mSelectionAlpha
        canvas.drawCircle(pointX.toFloat(), pointY.toFloat(), mSelectionRadius.toFloat(), mPaint)
        if (mForceDrawDot or (mSelectionDegrees % 30 != 0)) {
            // We're not on a direct tick (or we've been told to draw the dot anyway).
            mPaint.alpha = FULL_ALPHA
            canvas.drawCircle(
                pointX.toFloat(),
                pointY.toFloat(),
                (mSelectionRadius * 2 / 7).toFloat(),
                mPaint
            )
        } else {
            // We're not drawing the dot, so shorten the line to only go as far as the edge of the
            // selection circle.
            var lineLength = mLineLength
            lineLength -= mSelectionRadius
            pointX = mXCenter + (lineLength * Math.sin(mSelectionRadians)).toInt()
            pointY = mYCenter - (lineLength * Math.cos(mSelectionRadians)).toInt()
        }

        // Draw the line from the center of the circle.
        mPaint.alpha = 255
        mPaint.strokeWidth = 3f
        canvas.drawLine(
            mXCenter.toFloat(),
            mYCenter.toFloat(),
            pointX.toFloat(),
            pointY.toFloat(),
            mPaint
        )
    }

    val disappearAnimator: ObjectAnimator?
        get() {
            if (!mIsInitialized || !mDrawValuesReady) {
                Log.e(RadialSelectorView::javaClass.name, "RadialSelectorView was not ready for animation.")
                return null
            }
            var kf0: Keyframe?
            var kf1: Keyframe?
            val kf2: Keyframe
            val midwayPoint = 0.2f
            val duration = 500
            kf0 = Keyframe.ofFloat(0f, 1f)
            kf1 = Keyframe.ofFloat(midwayPoint, mTransitionMidRadiusMultiplier)
            kf2 = Keyframe.ofFloat(1f, mTransitionEndRadiusMultiplier)
            val radiusDisappear = PropertyValuesHolder.ofKeyframe(
                "animationRadiusMultiplier", kf0, kf1, kf2
            )
            kf0 = Keyframe.ofFloat(0f, 1f)
            kf1 = Keyframe.ofFloat(1f, 0f)
            val fadeOut = PropertyValuesHolder.ofKeyframe("alpha", kf0, kf1)
            val disappearAnimator = ObjectAnimator.ofPropertyValuesHolder(
                this, radiusDisappear, fadeOut
            ).setDuration(duration.toLong())
            disappearAnimator.addUpdateListener(mInvalidateUpdateListener)
            return disappearAnimator
        }

    // The time points are half of what they would normally be, because this animation is
    // staggered against the disappear so they happen seamlessly. The reappear starts
    // halfway into the disappear.
    val reappearAnimator: ObjectAnimator?
        get() {
            if (!mIsInitialized || !mDrawValuesReady) {
                Log.e(RadialSelectorView::javaClass.name, "RadialSelectorView was not ready for animation.")
                return null
            }
            var kf0: Keyframe?
            var kf1: Keyframe?
            var kf2: Keyframe?
            val kf3: Keyframe
            var midwayPoint = 0.2f
            val duration = 500

            // The time points are half of what they would normally be, because this animation is
            // staggered against the disappear so they happen seamlessly. The reappear starts
            // halfway into the disappear.
            val delayMultiplier = 0.25f
            val transitionDurationMultiplier = 1f
            val totalDurationMultiplier = transitionDurationMultiplier + delayMultiplier
            val totalDuration = (duration * totalDurationMultiplier).toInt()
            val delayPoint = delayMultiplier * duration / totalDuration
            midwayPoint = 1 - midwayPoint * (1 - delayPoint)
            kf0 = Keyframe.ofFloat(0f, mTransitionEndRadiusMultiplier)
            kf1 = Keyframe.ofFloat(delayPoint, mTransitionEndRadiusMultiplier)
            kf2 = Keyframe.ofFloat(midwayPoint, mTransitionMidRadiusMultiplier)
            kf3 = Keyframe.ofFloat(1f, 1f)
            val radiusReappear = PropertyValuesHolder.ofKeyframe(
                "animationRadiusMultiplier", kf0, kf1, kf2, kf3
            )
            kf0 = Keyframe.ofFloat(0f, 0f)
            kf1 = Keyframe.ofFloat(delayPoint, 0f)
            kf2 = Keyframe.ofFloat(1f, 1f)
            val fadeIn = PropertyValuesHolder.ofKeyframe("alpha", kf0, kf1, kf2)
            val reappearAnimator = ObjectAnimator.ofPropertyValuesHolder(
                this, radiusReappear, fadeIn
            ).setDuration(totalDuration.toLong())
            reappearAnimator.addUpdateListener(mInvalidateUpdateListener)
            return reappearAnimator
        }

    /**
     * We'll need to invalidate during the animation.
     */
    private class InvalidateUpdateListener internal constructor(selectorView: RadialSelectorView) :
        ValueAnimator.AnimatorUpdateListener {
        private val selectorRef: WeakReference<RadialSelectorView>
        override fun onAnimationUpdate(animation: ValueAnimator) {
            val selectorView = selectorRef.get()
            selectorView?.invalidate()
        }

        init {
            selectorRef = WeakReference(selectorView)
        }
    }

}

private const val SELECTED_ALPHA = 255
private const val SELECTED_ALPHA_THEME_DARK = 255
private const val FULL_ALPHA = 255