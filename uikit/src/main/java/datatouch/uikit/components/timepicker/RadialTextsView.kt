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
import android.graphics.Paint.Align
import android.graphics.Typeface
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import datatouch.uikit.R

/**
 * A view to show a series of numbers in a circular pattern.
 */
class RadialTextsView(context: Context?) : View(context) {
    private val mPaint = Paint()
    private val mSelectedPaint = Paint()
    private val mInactivePaint = Paint()
    private var mDrawValuesReady = false
    private var mIsInitialized = false
    private var selection = -1
    private var mValidator: SelectionValidator? = null
    private var mTypefaceLight: Typeface? = null
    private var mTypefaceRegular: Typeface? = null
    private var mTexts: Array<String> = emptyArray()
    private var mInnerTexts: Array<String>? = emptyArray()
    private var mIs24HourMode = false
    private var mHasInnerCircle = false
    private var mCircleRadiusMultiplier = 0f
    private var mAmPmCircleRadiusMultiplier = 0f
    private var mNumbersRadiusMultiplier = 0f
    private var mInnerNumbersRadiusMultiplier = 0f
    private var mTextSizeMultiplier = 0f
    private var mInnerTextSizeMultiplier = 0f
    private var mXCenter = 0
    private var mYCenter = 0
    private var mCircleRadius = 0f
    private var mTextGridValuesDirty = false
    private var mTextSize = 0f
    private var mInnerTextSize = 0f
    private var mTextGridHeights: FloatArray = FloatArray(0)
    private var mTextGridWidths: FloatArray = FloatArray(0)
    private var mInnerTextGridHeights: FloatArray = FloatArray(0)
    private var mInnerTextGridWidths: FloatArray = FloatArray(0)
    private var mAnimationRadiusMultiplier = 0f
    private var mTransitionMidRadiusMultiplier = 0f
    private var mTransitionEndRadiusMultiplier = 0f
    var mDisappearAnimator: ObjectAnimator? = null
    var mReappearAnimator: ObjectAnimator? = null
    private var mInvalidateUpdateListener: InvalidateUpdateListener? = null
    fun initialize(context: Context, texts: Array<String>, innerTexts: Array<String>?,
                   controller: TimePickerController, validator: SelectionValidator?, disappearsOut: Boolean) {
        if (mIsInitialized) {
            Log.e(TAG, "This RadialTextsView may only be initialized once.")
            return
        }
        val res = context.resources

        // Set up the paint.
        val textColorRes = if (controller.isThemeDark) R.color.mdtp_white else R.color.mdtp_numbers_text_color
        mPaint.color = ContextCompat.getColor(context, textColorRes)
        val typefaceFamily = res.getString(R.string.mdtp_radial_numbers_typeface)
        mTypefaceLight = Typeface.create(typefaceFamily, Typeface.NORMAL)
        val typefaceFamilyRegular = res.getString(R.string.mdtp_sans_serif)
        mTypefaceRegular = Typeface.create(typefaceFamilyRegular, Typeface.NORMAL)
        mPaint.isAntiAlias = true
        mPaint.textAlign = Align.CENTER

        // Set up the selected paint
        val selectedTextColor = ContextCompat.getColor(context, R.color.mdtp_white)
        mSelectedPaint.color = selectedTextColor
        mSelectedPaint.isAntiAlias = true
        mSelectedPaint.textAlign = Align.CENTER

        // Set up the inactive paint
        val inactiveColorRes = if (controller.isThemeDark) R.color.mdtp_date_picker_text_disabled_dark_theme else R.color.mdtp_date_picker_text_disabled
        mInactivePaint.color = ContextCompat.getColor(context, inactiveColorRes)
        mInactivePaint.isAntiAlias = true
        mInactivePaint.textAlign = Align.CENTER
        mTexts = texts
        mInnerTexts = innerTexts
        mIs24HourMode = controller.is24HourMode()
        mHasInnerCircle = innerTexts != null

        // Calculate the radius for the main circle.
        if (mIs24HourMode || controller.version != TimePickerDialog.Version.VERSION_1) {
            mCircleRadiusMultiplier =
                    res.getString(R.string.mdtp_circle_radius_multiplier_24HourMode).toFloat()
        } else {
            mCircleRadiusMultiplier =
                    res.getString(R.string.mdtp_circle_radius_multiplier).toFloat()
            mAmPmCircleRadiusMultiplier = res.getString(R.string.mdtp_ampm_circle_radius_multiplier).toFloat()
        }

        // Initialize the widths and heights of the grid, and calculate the values for the numbers.
        mTextGridHeights = FloatArray(7)
        mTextGridWidths = FloatArray(7)
        if (mHasInnerCircle) {
            mNumbersRadiusMultiplier =
                    res.getString(R.string.mdtp_numbers_radius_multiplier_outer).toFloat()
            mInnerNumbersRadiusMultiplier =
                    res.getString(R.string.mdtp_numbers_radius_multiplier_inner).toFloat()

            // Version 2 layout draws outer circle bigger than inner
            if (controller.version == TimePickerDialog.Version.VERSION_1) {
                mTextSizeMultiplier =
                        res.getString(R.string.mdtp_text_size_multiplier_outer).toFloat()
                mInnerTextSizeMultiplier =
                        res.getString(R.string.mdtp_text_size_multiplier_inner).toFloat()
            } else {
                mTextSizeMultiplier =
                        res.getString(R.string.mdtp_text_size_multiplier_outer_v2).toFloat()
                mInnerTextSizeMultiplier =
                        res.getString(R.string.mdtp_text_size_multiplier_inner_v2).toFloat()
            }
            mInnerTextGridHeights = FloatArray(7)
            mInnerTextGridWidths = FloatArray(7)
        } else {
            mNumbersRadiusMultiplier =
                    res.getString(R.string.mdtp_numbers_radius_multiplier_normal).toFloat()
            mTextSizeMultiplier =
                    res.getString(R.string.mdtp_text_size_multiplier_normal).toFloat()
        }
        mAnimationRadiusMultiplier = 1f
        mTransitionMidRadiusMultiplier = 1f + 0.05f * if (disappearsOut) -1 else 1
        mTransitionEndRadiusMultiplier = 1f + 0.3f * if (disappearsOut) 1 else -1
        mInvalidateUpdateListener = InvalidateUpdateListener()
        mValidator = validator
        mTextGridValuesDirty = true
        mIsInitialized = true
    }

    /**
     * Set the value of the selected text. Depending on the theme this will be rendered differently
     * @param selection The text which is currently selected
     */
    fun setSelection(selection: Int) {
        this.selection = selection
    }

    /**
     * Allows for smoother animation.
     */
    override fun hasOverlappingRendering(): Boolean {
        return false
    }

    /**
     * Used by the animation to move the numbers in and out.
     */
    fun setAnimationRadiusMultiplier(animationRadiusMultiplier: Float) {
        mAnimationRadiusMultiplier = animationRadiusMultiplier
        mTextGridValuesDirty = true
    }

    public override fun onDraw(canvas: Canvas) {
        val viewWidth = width
        if (viewWidth == 0 || !mIsInitialized) {
            return
        }
        if (!mDrawValuesReady) {
            mXCenter = width / 2
            mYCenter = height / 2
            mCircleRadius = Math.min(mXCenter, mYCenter) * mCircleRadiusMultiplier
            if (!mIs24HourMode) {
                // We'll need to draw the AM/PM circles, so the main circle will need to have
                // a slightly higher center. To keep the entire view centered vertically, we'll
                // have to push it up by half the radius of the AM/PM circles.
                val amPmCircleRadius = mCircleRadius * mAmPmCircleRadiusMultiplier
                mYCenter -= (amPmCircleRadius * 0.75).toInt()
            }
            mTextSize = mCircleRadius * mTextSizeMultiplier
            if (mHasInnerCircle) {
                mInnerTextSize = mCircleRadius * mInnerTextSizeMultiplier
            }

            // Because the text positions will be static, pre-render the animations.
            renderAnimations()
            mTextGridValuesDirty = true
            mDrawValuesReady = true
        }

        // Calculate the text positions, but only if they've changed since the last onDraw.
        if (mTextGridValuesDirty) {
            val numbersRadius = mCircleRadius * mNumbersRadiusMultiplier * mAnimationRadiusMultiplier

            // Calculate the positions for the 12 numbers in the main circle.
            calculateGridSizes(numbersRadius, mXCenter.toFloat(), mYCenter.toFloat(),
                    mTextSize, mTextGridHeights, mTextGridWidths)
            if (mHasInnerCircle) {
                // If we have an inner circle, calculate those positions too.
                val innerNumbersRadius = mCircleRadius * mInnerNumbersRadiusMultiplier * mAnimationRadiusMultiplier
                calculateGridSizes(innerNumbersRadius, mXCenter.toFloat(), mYCenter.toFloat(),
                        mInnerTextSize, mInnerTextGridHeights, mInnerTextGridWidths)
            }
            mTextGridValuesDirty = false
        }

        // Draw the texts in the pre-calculated positions.
        drawTexts(canvas, mTextSize, mTypefaceLight, mTexts, mTextGridWidths, mTextGridHeights)
        if (mHasInnerCircle) {
            drawTexts(canvas, mInnerTextSize, mTypefaceRegular, mInnerTexts,
                    mInnerTextGridWidths, mInnerTextGridHeights)
        }
    }

    /**
     * Using the trigonometric Unit Circle, calculate the positions that the text will need to be
     * drawn at based on the specified circle radius. Place the values in the textGridHeights and
     * textGridWidths parameters.
     */
    private fun calculateGridSizes(numbersRadius: Float, xCenter: Float, yCenter: Float,
                                   textSize: Float, textGridHeights: FloatArray, textGridWidths: FloatArray) {
        /*
         * The numbers need to be drawn in a 7x7 grid, representing the points on the Unit Circle.
         */
        var yCenter = yCenter
        // cos(30) = a / r => r * cos(30) = a => r * √3/2 = a
        val offset2 = numbersRadius * Math.sqrt(3.0).toFloat() / 2f
        // sin(30) = o / r => r * sin(30) = o => r / 2 = a
        val offset3 = numbersRadius / 2f
        mPaint.textSize = textSize
        mSelectedPaint.textSize = textSize
        mInactivePaint.textSize = textSize
        // We'll need yTextBase to be slightly lower to account for the text's baseline.
        yCenter -= (mPaint.descent() + mPaint.ascent()) / 2
        textGridHeights[0] = yCenter - numbersRadius
        textGridWidths[0] = xCenter - numbersRadius
        textGridHeights[1] = yCenter - offset2
        textGridWidths[1] = xCenter - offset2
        textGridHeights[2] = yCenter - offset3
        textGridWidths[2] = xCenter - offset3
        textGridHeights[3] = yCenter
        textGridWidths[3] = xCenter
        textGridHeights[4] = yCenter + offset3
        textGridWidths[4] = xCenter + offset3
        textGridHeights[5] = yCenter + offset2
        textGridWidths[5] = xCenter + offset2
        textGridHeights[6] = yCenter + numbersRadius
        textGridWidths[6] = xCenter + numbersRadius
    }

    private fun assignTextColors(texts: Array<String>?): Array<Paint?> {
        val paints = arrayOfNulls<Paint>(texts!!.size)
        for (i in texts.indices) {
            val text = texts[i].toInt()
            if (text == selection) paints[i] = mSelectedPaint else if (mValidator!!.isValidSelection(text)) paints[i] = mPaint else paints[i] = mInactivePaint
        }
        return paints
    }

    /**
     * Draw the 12 text values at the positions specified by the textGrid parameters.
     */
    private fun drawTexts(canvas: Canvas, textSize: Float, typeface: Typeface?, texts: Array<String>?,
                          textGridWidths: FloatArray, textGridHeights: FloatArray) {
        mPaint.textSize = textSize
        mPaint.typeface = typeface
        val textPaints = assignTextColors(texts)
        canvas.drawText(texts!![0], textGridWidths[3], textGridHeights[0], textPaints[0]!!)
        canvas.drawText(texts[1], textGridWidths[4], textGridHeights[1], textPaints[1]!!)
        canvas.drawText(texts[2], textGridWidths[5], textGridHeights[2], textPaints[2]!!)
        canvas.drawText(texts[3], textGridWidths[6], textGridHeights[3], textPaints[3]!!)
        canvas.drawText(texts[4], textGridWidths[5], textGridHeights[4], textPaints[4]!!)
        canvas.drawText(texts[5], textGridWidths[4], textGridHeights[5], textPaints[5]!!)
        canvas.drawText(texts[6], textGridWidths[3], textGridHeights[6], textPaints[6]!!)
        canvas.drawText(texts[7], textGridWidths[2], textGridHeights[5], textPaints[7]!!)
        canvas.drawText(texts[8], textGridWidths[1], textGridHeights[4], textPaints[8]!!)
        canvas.drawText(texts[9], textGridWidths[0], textGridHeights[3], textPaints[9]!!)
        canvas.drawText(texts[10], textGridWidths[1], textGridHeights[2], textPaints[10]!!)
        canvas.drawText(texts[11], textGridWidths[2], textGridHeights[1], textPaints[11]!!)
    }

    /**
     * Render the animations for appearing and disappearing.
     */
    private fun renderAnimations() {
        var kf0: Keyframe?
        var kf1: Keyframe?
        var kf2: Keyframe?
        val kf3: Keyframe
        var midwayPoint = 0.2f
        val duration = 500

        // Set up animator for disappearing.
        kf0 = Keyframe.ofFloat(0f, 1f)
        kf1 = Keyframe.ofFloat(midwayPoint, mTransitionMidRadiusMultiplier)
        kf2 = Keyframe.ofFloat(1f, mTransitionEndRadiusMultiplier)
        val radiusDisappear = PropertyValuesHolder.ofKeyframe(
                "animationRadiusMultiplier", kf0, kf1, kf2)
        kf0 = Keyframe.ofFloat(0f, 1f)
        kf1 = Keyframe.ofFloat(1f, 0f)
        val fadeOut = PropertyValuesHolder.ofKeyframe("alpha", kf0, kf1)
        mDisappearAnimator = ObjectAnimator.ofPropertyValuesHolder(
                this, radiusDisappear, fadeOut).setDuration(duration.toLong())
        mDisappearAnimator!!.addUpdateListener(mInvalidateUpdateListener)


        // Set up animator for reappearing.
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
                "animationRadiusMultiplier", kf0, kf1, kf2, kf3)
        kf0 = Keyframe.ofFloat(0f, 0f)
        kf1 = Keyframe.ofFloat(delayPoint, 0f)
        kf2 = Keyframe.ofFloat(1f, 1f)
        val fadeIn = PropertyValuesHolder.ofKeyframe("alpha", kf0, kf1, kf2)
        mReappearAnimator = ObjectAnimator.ofPropertyValuesHolder(
                this, radiusReappear, fadeIn).setDuration(totalDuration.toLong())
        mReappearAnimator!!.addUpdateListener(mInvalidateUpdateListener)
    }

    val disappearAnimator: ObjectAnimator?
        get() {
            if (!mIsInitialized || !mDrawValuesReady || mDisappearAnimator == null) {
                Log.e(TAG, "RadialTextView was not ready for animation.")
                return null
            }
            return mDisappearAnimator
        }

    val reappearAnimator: ObjectAnimator?
        get() {
            if (!mIsInitialized || !mDrawValuesReady || mReappearAnimator == null) {
                Log.e(TAG, "RadialTextView was not ready for animation.")
                return null
            }
            return mReappearAnimator
        }

    private inner class InvalidateUpdateListener : ValueAnimator.AnimatorUpdateListener {
        override fun onAnimationUpdate(animation: ValueAnimator) {
            this@RadialTextsView.invalidate()
        }
    }

    interface SelectionValidator {
        fun isValidSelection(selection: Int): Boolean
    }

    companion object {
        private const val TAG = "RadialTextsView"
    }

}