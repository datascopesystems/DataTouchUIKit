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

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Typeface
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import datatouch.uikit.R
import datatouch.uikit.utils.ColorUtils
import java.text.DateFormatSymbols
import java.util.*

/**
 * Draw the two smaller AM and PM circles next to where the larger circle will be.
 */
class AmPmCirclesView(context: Context?) : View(context) {
    private val mPaint = Paint()
    private var mSelectedAlpha = 0
    private var mTouchedColor = 0
    private var mUnselectedColor = 0
    private var mAmPmTextColor = 0
    private var mAmPmSelectedTextColor = 0
    private var mAmPmDisabledTextColor = 0
    private var mSelectedColor = 0
    private var mCircleRadiusMultiplier = 0f
    private var mAmPmCircleRadiusMultiplier = 0f
    private var mAmText: String? = null
    private var mPmText: String? = null
    private var mAmDisabled = false
    private var mPmDisabled = false
    private var mIsInitialized = false
    private var mDrawValuesReady = false
    private var mAmPmCircleRadius = 0
    private var mAmXCenter = 0
    private var mPmXCenter = 0
    private var mAmPmYCenter = 0
    private var mAmOrPm = 0
    private var mAmOrPmPressed = 0
    fun initialize(context: Context, locale: Locale?, controller: TimePickerController, amOrPm: Int) {
        if (mIsInitialized) {
            Log.e(AmPmCirclesView::javaClass.name, "AmPmCirclesView may only be initialized once.")
            return
        }
        val res = context.resources
        if (controller.isThemeDark) {
            mUnselectedColor = ContextCompat.getColor(context, R.color.mdtp_circle_background_dark_theme)
            mAmPmTextColor = ContextCompat.getColor(context, R.color.mdtp_white)
            mAmPmDisabledTextColor = ContextCompat.getColor(context, R.color.mdtp_date_picker_text_disabled_dark_theme)
            mSelectedAlpha = SELECTED_ALPHA_THEME_DARK
        } else {
            mUnselectedColor = ContextCompat.getColor(context, R.color.mdtp_white)
            mAmPmTextColor = ContextCompat.getColor(context, R.color.mdtp_ampm_text_color)
            mAmPmDisabledTextColor = ContextCompat.getColor(context, R.color.mdtp_date_picker_text_disabled)
            mSelectedAlpha = SELECTED_ALPHA
        }
        mSelectedColor = controller.accentColor
        mTouchedColor = ColorUtils.darkenColor(mSelectedColor)
        mAmPmSelectedTextColor = ContextCompat.getColor(context, R.color.mdtp_white)
        val typefaceFamily = res.getString(R.string.mdtp_sans_serif)
        val tf = Typeface.create(typefaceFamily, Typeface.NORMAL)
        mPaint.typeface = tf
        mPaint.isAntiAlias = true
        mPaint.textAlign = Align.CENTER
        mCircleRadiusMultiplier = res.getString(R.string.mdtp_circle_radius_multiplier).toFloat()
        mAmPmCircleRadiusMultiplier = res.getString(R.string.mdtp_ampm_circle_radius_multiplier).toFloat()
        val amPmTexts = DateFormatSymbols(locale).amPmStrings
        mAmText = amPmTexts[0]
        mPmText = amPmTexts[1]
        mAmDisabled = controller.isAmDisabled
        mPmDisabled = controller.isPmDisabled
        setAmOrPm(amOrPm)
        mAmOrPmPressed = -1
        mIsInitialized = true
    }

    fun setAmOrPm(amOrPm: Int) {
        mAmOrPm = amOrPm
    }

    fun setAmOrPmPressed(amOrPmPressed: Int) {
        mAmOrPmPressed = amOrPmPressed
    }

    /**
     * Calculate whether the coordinates are touching the AM or PM circle.
     */
    fun getIsTouchingAmOrPm(xCoord: Float, yCoord: Float): Int {
        if (!mDrawValuesReady) {
            return -1
        }
        val squaredYDistance = ((yCoord - mAmPmYCenter) * (yCoord - mAmPmYCenter)).toInt()
        val distanceToAmCenter = Math.sqrt((xCoord - mAmXCenter) * (xCoord - mAmXCenter) + squaredYDistance.toDouble()).toInt()
        if (distanceToAmCenter <= mAmPmCircleRadius && !mAmDisabled) {
            return AM
        }
        val distanceToPmCenter = Math.sqrt((xCoord - mPmXCenter) * (xCoord - mPmXCenter) + squaredYDistance.toDouble()).toInt()
        return if (distanceToPmCenter <= mAmPmCircleRadius && !mPmDisabled) {
            PM
        } else -1

        // Neither was close enough.
    }

    public override fun onDraw(canvas: Canvas) {
        val viewWidth = width
        if (viewWidth == 0 || !mIsInitialized) {
            return
        }
        if (!mDrawValuesReady) {
            val layoutXCenter = width / 2
            var layoutYCenter = height / 2
            val circleRadius = (Math.min(layoutXCenter, layoutYCenter) * mCircleRadiusMultiplier).toInt()
            mAmPmCircleRadius = (circleRadius * mAmPmCircleRadiusMultiplier).toInt()
            layoutYCenter += mAmPmCircleRadius * 0.75.toInt()
            val textSize = mAmPmCircleRadius * 3 / 4
            mPaint.textSize = textSize.toFloat()

            // Line up the vertical center of the AM/PM circles with the bottom of the main circle.
            mAmPmYCenter = layoutYCenter - mAmPmCircleRadius / 2 + circleRadius
            // Line up the horizontal edges of the AM/PM circles with the horizontal edges
            // of the main circle.
            mAmXCenter = layoutXCenter - circleRadius + mAmPmCircleRadius
            mPmXCenter = layoutXCenter + circleRadius - mAmPmCircleRadius
            mDrawValuesReady = true
        }

        // We'll need to draw either a lighter blue (for selection), a darker blue (for touching)
        // or white (for not selected).
        var amColor = mUnselectedColor
        var amAlpha = 255
        var amTextColor = mAmPmTextColor
        var pmColor = mUnselectedColor
        var pmAlpha = 255
        var pmTextColor = mAmPmTextColor
        if (mAmOrPm == AM) {
            amColor = mSelectedColor
            amAlpha = mSelectedAlpha
            amTextColor = mAmPmSelectedTextColor
        } else if (mAmOrPm == PM) {
            pmColor = mSelectedColor
            pmAlpha = mSelectedAlpha
            pmTextColor = mAmPmSelectedTextColor
        }
        if (mAmOrPmPressed == AM) {
            amColor = mTouchedColor
            amAlpha = mSelectedAlpha
        } else if (mAmOrPmPressed == PM) {
            pmColor = mTouchedColor
            pmAlpha = mSelectedAlpha
        }
        if (mAmDisabled) {
            amColor = mUnselectedColor
            amTextColor = mAmPmDisabledTextColor
        }
        if (mPmDisabled) {
            pmColor = mUnselectedColor
            pmTextColor = mAmPmDisabledTextColor
        }

        // Draw the two circles.
        mPaint.color = amColor
        mPaint.alpha = amAlpha
        canvas.drawCircle(mAmXCenter.toFloat(), mAmPmYCenter.toFloat(), mAmPmCircleRadius.toFloat(), mPaint)
        mPaint.color = pmColor
        mPaint.alpha = pmAlpha
        canvas.drawCircle(mPmXCenter.toFloat(), mAmPmYCenter.toFloat(), mAmPmCircleRadius.toFloat(), mPaint)

        // Draw the AM/PM texts on top.
        mPaint.color = amTextColor
        val textYCenter = mAmPmYCenter - (mPaint.descent() + mPaint.ascent()).toInt() / 2
        canvas.drawText(mAmText!!, mAmXCenter.toFloat(), textYCenter.toFloat(), mPaint)
        mPaint.color = pmTextColor
        canvas.drawText(mPmText!!, mPmXCenter.toFloat(), textYCenter.toFloat(), mPaint)
    }

    companion object {

    }

}

private const val SELECTED_ALPHA = 255
private const val SELECTED_ALPHA_THEME_DARK = 255
private const val AM = TimePickerDialog.AM
private const val PM = TimePickerDialog.PM