package datatouch.uikit.components.views.groupedlist.implementation

import android.view.ViewGroup
import datatouch.uikit.components.views.groupedlist.SectionGroupedListContainerLayout.GroupViewRenderer
import datatouch.uikit.components.views.groupedlist.viewmodels.SectionGroupViewModel

class DefaultGroupViewRenderer : GroupViewRenderer<DefaultSectionGroupView?> {
    override fun getViewForGroup(
        parentViewGroup: ViewGroup?,
        groupViewModel: SectionGroupViewModel?
    ): DefaultSectionGroupView {
        val sectionGroupView =
            DefaultSectionGroupView(parentViewGroup!!.context)
        sectionGroupView.setGroupViewModel(groupViewModel)
        return sectionGroupView
    }
}