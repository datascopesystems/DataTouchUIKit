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
package datatouch.uikit.components.datapicker.date

import android.content.Context
import android.graphics.drawable.StateListDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import datatouch.uikit.R

/**
 * Displays a selectable list of years.
 */
class YearPickerView(
    context: Context,
    private val mController: DatePickerController
) : ListView(context), OnItemClickListener,
    DatePickerFragmentDialog.OnDateChangedListener {
    private var mAdapter: YearAdapter? = null
    private val mViewSize: Int
    private val mChildSize: Int
    private var mSelectedView: TextView? = null
    private fun init() {
        mAdapter = YearAdapter(mController.minYear, mController.maxYear)
        adapter = mAdapter
    }

    override fun onItemClick(
        parent: AdapterView<*>?,
        view: View,
        position: Int,
        id: Long
    ) {
        val clickedView = view as TextView
        if (clickedView != null) {
            if (clickedView !== mSelectedView) {
                if (mSelectedView != null) {
                    mSelectedView!!.requestLayout()
                }
                clickedView.requestLayout()
                mSelectedView = clickedView
            }
            mController.onYearSelected(getYearFromTextView(clickedView))
            mAdapter!!.notifyDataSetChanged()
        }
    }

    fun postSetSelectionCentered(position: Int) {
        postSetSelectionFromTop(position, mViewSize / 2 - mChildSize / 2)
    }

    fun postSetSelectionFromTop(position: Int, offset: Int) {
        post {
            setSelectionFromTop(position, offset)
            requestLayout()
        }
    }

    val firstPositionOffset: Int
        get() {
            val firstChild = getChildAt(0) ?: return 0
            return firstChild.top
        }

    override fun onDateChanged() {
        mAdapter!!.notifyDataSetChanged()
        postSetSelectionCentered(mController.selectedDay!!.year - mController.minYear)
    }

    override fun onInitializeAccessibilityEvent(event: AccessibilityEvent) {
        super.onInitializeAccessibilityEvent(event)
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            event.fromIndex = 0
            event.toIndex = 0
        }
    }

    private inner class YearAdapter internal constructor(minYear: Int, maxYear: Int) :
        BaseAdapter() {
        private val mMinYear: Int
        private val mMaxYear: Int
        override fun getCount(): Int {
            return mMaxYear - mMinYear + 1
        }

        override fun getItem(position: Int): Any {
            return mMinYear + position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(
            position: Int,
            convertView: View,
            parent: ViewGroup
        ): View {
            val v: TextView
            v = if (convertView != null) {
                convertView as TextView
            } else {
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.amdp_year_label_text_view, parent, false) as TextView
            }
            val year = mMinYear + position
            val selected = mController.selectedDay!!.year === year
            v.text = year.toString()
            v.requestLayout()
            if (selected) {
                mSelectedView = v
            }
            return v
        }

        init {
            require(minYear <= maxYear) { "minYear > maxYear" }
            mMinYear = minYear
            mMaxYear = maxYear
        }
    }

    companion object {
        private const val TAG = "YearPickerView"
        private fun getYearFromTextView(view: TextView): Int {
            return Integer.valueOf(view.text.toString())
        }
    }

    init {
        mController.registerOnDateChangedListener(this)
        val frame = ViewGroup.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        layoutParams = frame
        val res = context.resources
        mViewSize = res.getDimensionPixelOffset(R.dimen.amdp_date_picker_view_animator_height)
        mChildSize = res.getDimensionPixelOffset(R.dimen.amdp_year_label_height)
        isVerticalFadingEdgeEnabled = true
        setFadingEdgeLength(mChildSize / 3)
        init()
        onItemClickListener = this
        selector = StateListDrawable()
        dividerHeight = 0
        onDateChanged()
    }
}