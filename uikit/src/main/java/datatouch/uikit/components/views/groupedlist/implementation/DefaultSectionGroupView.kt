package datatouch.uikit.components.views.groupedlist.implementation

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RelativeLayout
import datatouch.uikit.R
import datatouch.uikit.components.views.groupedlist.SectionGroupItemView
import datatouch.uikit.components.views.groupedlist.SectionGroupView
import datatouch.uikit.components.views.groupedlist.SectionGroupView.OnGroupItemClickListener
import datatouch.uikit.components.views.groupedlist.viewmodels.SectionGroupItemViewModel
import datatouch.uikit.components.views.groupedlist.viewmodels.SectionGroupViewModel
import datatouch.uikit.core.utils.Conditions
import datatouch.uikit.databinding.SectionGroupBinding

class DefaultSectionGroupView : RelativeLayout, SectionGroupView,
    SectionGroupItemView.OnSectionItemClickCallback {

    private val ui = SectionGroupBinding
        .inflate(LayoutInflater.from(context), this)

    private var onGroupItemClickListener: OnGroupItemClickListener? = null
    private var groupViewModel: SectionGroupViewModel? = null

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

    override fun setGroupViewModel(groupViewModel: SectionGroupViewModel?) {
        this.groupViewModel = groupViewModel
    }

    override fun renderNewViewModel() {
        ui.llItemsContainer.removeAllViews()
        ui.tvHeaderTitle.setText(groupViewModel?.title)
        val iconResource: Int = groupViewModel!!.iconResourceId
        if (iconResource > 0) {
            ui.ivHeaderIcon.setImageResource(iconResource)
        }
        for (itemViewModel in groupViewModel!!.items!!) {
            val itemView =
                DefaultSectionGroupItemView(context)
            itemView.setupItem(itemViewModel)
            itemView.setOnItemClickListener(this)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            ui.llItemsContainer.addView(itemView, params)
        }
    }

    fun getOnGroupItemClickListener(): OnGroupItemClickListener? {
        return onGroupItemClickListener
    }

    override fun setOnGroupItemClickListener(onGroupItemClickListener: OnGroupItemClickListener?) {
        this.onGroupItemClickListener = onGroupItemClickListener
    }

    override fun setSelectedInGroupItemState(itemId: Int) {
        ui.rlGroupHeader.setBackgroundResource(R.drawable.bg_rounded_top_group_selected)
        for (i in 0 until ui.llItemsContainer.childCount) {
            val v = ui.llItemsContainer.getChildAt(i)
            val sectionGroupItem: SectionGroupItemView = v as SectionGroupItemView
            if (sectionGroupItem.viewModelId === itemId) {
                sectionGroupItem.setSelectedState()
            } else {
                sectionGroupItem.setNormalState()
            }
        }
    }

    override fun setNormalState() {
        ui.rlGroupHeader.setBackgroundResource(R.drawable.bg_rounded_top_group)
        for (i in 0 until ui.llItemsContainer.childCount) {
            val v = ui.llItemsContainer.getChildAt(i)
            val sectionGroupItem: SectionGroupItemView = v as SectionGroupItemView
            sectionGroupItem.setNormalState()
        }
    }

    override val viewModelId: Int
        get() = groupViewModel!!.id

    override fun onItemClick(viewModel: SectionGroupItemViewModel) {
        if (Conditions.isNotNull(onGroupItemClickListener)) {
            onGroupItemClickListener?.onGroupItemClicked(
                groupViewModel!!.id,
                viewModel.id
            )
        }
    }
}