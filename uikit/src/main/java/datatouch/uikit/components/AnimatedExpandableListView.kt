package datatouch.uikit.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import java.util.*

class AnimatedExpandableListView : ExpandableListView {
    private var adapter: AnimatedExpandableListAdapter? = null

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
    }

    /**
     * @see ExpandableListView.setAdapter
     */
    override fun setAdapter(adapter: ExpandableListAdapter) {
        super.setAdapter(adapter)
        setGroupIndicator(null)
        // Make sure that the adapter extends AnimatedExpandableListAdapter
        if (adapter is AnimatedExpandableListAdapter) {
            this.adapter = adapter
            this.adapter?.setParent(this)
        } else {
            throw ClassCastException("$adapter must implement AnimatedExpandableListAdapter")
        }
    }

    /**
     * Expands the given group with an animation.
     *
     * @param groupPos The position of the group to expand
     * @return Returns true if the group was expanded. False if the group was
     * already expanded.
     */
    @SuppressLint("NewApi")
    fun expandGroupWithAnimation(groupPos: Int): Boolean {
        val lastGroup = groupPos == adapter!!.groupCount - 1
        if (lastGroup) {
            return expandGroup(groupPos, true)
        }
        val groupFlatPos =
            getFlatListPosition(getPackedPositionForGroup(groupPos))
        if (groupFlatPos != -1) {
            val childIndex = groupFlatPos - firstVisiblePosition
            if (childIndex < childCount) {
                // Get the view for the group is it is on screen...
                val v = getChildAt(childIndex) ?: return expandGroup(groupPos)
                if (v.bottom >= bottom) {
                    // If the user is not going to be able to see the animation
                    // we just expand the group without an animation.
                    // This resolves the case where getChildView will not be
                    // called if the children of the group is not on screen

                    // We need to notify the adapter that the group was expanded
                    // without it's knowledge
                    adapter!!.notifyGroupExpanded(groupPos)
                    return expandGroup(groupPos)
                }
            }
        }

        // Let the adapter know that we are starting the animation...
        adapter?.startExpandAnimation(groupPos, 0)
        // Finally call expandGroup (note that expandGroup will call
        // notifyDataSetChanged so we don't need to)
        return expandGroup(groupPos)
    }

    /**
     * Collapses the given group with an animation.
     *
     * @param groupPos The position of the group to collapse
     * @return Returns true if the group was collapsed. False if the group was
     * already collapsed.
     */
    fun collapseGroupWithAnimation(groupPos: Int): Boolean {
        val groupFlatPos =
            getFlatListPosition(getPackedPositionForGroup(groupPos))
        if (groupFlatPos != -1) {
            val childIndex = groupFlatPos - firstVisiblePosition
            if (childIndex >= 0 && childIndex < childCount) {
                // Get the view for the group is it is on screen...
                val v = getChildAt(childIndex)
                if (v.bottom >= bottom) {
                    // If the user is not going to be able to see the animation
                    // we just collapse the group without an animation.
                    // This resolves the case where getChildView will not be
                    // called if the children of the group is not on screen
                    return collapseGroup(groupPos)
                }
            } else {
                // If the group is offscreen, we can just collapse it without an
                // animation...
                return collapseGroup(groupPos)
            }
        }

        // Get the position of the firstChild visible from the top of the screen
        val packedPos = getExpandableListPosition(firstVisiblePosition)
        var firstChildPos = getPackedPositionChild(packedPos)
        val firstGroupPos = getPackedPositionGroup(packedPos)

        // If the first visible view on the screen is a child view AND it's a
        // child of the group we are trying to collapse, then set that
        // as the first child position of the group... see
        // {@link #startCollapseAnimation(int, int)} for why this is necessary
        firstChildPos = if (firstChildPos == -1 || firstGroupPos != groupPos) 0 else firstChildPos

        // Let the adapter know that we are going to start animating the
        // collapse animation.
        adapter?.startCollapseAnimation(groupPos, firstChildPos)

        // Force the listview to setHotspots it's views
        adapter?.notifyDataSetChanged()
        return isGroupExpanded(groupPos)
    }

    /**
     * Used for holding information regarding the group.
     */
    private class GroupInfo {
        var animating = false
        var expanding = false
        var firstChildPosition = 0

        /**
         * This variable contains the last known height value of the dummy view.
         * We save this information so that if the user collapses a group
         * before it fully expands, the collapse animation will start from the
         * CURRENT height of the dummy view and not from the full expanded
         * height.
         */
        var dummyHeight = -1
    }

    /**
     * A specialized adapter for use with the AnimatedExpandableListView. All
     * adapters used with AnimatedExpandableListView MUST extend this class.
     */
    abstract class AnimatedExpandableListAdapter : BaseExpandableListAdapter() {
        private val groupInfo = SparseArray<GroupInfo>()
        private var parent: AnimatedExpandableListView? = null
        fun setParent(parent: AnimatedExpandableListView) {
            this.parent = parent
        }

        fun getRealChildType(groupPosition: Int, childPosition: Int): Int {
            return 0
        }

        val realChildTypeCount: Int
            get() = 1

        abstract fun getRealChildView(
            groupPosition: Int,
            childPosition: Int,
            isLastChild: Boolean,
            convertView: View?,
            parent: ViewGroup?
        ): View

        abstract fun getRealChildrenCount(groupPosition: Int): Int
        private fun getGroupInfo(groupPosition: Int): GroupInfo {
            var info = groupInfo[groupPosition]
            if (info == null) {
                info = GroupInfo()
                groupInfo.put(groupPosition, info)
            }
            return info
        }

        fun notifyGroupExpanded(groupPosition: Int) {
            val info = getGroupInfo(groupPosition)
            info.dummyHeight = -1
        }

        fun startExpandAnimation(groupPosition: Int, firstChildPosition: Int) {
            val info = getGroupInfo(groupPosition)
            info.animating = true
            info.firstChildPosition = firstChildPosition
            info.expanding = true
        }

        fun startCollapseAnimation(
            groupPosition: Int,
            firstChildPosition: Int
        ) {
            val info = getGroupInfo(groupPosition)
            info.animating = true
            info.firstChildPosition = firstChildPosition
            info.expanding = false
        }

        private fun stopAnimation(groupPosition: Int) {
            val info = getGroupInfo(groupPosition)
            info.animating = false
        }

        /**
         * Override [.getRealChildType] instead.
         */
        override fun getChildType(groupPosition: Int, childPosition: Int): Int {
            val info = getGroupInfo(groupPosition)
            return if (info.animating) {
                // If we are animating this group, then all of it's children
                // are going to be dummy views which we will say is type 0.
                0
            } else {
                // If we are not animating this group, then we will add 1 to
                // the type it has so that no type moduleId conflicts will occur
                // unless getRealChildType() returns MAX_INT
                getRealChildType(groupPosition, childPosition) + 1
            }
        }

        /**
         * Override [.getRealChildTypeCount] instead.
         */
        override fun getChildTypeCount(): Int {
            // Return 1 more than the childTypeCount to account for DummyView
            return realChildTypeCount + 1
        }

        protected fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
            return LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0
            )
        }

        /**
         * Override [.getChildView] instead.
         */
        override fun getChildView(
            groupPosition: Int,
            childPosition: Int,
            isLastChild: Boolean,
            convertView: View,
            parent: ViewGroup
        ): View {
            var convertView = convertView
            val info = getGroupInfo(groupPosition)
            return if (info.animating) {
                // If this group is animating, return the a DummyView...
                if (convertView !is DummyView) {
                    convertView = DummyView(parent.context)
                    convertView.setLayoutParams(
                        LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            0
                        )
                    )
                }
                if (childPosition < info.firstChildPosition) {
                    // The reason why we do this is to support the collapse
                    // this group when the group view is not visible but the
                    // children of this group are. When notifyDataSetChanged
                    // is called, the ExpandableListView tries to keep the
                    // list position the same by saving the first visible item
                    // and jumping back to that item after the views have been
                    // refreshed. Now the problem is, if a group has 2 items
                    // and the first visible item is the 2nd child of the group
                    // and this group is collapsed, then the dummy view will be
                    // used for the group. But now the group only has 1 item
                    // which is the dummy view, thus when the ListView is trying
                    // to restore the scroll position, it will try to jump to
                    // the second item of the group. But this group no longer
                    // has a second item, so it is forced to jump to the next
                    // group. This will cause a very ugly visual glitch. So
                    // the way that we counteract this is by creating as many
                    // dummy views as we need to maintain the scroll position
                    // of the ListView after notifyDataSetChanged has been
                    // called.
                    convertView.getLayoutParams().height = 0
                    return convertView
                }
                val listView = parent as ExpandableListView
                val dummyView = convertView

                // Clear the views that the dummy view draws.
                dummyView.clearViews()

                // Set the style of the divider
                dummyView.setDivider(
                    listView.divider,
                    parent.getMeasuredWidth(),
                    listView.dividerHeight
                )

                // Make measure specs to measure child views
                val measureSpecW =
                    MeasureSpec.makeMeasureSpec(parent.getWidth(), MeasureSpec.EXACTLY)
                val measureSpecH = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                var totalHeight = 0
                val clipHeight = parent.getHeight()
                val len = getRealChildrenCount(groupPosition)
                for (i in info.firstChildPosition until len) {
                    val childView =
                        getRealChildView(groupPosition, i, i == len - 1, null, parent)
                    var p =
                        childView.layoutParams as LayoutParams
                    if (p == null) {
                        p = generateDefaultLayoutParams() as LayoutParams
                        childView.layoutParams = p
                    }
                    val lpHeight = p.height
                    var childHeightSpec: Int
                    childHeightSpec = if (lpHeight > 0) {
                        MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY)
                    } else {
                        measureSpecH
                    }
                    childView.measure(measureSpecW, childHeightSpec)
                    totalHeight += childView.measuredHeight
                    if (totalHeight < clipHeight) {
                        // we only need to draw enough views to fool the user...
                        dummyView.addFakeView(childView)
                    } else {
                        dummyView.addFakeView(childView)

                        // if this group has too many views, we don't want to
                        // calculate the height of everything... just do a light
                        // approximation and break
                        val averageHeight = totalHeight / (i + 1)
                        totalHeight += (len - i - 1) * averageHeight
                        break
                    }
                }
                var o: Any?
                val state = (if (dummyView.tag
                        .also { o = it } == null
                ) STATE_IDLE else o as Int?)!!
                if (info.expanding && state != STATE_EXPANDING) {
                    val ani = ExpandAnimation(dummyView, 0, totalHeight, info)
                    ani.duration = this.parent?.animation?.duration?.toLong() ?: 0
                    ani.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationEnd(animation: Animation) {
                            stopAnimation(groupPosition)
                            notifyDataSetChanged()
                            dummyView.tag = STATE_IDLE
                        }

                        override fun onAnimationRepeat(animation: Animation) {}
                        override fun onAnimationStart(animation: Animation) {}
                    })
                    dummyView.startAnimation(ani)
                    dummyView.tag = STATE_EXPANDING
                } else if (!info.expanding && state != STATE_COLLAPSING) {
                    if (info.dummyHeight == -1) {
                        info.dummyHeight = totalHeight
                    }
                    val ani = ExpandAnimation(dummyView, info.dummyHeight, 0, info)
                    ani.duration = this.parent?.animation?.duration?.toLong() ?: 0
                    ani.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationEnd(animation: Animation) {
                            stopAnimation(groupPosition)
                            listView.collapseGroup(groupPosition)
                            notifyDataSetChanged()
                            info.dummyHeight = -1
                            dummyView.tag = STATE_IDLE
                        }

                        override fun onAnimationRepeat(animation: Animation) {}
                        override fun onAnimationStart(animation: Animation) {}
                    })
                    dummyView.startAnimation(ani)
                    dummyView.tag = STATE_COLLAPSING
                }
                convertView
            } else {
                getRealChildView(
                    groupPosition,
                    childPosition,
                    isLastChild,
                    convertView,
                    parent
                )
            }
        }

        override fun getChildrenCount(groupPosition: Int): Int {
            val info = getGroupInfo(groupPosition)
            return if (info.animating) {
                info.firstChildPosition + 1
            } else {
                getRealChildrenCount(groupPosition)
            }
        }

        companion object {
            private const val STATE_IDLE = 0
            private const val STATE_EXPANDING = 1
            private const val STATE_COLLAPSING = 2
        }
    }

    private class DummyView(context: Context?) :
        View(context) {
        private val views: MutableList<View> =
            ArrayList()
        private var divider: Drawable? = null
        private var dividerWidth = 0
        private var dividerHeight = 0
        fun setDivider(divider: Drawable?, dividerWidth: Int, dividerHeight: Int) {
            if (divider != null) {
                this.divider = divider
                this.dividerWidth = dividerWidth
                this.dividerHeight = dividerHeight
                divider.setBounds(0, 0, dividerWidth, dividerHeight)
            }
        }

        /**
         * Add a view for the DummyView to draw.
         *
         * @param childView View to draw
         */
        fun addFakeView(childView: View) {
            childView.layout(0, 0, width, childView.measuredHeight)
            views.add(childView)
        }

        override fun onLayout(
            changed: Boolean,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int
        ) {
            super.onLayout(changed, left, top, right, bottom)
            val len = views.size
            for (i in 0 until len) {
                val v = views[i]
                v.layout(left, top, left + v.measuredWidth, top + v.measuredHeight)
            }
        }

        fun clearViews() {
            views.clear()
        }

        public override fun dispatchDraw(canvas: Canvas) {
            canvas.save()
            if (divider != null) {
                divider!!.setBounds(0, 0, dividerWidth, dividerHeight)
            }
            val len = views.size
            for (i in 0 until len) {
                val v = views[i]
                canvas.save()
                canvas.clipRect(0, 0, width, v.measuredHeight)
                v.draw(canvas)
                canvas.restore()
                if (divider != null) {
                    divider!!.draw(canvas)
                    canvas.translate(0f, dividerHeight.toFloat())
                }
                canvas.translate(0f, v.measuredHeight.toFloat())
            }
            canvas.restore()
        }
    }

    private class ExpandAnimation(
        v: View,
        private val baseHeight: Int,
        endHeight: Int,
        info: GroupInfo
    ) : Animation() {
        private val delta: Int
        private val view: View
        private val groupInfo: GroupInfo
        override fun applyTransformation(
            interpolatedTime: Float,
            t: Transformation
        ) {
            super.applyTransformation(interpolatedTime, t)
            if (interpolatedTime < 1.0f) {
                val `val` = baseHeight + (delta * interpolatedTime).toInt()
                view.layoutParams.height = `val`
                groupInfo.dummyHeight = `val`
                view.requestLayout()
            } else {
                val `val` = baseHeight + delta
                view.layoutParams.height = `val`
                groupInfo.dummyHeight = `val`
                view.requestLayout()
            }
        }

        init {
            delta = endHeight - baseHeight
            view = v
            groupInfo = info
            view.layoutParams.height = baseHeight
            view.requestLayout()
        }
    }

    companion object {
        private val TAG = AnimatedExpandableListAdapter::class.java.simpleName
        private const val animationDuration = 300
    }
}