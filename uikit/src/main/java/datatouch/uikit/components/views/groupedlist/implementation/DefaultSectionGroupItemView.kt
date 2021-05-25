package datatouch.uikit.components.views.groupedlist.implementation

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import datatouch.uikit.R
import datatouch.uikit.components.views.groupedlist.SectionGroupItemView
import datatouch.uikit.components.views.groupedlist.SectionGroupItemView.OnSectionItemClickCallback
import datatouch.uikit.components.views.groupedlist.viewmodels.SectionGroupItemViewModel
import datatouch.uikit.core.utils.Conditions
import datatouch.uikit.databinding.SectionGroupItemViewBinding

class DefaultSectionGroupItemView : RelativeLayout, SectionGroupItemView {

    private val ui = SectionGroupItemViewBinding
        .inflate(LayoutInflater.from(context), this, true)

    var callback: OnSectionItemClickCallback? = null
        private set
    private var viewModel: SectionGroupItemViewModel? = null
    private var isSelectedItem = false

    constructor(context: Context?, callback: OnSectionItemClickCallback?) : this(
        context
    ) {
        this.callback = callback
    }

    constructor(context: Context?) : super(context) {
        inflateView()
        afterViews()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        inflateView()
        afterViews()
    }

    fun setupItem(viewModel: SectionGroupItemViewModel) {
        this.viewModel = viewModel
        ui.tvName.text = viewModel.name
        ui.tvDescription.text = viewModel.description
        ui.ivIcon.setImageResource(viewModel.iconResourceId)
        setChecked(false)
    }

    fun setChecked(checked: Boolean) {
        isSelectedItem = checked
        ui.tvName.setTextColor(
            if (isSelectedItem) ContextCompat.getColor(context, R.color.accent_end) else ContextCompat.getColor(context, R.color.white))
        ui.ivIcon.setBackgroundResource(if (isSelectedItem) R.drawable.image_background_circle_accent else 0)
        ui.vPolygon.visibility = if (isSelectedItem) View.VISIBLE else View.INVISIBLE
    }

    fun rlRoot() {
        if (Conditions.isNotNull(callback)) {
            callback?.onItemClick(viewModel)
        }
    }

    fun afterViews() {
        ui.rlRoot.setOnClickListener { rlRoot() }
    }

    override fun setSelectedState() {
        setChecked(true)
    }

    override fun setNormalState() {
        setChecked(false)
    }

    override fun getViewModelId(): Int {
        return viewModel!!.id
    }

    protected fun inflateView() {
        View.inflate(context, R.layout.section_group_item_view, this)

    }

    override fun setOnItemClickListener(onItemClickCallback: OnSectionItemClickCallback) {
        callback = onItemClickCallback
    }
}