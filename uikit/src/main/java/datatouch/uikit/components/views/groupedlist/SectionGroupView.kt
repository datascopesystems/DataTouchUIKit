package datatouch.uikit.components.views.groupedlist

import datatouch.uikit.components.views.groupedlist.viewmodels.SectionGroupViewModel

interface SectionGroupView {
    fun setGroupViewModel(groupViewModel: SectionGroupViewModel?)
    fun renderNewViewModel()
    fun setSelectedInGroupItemState(itemId: Int)
    fun setNormalState()
    val viewModelId: Int
    fun setOnGroupItemClickListener(onGroupItemClickListener: OnGroupItemClickListener?)
    interface OnGroupItemClickListener {
        fun onGroupItemClicked(groupId: Int, itemId: Int)
    }
}