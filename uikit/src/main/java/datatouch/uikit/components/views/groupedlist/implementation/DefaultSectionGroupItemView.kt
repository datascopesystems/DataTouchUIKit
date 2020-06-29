package datatouch.uikit.components.views.groupedlist.implementation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import datatouch.uikit.R
import datatouch.uikit.components.views.groupedlist.SectionGroupItemView
import datatouch.uikit.components.views.groupedlist.SectionGroupItemView.OnSectionItemClickCallback
import datatouch.uikit.components.views.groupedlist.viewmodels.SectionGroupItemViewModel
import datatouch.uikit.utils.Conditions
import kotlinx.android.synthetic.main.section_group_item_view.view.*

class DefaultSectionGroupItemView : RelativeLayout, SectionGroupItemView {

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
        tvName?.text = viewModel.name
        tvDescription?.text = viewModel.description
        ivIcon?.setImageResource(viewModel.iconResourceId)
        setChecked(false)
    }

    fun setChecked(checked: Boolean) {
        isSelectedItem = checked
        tvName?.setTextColor(
            if (isSelectedItem) context.resources.getColor(R.color.accent_end) else context.resources.getColor(
                R.color.white
            )
        )
        ivIcon?.setBackgroundResource(if (isSelectedItem) R.drawable.image_background_circle_accent else 0)
        vPolygon?.visibility = if (isSelectedItem) View.VISIBLE else View.INVISIBLE
    }

    fun rlRoot() {
        if (Conditions.isNotNull(callback)) {
            callback?.onItemClick(viewModel)
        }
    }

    fun afterViews() {
        rlRoot?.setOnClickListener { rlRoot() }
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