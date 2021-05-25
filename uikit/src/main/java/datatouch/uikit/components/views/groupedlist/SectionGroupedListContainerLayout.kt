package datatouch.uikit.components.views.groupedlist

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import datatouch.uikit.components.views.groupedlist.SectionGroupView.OnGroupItemClickListener
import datatouch.uikit.components.views.groupedlist.implementation.DefaultGroupViewRenderer
import datatouch.uikit.components.views.groupedlist.viewmodels.SectionGroupViewModel
import datatouch.uikit.core.utils.Conditions.isNotNull
import datatouch.uikit.databinding.SectionGroupedListContainerBinding

class SectionGroupedListContainerLayout : ScrollView, OnGroupItemClickListener {

    private val ui = SectionGroupedListContainerBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var groupViewRenderer =
        DefaultGroupViewRenderer() as GroupViewRenderer<*>
    var onGroupItemClickListener: OnGroupItemClickListener? = null
    private var groups: List<SectionGroupViewModel>? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    fun setGroupViewRenderer(groupViewRenderer: GroupViewRenderer<*>) {
        this.groupViewRenderer = groupViewRenderer
    }

    protected fun renderNewGroups() {
        ui.llRootContainer.removeAllViews()
        for (groupModel in groups!!) {
            val groupView = groupViewRenderer.getViewForGroup(
                ui.llRootContainer,
                groupModel
            ) as SectionGroupView
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            groupView.setGroupViewModel(groupModel)
            groupView.setOnGroupItemClickListener(this)
            ui.llRootContainer.addView(groupView as View, params)
            groupView.renderNewViewModel()
        }
    }

    fun setGroups(groups: List<SectionGroupViewModel>?) {
        this.groups = groups
        renderNewGroups()
    }

    protected fun setGroupStateOnClick(groupId: Int, itemId: Int) {
        for (i in 0 until ui.llRootContainer.childCount) {
            val v = ui.llRootContainer.getChildAt(i)
            val sectionGroupView = v as SectionGroupView
            if (sectionGroupView.viewModelId == groupId) {
                sectionGroupView.setSelectedInGroupItemState(itemId)
            } else {
                sectionGroupView.setNormalState()
            }
        }
    }

    override fun onGroupItemClicked(groupId: Int, itemId: Int) {
        if (isNotNull(onGroupItemClickListener)) {
            onGroupItemClickListener?.onGroupItemClicked(groupId, itemId)
        }
        setGroupStateOnClick(groupId, itemId)
    }

    interface GroupViewRenderer<T> where T : View?, T : SectionGroupView? {
        fun getViewForGroup(
            parentViewGroup: ViewGroup?,
            groupViewModel: SectionGroupViewModel?
        ): T
    }
}