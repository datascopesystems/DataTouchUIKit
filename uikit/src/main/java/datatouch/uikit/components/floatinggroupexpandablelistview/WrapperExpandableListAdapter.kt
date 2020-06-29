package datatouch.uikit.components.floatinggroupexpandablelistview

import android.database.DataSetObserver
import android.util.SparseBooleanArray
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListAdapter
import datatouch.uikit.R

class WrapperExpandableListAdapter(private val wrappedAdapter: ExpandableListAdapter) : BaseExpandableListAdapter() {

    private val groupExpandedMap = SparseBooleanArray()
    private val observer: DataSetObserver? = null

    override fun registerDataSetObserver(observer: DataSetObserver) {
        wrappedAdapter.registerDataSetObserver(observer)
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver) {
        wrappedAdapter.unregisterDataSetObserver(observer)
    }

    fun unregisterDataSetObserver() {
        observer?.let { wrappedAdapter.unregisterDataSetObserver(observer) }
    }

    override fun getGroupCount(): Int {
        return wrappedAdapter.groupCount
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return wrappedAdapter.getChildrenCount(groupPosition)
    }

    override fun getGroup(groupPosition: Int): Any {
        return wrappedAdapter.getGroup(groupPosition)
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return wrappedAdapter.getChild(groupPosition, childPosition)
    }

    override fun getGroupId(groupPosition: Int): Long {
        return wrappedAdapter.getGroupId(groupPosition)
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return wrappedAdapter.getChildId(groupPosition, childPosition)
    }

    override fun hasStableIds(): Boolean {
        return wrappedAdapter.hasStableIds()
    }

    override fun getGroupView(groupPosition: Int,
                              isExpanded: Boolean,
                              convertView: View?,
                              parent: ViewGroup?): View {
        if (convertView != null) {
            val tag = convertView.getTag(R.id.fgelv_tag_changed_visibility)
            if (tag is Boolean) {
                if (tag) {
                    convertView.visibility = View.VISIBLE
                }
            }
            convertView.setTag(R.id.fgelv_tag_changed_visibility, null)
        }
        groupExpandedMap.put(groupPosition, isExpanded)
        return wrappedAdapter.getGroupView(groupPosition, isExpanded, convertView, parent)
    }

    override fun getChildView(groupPosition: Int,
                              childPosition: Int,
                              isLastChild: Boolean,
                              convertView: View?,
                              parent: ViewGroup?): View {
        return wrappedAdapter.getChildView(groupPosition, childPosition, isLastChild, convertView, parent)
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return wrappedAdapter.isChildSelectable(groupPosition, childPosition)
    }

    override fun areAllItemsEnabled(): Boolean {
        return wrappedAdapter.areAllItemsEnabled()
    }

    override fun isEmpty(): Boolean {
        return wrappedAdapter.isEmpty
    }

    override fun onGroupExpanded(groupPosition: Int) {
        groupExpandedMap.put(groupPosition, true)
        wrappedAdapter.onGroupExpanded(groupPosition)
    }

    override fun onGroupCollapsed(groupPosition: Int) {
        groupExpandedMap.put(groupPosition, false)
        wrappedAdapter.onGroupCollapsed(groupPosition)
    }

    override fun getCombinedGroupId(groupId: Long): Long {
        return wrappedAdapter.getCombinedGroupId(groupId)
    }

    override fun getCombinedChildId(groupId: Long, childId: Long): Long {
        return wrappedAdapter.getCombinedChildId(groupId, childId)
    }

    fun isGroupExpanded(groupPosition: Int): Boolean {
        return groupExpandedMap[groupPosition]
    }

}