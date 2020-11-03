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
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import datatouch.uikit.R


/**
 * Draws a simple white circle on which the numbers will be drawn.
 */
class CircleView(context: Context?) : View(context) {
    private val mPaint = Paint()
    private var mIs24HourMode = false
    private var mCircleColor = 0
    private var mDotColor = 0
    private var mCircleRadiusMultiplier = 0f
    private var mAmPmCircleRadiusMultiplier = 0f
    private var mIsInitialized = false
    private var mDrawValuesReady = false
    private var mXCenter = 0
    private var mYCenter = 0
    private var mCircleRadius = 0
    fun initialize(context: Context, controller: TimePickerController) {
        if (mIsInitialized) {
            Log.e(TAG, "CircleView may only be initialized once.")
            return
        }
        val res = context.resources
        val colorRes: Int = if (controller.isThemeDark) R.color.mdtp_circle_background_dark_theme else R.color.mdtp_circle_color
        mCircleColor = ContextCompat.getColor(context, colorRes)
        mDotColor = controller.accentColor
        mPaint.isAntiAlias = true
        mIs24HourMode = controller.is24HourMode()
        if (mIs24HourMode || controller.version !== TimePickerDialog.Version.VERSION_1) {
            mCircleRadiusMultiplier =
                    res.getString(R.string.mdtp_circle_radius_multiplier_24HourMode).toFloat()
        } else {
            mCircleRadiusMultiplier =
                    res.getString(R.string.mdtp_circle_radius_multiplier).toFloat()
            mAmPmCircleRadiusMultiplier = res.getString(R.string.mdtp_ampm_circle_radius_multiplier).toFloat()
        }
        mIsInitialized = true
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
            mDrawValuesReady = true
        }

        // Draw the white circle.
        mPaint.color = mCircleColor
        canvas.drawCircle(mXCenter.toFloat(), mYCenter.toFloat(), mCircleRadius.toFloat(), mPaint)

        // Draw a small black circle in the center.
        mPaint.color = mDotColor
        canvas.drawCircle(mXCenter.toFloat(), mYCenter.toFloat(), 8f, mPaint)
    }

    companion object {
        private const val TAG = "CircleView"
    }

}