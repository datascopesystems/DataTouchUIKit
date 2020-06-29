package datatouch.uikit.components.floatinggroupexpandablelistview

import android.annotation.SuppressLint
import android.content.Context
import android.database.DataSetObserver
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.widget.AbsListView
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import datatouch.uikit.R
import datatouch.uikit.components.floatinggroupexpandablelistview.ReflectionUtils.getFieldValue
import datatouch.uikit.components.floatinggroupexpandablelistview.ReflectionUtils.invokeMethod
import datatouch.uikit.components.floatinggroupexpandablelistview.ReflectionUtils.setFieldValue

class FloatingGroupExpandableListView : ExpandableListView {
    private var adapter: WrapperExpandableListAdapter? = null
    private var dataSetObserver: DataSetObserver? = null
    private var onScrollListener: OnScrollListener? = null
    private var floatingGroupEnabled = true // By default, the floating group is enabled
    private var floatingGroupView: View? = null
    private var floatingGroupPosition = 0
    private var onScrollFloatingGroupListener: OnScrollFloatingGroupListener? = null
    private var onGroupClickListener: OnGroupClickListener? = null
    private var widthMeasureSpec = 0
    private var viewAttachInfo: Any? = null// An AttachInfo instance is added to the FloatingGroupView in order to have proper touch event handling
    private var handledByOnInterceptTouchEvent = false
    private var handledByOnTouchEvent = false
    private var onClickAction: Runnable? = null
    private var gestureDetector: GestureDetector? = null
    private var selectorEnabled = false
    private var shouldPositionSelector = false
    private var drawSelectorOnTop = false
    private var customSelector: Drawable? = null
    private var selectorPosition = 0
    private val selectorRect = Rect()
    private var positionSelectorOnTapAction: Runnable? = null
    private var clearSelectorOnTapAction: Runnable? = null
    private val indicatorRect = Rect()

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        super.setOnScrollListener(object : OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                if (onScrollListener != null) {
                    onScrollListener!!.onScrollStateChanged(view, scrollState)
                }
            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (onScrollListener != null) {
                    onScrollListener!!.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount)
                }
                if (floatingGroupEnabled && adapter != null && adapter!!.groupCount > 0 && visibleItemCount > 0) {
                    createFloatingGroupView(firstVisibleItem)
                }
            }
        })
        onClickAction = Runnable {
            var allowSelection = true
            if (onGroupClickListener != null) {
                allowSelection = !onGroupClickListener!!.onGroupClick(this@FloatingGroupExpandableListView, floatingGroupView, floatingGroupPosition, adapter!!.getGroupId(floatingGroupPosition))
            }
            if (allowSelection) {
                if (adapter!!.isGroupExpanded(floatingGroupPosition)) {
                    collapseGroup(floatingGroupPosition)
                } else {
                    expandGroup(floatingGroupPosition)
                }
                setSelectedGroup(floatingGroupPosition)
            }
        }
        positionSelectorOnTapAction = Runnable {
            positionSelectorOnFloatingGroup()
            isPressed = true
            if (floatingGroupView != null) {
                floatingGroupView!!.isPressed = true
            }
        }
        clearSelectorOnTapAction = Runnable {
            isPressed = false
            if (floatingGroupView != null) {
                floatingGroupView!!.isPressed = false
            }
            invalidate()
        }
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                if (floatingGroupView != null && !floatingGroupView!!.isLongClickable) {
                    val contextMenuInfo: ContextMenu.ContextMenuInfo = ExpandableListContextMenuInfo(floatingGroupView, getPackedPositionForGroup(floatingGroupPosition), adapter!!.getGroupId(floatingGroupPosition))
                    setFieldValue(AbsListView::class.java, "mContextMenuInfo", this@FloatingGroupExpandableListView, contextMenuInfo)
                    showContextMenu()
                }
            }
        })
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (adapter != null && dataSetObserver != null) {
            adapter!!.unregisterDataSetObserver(dataSetObserver!!)
            dataSetObserver = null
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        this.widthMeasureSpec = widthMeasureSpec
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun dispatchDraw(canvas: Canvas) { // Reflection is used here to obtain info about the selector

        selectorPosition = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> 0

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ->
                getFieldValue(AbsListView::class.java, "mSelectorPosition", this@FloatingGroupExpandableListView) as Int

            else -> getFieldValue(AbsListView::class.java, "mMotionPosition", this@FloatingGroupExpandableListView) as Int
        }

        val rect = getFieldValue(AbsListView::class.java, "mSelectorRect", this@FloatingGroupExpandableListView) as Rect?

        if (null != rect) selectorRect.set(rect)
        if (!drawSelectorOnTop) {
            drawDefaultSelector(canvas)
        }
        super.dispatchDraw(canvas)
        if (floatingGroupEnabled && floatingGroupView != null) {
            if (!drawSelectorOnTop) {
                drawFloatingGroupSelector(canvas)
            }
            canvas.save()
            canvas.clipRect(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom)
            if (floatingGroupView!!.visibility == View.VISIBLE) {
                drawChild(canvas, floatingGroupView, drawingTime)
            }
            drawFloatingGroupIndicator(canvas)
            canvas.restore()
            if (drawSelectorOnTop) {
                drawDefaultSelector(canvas)
                drawFloatingGroupSelector(canvas)
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action and MotionEvent.ACTION_MASK
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_CANCEL) {
            handledByOnInterceptTouchEvent = false
            handledByOnTouchEvent = false
            shouldPositionSelector = false
        }
        // If touch events are being handled by onInterceptTouchEvent() or onTouchEvent() we shouldn't dispatch them to the floating group
        if (!handledByOnInterceptTouchEvent && !handledByOnTouchEvent && floatingGroupView != null) {
            val screenCoords = IntArray(2)
            getLocationInWindow(screenCoords)
            val floatingGroupRect = RectF((screenCoords[0] + floatingGroupView!!.left).toFloat(), (screenCoords[1] + floatingGroupView!!.top).toFloat(), (screenCoords[0] + floatingGroupView!!.right).toFloat(), (screenCoords[1] + floatingGroupView!!.bottom).toFloat())
            if (floatingGroupRect.contains(ev.rawX, ev.rawY)) {
                if (selectorEnabled) {
                    when (action) {
                        MotionEvent.ACTION_DOWN -> {
                            shouldPositionSelector = true
                            removeCallbacks(positionSelectorOnTapAction)
                            postDelayed(positionSelectorOnTapAction, ViewConfiguration.getTapTimeout().toLong())
                        }
                        MotionEvent.ACTION_UP -> {
                            positionSelectorOnFloatingGroup()
                            isPressed = true
                            if (floatingGroupView != null) {
                                floatingGroupView!!.isPressed = true
                            }
                            removeCallbacks(clearSelectorOnTapAction)
                            postDelayed(clearSelectorOnTapAction, ViewConfiguration.getPressedStateDuration().toLong())
                        }
                    }
                }
                if (floatingGroupView!!.dispatchTouchEvent(ev)) {
                    gestureDetector!!.onTouchEvent(ev)
                    onInterceptTouchEvent(ev)
                    return true
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        handledByOnInterceptTouchEvent = super.onInterceptTouchEvent(ev)
        return handledByOnInterceptTouchEvent
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        handledByOnTouchEvent = super.onTouchEvent(ev)
        return handledByOnTouchEvent
    }

    override fun setSelector(sel: Drawable?) {
        super.setSelector(ColorDrawable(Color.TRANSPARENT))
        if (customSelector != null) {
            customSelector!!.callback = null
            unscheduleDrawable(customSelector)
        }
        customSelector = sel
        customSelector?.callback = this
    }

    override fun setDrawSelectorOnTop(onTop: Boolean) {
        super.setDrawSelectorOnTop(onTop)
        drawSelectorOnTop = onTop
    }

    override fun setAdapter(adapter: ExpandableListAdapter) {
        require(adapter is WrapperExpandableListAdapter) { "The adapter must be an instance of WrapperExpandableListAdapter" }
        setAdapter(adapter)
    }

    fun setAdapter(adapter: WrapperExpandableListAdapter) {
        super.setAdapter(adapter)
        if (dataSetObserver != null) {
            adapter.unregisterDataSetObserver()
            dataSetObserver = null
        }
        this.adapter = adapter
        if (dataSetObserver == null) {
            dataSetObserver = object : DataSetObserver() {
                override fun onChanged() {
                    floatingGroupView = null
                }

                override fun onInvalidated() {
                    floatingGroupView = null
                }
            }

            dataSetObserver?.let { adapter.registerDataSetObserver(it) }
        }
    }

    override fun setOnScrollListener(listener: OnScrollListener) {
        onScrollListener = listener
    }

    override fun setOnGroupClickListener(onGroupClickListener: OnGroupClickListener) {
        super.setOnGroupClickListener(onGroupClickListener)
        this.onGroupClickListener = onGroupClickListener
    }

    private fun createFloatingGroupView(position: Int) {
        floatingGroupView = null
        floatingGroupPosition = getPackedPositionGroup(getExpandableListPosition(position))
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val tag = child.getTag(R.id.fgelv_tag_changed_visibility)
            if (tag is Boolean) {
                if (tag) {
                    child.visibility = View.VISIBLE
                    child.setTag(R.id.fgelv_tag_changed_visibility, null)
                }
            }
        }
        if (!floatingGroupEnabled) {
            return
        }
        val floatingGroupFlatPosition = getFlatListPosition(getPackedPositionForGroup(floatingGroupPosition))
        val floatingGroupListPosition = floatingGroupFlatPosition - position
        if (floatingGroupListPosition in 0 until childCount) {
            val currentGroupView = getChildAt(floatingGroupListPosition)
            if (currentGroupView.top >= paddingTop) {
                return
            } else if (currentGroupView.top < paddingTop && currentGroupView.visibility == View.VISIBLE) {
                currentGroupView.visibility = View.INVISIBLE
                currentGroupView.setTag(R.id.fgelv_tag_changed_visibility, true)
            }
        }
        if (floatingGroupPosition >= 0) {
            floatingGroupView = adapter!!.getGroupView(floatingGroupPosition, adapter!!.isGroupExpanded(floatingGroupPosition), floatingGroupView, this)
            if (!floatingGroupView!!.isClickable) {
                selectorEnabled = true
                floatingGroupView!!.setOnClickListener { postDelayed(onClickAction, ViewConfiguration.getPressedStateDuration().toLong()) }
            } else {
                selectorEnabled = false
            }
            loadAttachInfo()
            setAttachInfo(floatingGroupView)
        }
        if (floatingGroupView == null) {
            return
        }
        val params = floatingGroupView!!.layoutParams as? LayoutParams?
        val childWidthSpec = ViewGroup.getChildMeasureSpec(
                widthMeasureSpec, paddingLeft + paddingRight, params?.width ?: 0)
        val paramsHeight = params?.height ?: 0
        val childHeightSpec: Int
        childHeightSpec = if (paramsHeight > 0) {
            MeasureSpec.makeMeasureSpec(paramsHeight, MeasureSpec.EXACTLY)
        } else {
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        }
        floatingGroupView!!.measure(childWidthSpec, childHeightSpec)
        var floatingGroupScrollY = 0
        val nextGroupFlatPosition = getFlatListPosition(getPackedPositionForGroup(floatingGroupPosition + 1))
        val nextGroupListPosition = nextGroupFlatPosition - position
        if (nextGroupListPosition in 0 until childCount) {
            val nextGroupView = getChildAt(nextGroupListPosition)
            if (nextGroupView != null && nextGroupView.top < paddingTop + floatingGroupView!!.measuredHeight + dividerHeight) {
                floatingGroupScrollY = nextGroupView.top - (paddingTop + floatingGroupView!!.measuredHeight + dividerHeight)
            }
        }
        val left = paddingLeft
        val top = paddingTop + floatingGroupScrollY
        val right = left + floatingGroupView!!.measuredWidth
        val bottom = top + floatingGroupView!!.measuredHeight
        floatingGroupView!!.layout(left, top, right, bottom)
        val mFloatingGroupScrollY = floatingGroupScrollY
        if (onScrollFloatingGroupListener != null) {
            onScrollFloatingGroupListener!!.onScrollFloatingGroupListener(floatingGroupView, mFloatingGroupScrollY)
        }
    }

    private fun loadAttachInfo() {
        if (viewAttachInfo == null) {
            viewAttachInfo = getFieldValue(View::class.java, "mAttachInfo", this@FloatingGroupExpandableListView)
        }
    }

    private fun setAttachInfo(v: View?) {
        if (v == null) {
            return
        }
        if (viewAttachInfo != null) {
            setFieldValue(View::class.java, "mAttachInfo", v, viewAttachInfo)
        }
        if (v is ViewGroup) {
            for (i in 0 until v.childCount) {
                setAttachInfo(v.getChildAt(i))
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun positionSelectorOnFloatingGroup() {
        if (shouldPositionSelector && floatingGroupView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                val floatingGroupFlatPosition = getFlatListPosition(getPackedPositionForGroup(floatingGroupPosition))
                invokeMethod(AbsListView::class.java, "positionSelector", arrayOf(Int::class.javaPrimitiveType, View::class.java), this@FloatingGroupExpandableListView, floatingGroupFlatPosition, floatingGroupView)
            } else {
                invokeMethod(AbsListView::class.java, "positionSelector", arrayOf(View::class.java), this@FloatingGroupExpandableListView, floatingGroupView)
            }
            invalidate()
        }
        shouldPositionSelector = false
        removeCallbacks(positionSelectorOnTapAction)
    }

    private fun drawDefaultSelector(canvas: Canvas) {
        val selectorListPosition = selectorPosition - firstVisiblePosition
        if (selectorListPosition in 0 until childCount && !selectorRect.isEmpty) {
            val floatingGroupFlatPosition = getFlatListPosition(getPackedPositionForGroup(floatingGroupPosition))
            if (floatingGroupView == null || selectorPosition != floatingGroupFlatPosition) {
                drawSelector(canvas)
            }
        }
    }

    private fun drawFloatingGroupSelector(canvas: Canvas) {
        if (!selectorRect.isEmpty) {
            val floatingGroupFlatPosition = getFlatListPosition(getPackedPositionForGroup(floatingGroupPosition))
            if (selectorPosition == floatingGroupFlatPosition) {
                selectorRect[floatingGroupView!!.left, floatingGroupView!!.top, floatingGroupView!!.right] = floatingGroupView!!.bottom
                drawSelector(canvas)
            }
        }
    }

    private fun drawSelector(canvas: Canvas) {
        canvas.save()
        canvas.clipRect(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom)
        if (isPressed) {
            customSelector!!.state = drawableState
        } else {
            customSelector!!.state = EMPTY_STATE_SET
        }
        customSelector!!.bounds = selectorRect
        customSelector!!.draw(canvas)
        canvas.restore()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun drawFloatingGroupIndicator(canvas: Canvas) {
        val groupIndicator = getFieldValue(ExpandableListView::class.java, "mGroupIndicator", this@FloatingGroupExpandableListView) as Drawable?
        if (groupIndicator != null) {
            val stateSetIndex = (if (adapter!!.isGroupExpanded(floatingGroupPosition)) 1 else 0) or  // Expanded?
                    if (adapter!!.getChildrenCount(floatingGroupPosition) > 0) 2 else 0 // Empty?
            groupIndicator.state = GROUP_STATE_SETS[stateSetIndex]
            val indicatorLeft = getFieldValue(ExpandableListView::class.java, "mIndicatorLeft", this@FloatingGroupExpandableListView) as Int
            val indicatorRight = getFieldValue(ExpandableListView::class.java, "mIndicatorRight", this@FloatingGroupExpandableListView) as Int
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                indicatorRect[indicatorLeft + paddingLeft, floatingGroupView!!.top, indicatorRight + paddingLeft] = floatingGroupView!!.bottom
            } else {
                indicatorRect[indicatorLeft, floatingGroupView!!.top, indicatorRight] = floatingGroupView!!.bottom
            }
            groupIndicator.bounds = indicatorRect
            groupIndicator.draw(canvas)
        }
    }

    interface OnScrollFloatingGroupListener {
        fun onScrollFloatingGroupListener(floatingGroupView: View?, scrollY: Int)
    }

    companion object {
        private val EMPTY_STATE_SET = intArrayOf()
        // State indicating the group is expanded
        private val GROUP_EXPANDED_STATE_SET = intArrayOf(android.R.attr.state_expanded)
        // State indicating the group is empty (has no children)
        private val GROUP_EMPTY_STATE_SET = intArrayOf(android.R.attr.state_empty)
        // State indicating the group is expanded and empty (has no children)
        private val GROUP_EXPANDED_EMPTY_STATE_SET = intArrayOf(android.R.attr.state_expanded, android.R.attr.state_empty)
        // States for the group where the 0th bit is expanded and 1st bit is empty.
        private val GROUP_STATE_SETS = arrayOf(
                EMPTY_STATE_SET,  // 00
                GROUP_EXPANDED_STATE_SET,  // 01
                GROUP_EMPTY_STATE_SET,  // 10
                GROUP_EXPANDED_EMPTY_STATE_SET // 11
        )
    }
}