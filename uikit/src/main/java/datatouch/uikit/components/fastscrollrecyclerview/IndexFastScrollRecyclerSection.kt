package datatouch.uikit.components.fastscrollrecyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.SectionIndexer
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver

class IndexFastScrollRecyclerSection(
    context: Context,
    rv: IndexFastScrollRecyclerView
) : AdapterDataObserver() {
    var attrs: AttributeSet? = null
    private var mIndexbarWidth: Float
    private var mIndexbarMargin: Float
    private val mPreviewPadding: Float
    private val mDensity: Float
    private val mScaledDensity: Float
    private var mListViewWidth = 0
    private var mListViewHeight = 0
    private var mCurrentSection = -1
    private var mIsIndexing = false
    private var mRecyclerView: RecyclerView? = null
    private var mIndexer: SectionIndexer? = null
    private var mSections: Array<String>? = null
    private var mIndexbarRect: RectF? = null
    private var setIndexTextSize: Int
    private val setIndexbarWidth: Float
    private val setIndexbarMargin: Float
    private var setPreviewPadding: Int
    private var previewVisibility = true
    private var setIndexBarCornerRadius: Int
    private var setTypeface: Typeface? = null
    private var setIndexBarVisibility = true
    private var setSetIndexBarHighLateTextVisibility = false

    @ColorInt
    private var indexbarBackgroudColor: Int

    @ColorInt
    private var indexbarTextColor: Int

    @ColorInt
    private var indexbarHighLateTextColor: Int
    private var indexbarBackgroudAlpha: Int
    private val indexPaintPaintColor = Color.WHITE

    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == WHAT_FADE_PREVIEW) {
                mRecyclerView!!.invalidate()
            }
        }
    }

    fun draw(canvas: Canvas) {
        if (setIndexBarVisibility) {
            val indexbarPaint = Paint()
            indexbarPaint.color = indexbarBackgroudColor
            indexbarPaint.alpha = indexbarBackgroudAlpha
            indexbarPaint.isAntiAlias = true
            canvas.drawRoundRect(
                mIndexbarRect!!,
                setIndexBarCornerRadius * mDensity,
                setIndexBarCornerRadius * mDensity,
                indexbarPaint
            )
            if (mSections != null && mSections!!.size > 0) {
                // Preview is shown when mCurrentSection is set
                if (previewVisibility && mCurrentSection >= 0 && mSections!![mCurrentSection] !== "") {
                    val previewPaint = Paint()
                    previewPaint.color = Color.BLACK
                    previewPaint.alpha = 96
                    previewPaint.isAntiAlias = true
                    previewPaint.setShadowLayer(
                        3f,
                        0f,
                        0f,
                        Color.argb(64, 0, 0, 0)
                    )
                    val previewTextPaint = Paint()
                    previewTextPaint.color = Color.WHITE
                    previewTextPaint.isAntiAlias = true
                    previewTextPaint.textSize = 50 * mScaledDensity
                    previewTextPaint.typeface = setTypeface
                    val previewTextWidth =
                        previewTextPaint.measureText(mSections!![mCurrentSection])
                    val previewSize =
                        2 * mPreviewPadding + previewTextPaint.descent() - previewTextPaint.ascent()
                    val previewRect = RectF(
                        mListViewWidth / 2 - previewTextWidth / 2
                        , (mListViewHeight - previewSize) / 2
                        , mListViewWidth / 2 - previewTextWidth / 2 + previewTextWidth
                        , (mListViewHeight - previewSize) / 2 + previewSize
                    )
                    canvas.drawRoundRect(previewRect, 5 * mDensity, 5 * mDensity, previewPaint)
                    canvas.drawText(
                        mSections!![mCurrentSection],
                        previewRect.left
                        ,
                        previewRect.top + mPreviewPadding - previewTextPaint.ascent() + 1,
                        previewTextPaint
                    )
                    fade(300)
                }
                val indexPaint = Paint()
                indexPaint.color = indexbarTextColor
                indexPaint.isAntiAlias = true
                indexPaint.textSize = setIndexTextSize * mScaledDensity
                indexPaint.typeface = setTypeface
                val sectionHeight =
                    (mIndexbarRect!!.height() - 2 * mIndexbarMargin) / mSections!!.size
                val paddingTop =
                    (sectionHeight - (indexPaint.descent() - indexPaint.ascent())) / 2
                for (i in mSections!!.indices) {
                    if (setSetIndexBarHighLateTextVisibility) {
                        if (mCurrentSection > -1 && i == mCurrentSection) {
                            indexPaint.typeface = Typeface.create(setTypeface, Typeface.BOLD)
                            indexPaint.textSize = (setIndexTextSize + 3) * mScaledDensity
                            indexPaint.color = indexbarHighLateTextColor
                        } else {
                            indexPaint.typeface = setTypeface
                            indexPaint.textSize = setIndexTextSize * mScaledDensity
                            indexPaint.color = indexbarTextColor
                        }
                        val paddingLeft =
                            (mIndexbarWidth - indexPaint.measureText(mSections!![i])) / 2
                        canvas.drawText(
                            mSections!![i],
                            mIndexbarRect!!.left + paddingLeft
                            ,
                            mIndexbarRect!!.top + mIndexbarMargin + sectionHeight * i + paddingTop - indexPaint.ascent(),
                            indexPaint
                        )
                    } else {
                        val paddingLeft =
                            (mIndexbarWidth - indexPaint.measureText(mSections!![i])) / 2
                        canvas.drawText(
                            mSections!![i],
                            mIndexbarRect!!.left + paddingLeft
                            ,
                            mIndexbarRect!!.top + mIndexbarMargin + sectionHeight * i + paddingTop - indexPaint.ascent(),
                            indexPaint
                        )
                    }
                }
            }
        }
    }

    fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN ->                 // If down event occurs inside index bar region, start indexing
                if (contains(ev.x, ev.y)) {

                    // It demonstrates that the motion event started from index bar
                    mIsIndexing = true
                    // Determine which section the point is in, and move the list to that section
                    mCurrentSection = getSectionByPoint(ev.y)
                    scrollToPosition()
                    return true
                }
            MotionEvent.ACTION_MOVE -> if (mIsIndexing) {
                // If this event moves inside index bar
                if (contains(ev.x, ev.y)) {
                    // Determine which section the point is in, and move the list to that section
                    mCurrentSection = getSectionByPoint(ev.y)
                    scrollToPosition()
                }
                return true
            }
            MotionEvent.ACTION_UP -> if (mIsIndexing) {
                mIsIndexing = false
                mCurrentSection = -1
            }
        }
        return false
    }

    private fun scrollToPosition() {
        try {
            val position = mIndexer!!.getPositionForSection(mCurrentSection)
            val layoutManager = mRecyclerView!!.layoutManager
            if (layoutManager is LinearLayoutManager) {
                layoutManager.scrollToPositionWithOffset(position, 0)
            } else {
                layoutManager!!.scrollToPosition(position)
            }
        } catch (e: Exception) {
        }
    }

    fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mListViewWidth = w
        mListViewHeight = h
        mIndexbarRect = RectF(
            w - mIndexbarMargin - mIndexbarWidth
            , mIndexbarMargin
            , w - mIndexbarMargin
            , h - mIndexbarMargin
        )
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        if (adapter is SectionIndexer) {
            adapter.registerAdapterDataObserver(this)
            mIndexer = adapter
            mSections = mIndexer!!.sections as Array<String>
        }
    }

    override fun onChanged() {
        super.onChanged()
        mSections = mIndexer!!.sections as Array<String>
    }

    fun contains(x: Float, y: Float): Boolean {
        // Determine if the point is in index bar region, which includes the right margin of the bar
        return x >= mIndexbarRect!!.left && y >= mIndexbarRect!!.top && y <= mIndexbarRect!!.top + mIndexbarRect!!.height()
    }

    private fun getSectionByPoint(y: Float): Int {
        if (mSections == null || mSections!!.size == 0) return 0
        if (y < mIndexbarRect!!.top + mIndexbarMargin) return 0
        return if (y >= mIndexbarRect!!.top + mIndexbarRect!!.height() - mIndexbarMargin) mSections!!.size - 1 else ((y - mIndexbarRect!!.top - mIndexbarMargin) / ((mIndexbarRect!!.height() - 2 * mIndexbarMargin) / mSections!!.size)).toInt()
    }

    private fun fade(delay: Long) {
        mHandler.removeMessages(0)
        mHandler.sendEmptyMessageAtTime(
            WHAT_FADE_PREVIEW,
            SystemClock.uptimeMillis() + delay
        )
    }

    private fun convertTransparentValueToBackgroundAlpha(value: Float): Int {
        return (255 * value).toInt()
    }

    /**
     * @param value int to set the text size of the index bar
     */
    fun setIndexTextSize(value: Int) {
        setIndexTextSize = value
    }

    /**
     * @param value float to set the width of the index bar
     */
    fun setIndexbarWidth(value: Float) {
        mIndexbarWidth = value
    }

    /**
     * @param value float to set the margin of the index bar
     */
    fun setIndexbarMargin(value: Float) {
        mIndexbarMargin = value
    }

    /**
     * @param value int to set preview padding
     */
    fun setPreviewPadding(value: Int) {
        setPreviewPadding = value
    }

    /**
     * @param value int to set the radius of the index bar
     */
    fun setIndexBarCornerRadius(value: Int) {
        setIndexBarCornerRadius = value
    }

    /**
     * @param value float to set the transparency of the color for index bar
     */
    fun setIndexBarTransparentValue(value: Float) {
        indexbarBackgroudAlpha = convertTransparentValueToBackgroundAlpha(value)
    }

    /**
     * @param typeface Typeface to set the typeface of the preview & the index bar
     */
    fun setTypeface(typeface: Typeface?) {
        setTypeface = typeface
    }

    /**
     * @param shown boolean to show or hide the index bar
     */
    fun setIndexBarVisibility(shown: Boolean) {
        setIndexBarVisibility = shown
    }

    /**
     * @param shown boolean to show or hide the preview box
     */
    fun setPreviewVisibility(shown: Boolean) {
        previewVisibility = shown
    }

    /**
     * @param color The color for the scroll track
     */
    fun setIndexBarColor(@ColorInt color: Int) {
        indexbarBackgroudColor = color
    }

    /**
     * @param color The text color for the index bar
     */
    fun setIndexBarTextColor(@ColorInt color: Int) {
        indexbarTextColor = color
    }

    /**
     * @param color The text color for the index bar
     */
    fun setIndexBarHighLateTextColor(@ColorInt color: Int) {
        indexbarHighLateTextColor = color
    }

    /**
     * @param shown boolean to show or hide the index bar
     */
    fun setIndexBarHighLateTextVisibility(shown: Boolean) {
        setSetIndexBarHighLateTextVisibility = shown
    }

    companion object {
        private const val WHAT_FADE_PREVIEW = 1
    }

    init {
        setIndexTextSize = rv.setIndexTextSize
        setIndexbarWidth = rv.indexBarWidth
        setIndexbarMargin = rv.mIndexbarMargin
        setPreviewPadding = rv.mPreviewPadding
        setIndexBarCornerRadius = rv.mIndexBarCornerRadius
        indexbarBackgroudColor = rv.mIndexbarBackgroudColor
        indexbarTextColor = rv.mIndexbarTextColor
        indexbarHighLateTextColor = rv.mIndexbarHighLateTextColor
        indexbarBackgroudAlpha =
            convertTransparentValueToBackgroundAlpha(rv.mIndexBarTransparentValue)
        mDensity = context.resources.displayMetrics.density
        mScaledDensity = context.resources.displayMetrics.scaledDensity
        mRecyclerView = rv
        setAdapter(mRecyclerView?.adapter)
        mIndexbarWidth = setIndexbarWidth * mDensity
        mIndexbarMargin = setIndexbarMargin * mDensity
        mPreviewPadding = setPreviewPadding * mDensity
    }
}