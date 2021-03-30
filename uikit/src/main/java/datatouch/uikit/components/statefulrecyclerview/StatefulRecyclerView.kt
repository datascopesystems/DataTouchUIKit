package datatouch.uikit.components.statefulrecyclerview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import datatouch.uikit.R
import datatouch.uikit.components.emptystateview.State
import datatouch.uikit.components.recyclerview.HorizontalLineDivider
import datatouch.uikit.components.recyclerview.RecyclerViewListAdapter
import datatouch.uikit.components.recyclerview.VerticalLineDivider
import datatouch.uikit.core.callbacks.UiCallback
import datatouch.uikit.core.callbacks.UiJustCallback
import datatouch.uikit.core.extensions.BooleanExtensions.ifYes
import datatouch.uikit.core.utils.Conditions
import datatouch.uikit.core.utils.ResourceUtils
import datatouch.uikit.core.utils.views.ViewUtils
import datatouch.uikit.databinding.StatefulRecyclerViewBinding


class StatefulRecyclerView : RelativeLayout {

    private val ui = StatefulRecyclerViewBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var detailedLoadingInfo = true
    private var stateTextColor = DefaultTextColor

    private var dimTitle: String? = null

    private var emptyStateTitle: String? = null
    private var emptyStateSubTitle: String? = null

    private var emptyStateDrawable: Drawable? = null

    private var loadingStateTitle: String? = null
    private var loadingStateSubTitle: String? = null

    private var actionButtonVisible = false
    private var actionButtonText: String? = null

    private var initialState = State.Loading

    private var layoutWidth = 0
    private var layoutHeight = 0

    private var layoutType = LayoutType.LinearVertical
    private var gridLayoutColumnsCount = DefaultGridLayoutColumnsType

    private var enableEmptyListFooter = true
    private var enableListLineDivider = true

    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        parseAttributes(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        parseAttributes(attrs)
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        val parsedNativeAttributes = ViewUtils.parseNativeAttributes(context, attrs)
        layoutHeight = parsedNativeAttributes.layoutHeight
        layoutWidth = parsedNativeAttributes.layoutWidth
        parseCustomAttributes(attrs)
    }

    private fun parseCustomAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(
            attrs,R.styleable.StatefulRecyclerView, 0, 0)
        try {
            detailedLoadingInfo = typedArray.getBoolean(
                    R.styleable.StatefulRecyclerView_srv_detailed_loading_info, true)

            initialState = State.fromValue(typedArray.getInt(
                R.styleable.StatefulRecyclerView_srv_initial_state,
                State.Loading.value))

            stateTextColor = typedArray.getColor(
                    R.styleable.StatefulRecyclerView_srv_state_text_color,
                    DefaultTextColor)

            dimTitle = typedArray.getString(R.styleable.StatefulRecyclerView_srv_dim_title)

            emptyStateTitle =
                typedArray.getString(R.styleable.StatefulRecyclerView_srv_empty_state_title)

            emptyStateSubTitle =
                typedArray.getString(R.styleable.StatefulRecyclerView_srv_empty_state_subtitle)

            emptyStateDrawable =
                typedArray.getDrawable(R.styleable.StatefulRecyclerView_srv_empty_state_drawable)

            loadingStateTitle =
                typedArray.getString(R.styleable.StatefulRecyclerView_srv_loading_state_title)

            loadingStateSubTitle =
                typedArray.getString(R.styleable.StatefulRecyclerView_srv_loading_state_subtitle)

            actionButtonVisible = typedArray.getBoolean(
                    R.styleable.StatefulRecyclerView_srv_action_button_visible,false)

            actionButtonText =
                typedArray.getString(R.styleable.StatefulRecyclerView_srv_action_button_text)

            layoutType = LayoutType.fromValue(typedArray.getInt(
                R.styleable.StatefulRecyclerView_srv_layout_type,
                LayoutType.LinearVertical.value))

            gridLayoutColumnsCount = typedArray.getInt(
                R.styleable.StatefulRecyclerView_srv_grid_layout_columns_count,
                DefaultGridLayoutColumnsType)

            enableEmptyListFooter = typedArray.getBoolean(
                R.styleable.StatefulRecyclerView_srv_enable_empty_list_footer, true)

            enableListLineDivider = typedArray.getBoolean(
                R.styleable.StatefulRecyclerView_srv_enable_list_line_divider, true)

        } finally {
            typedArray.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        applyNativeAttributes()
        setupInitialState()
        setupTextColor()
        setupEmptyStateTitle()
        setupEmptyStateSubTitle()
        setupEmptyStateImage()
        setupLoadingStateTitle()
        setupLoadingStateSubTitle()
        setupActionButtonVisibility()
        setupActionButtonText()
        setupDimTitle()
        initRv()
    }

    private fun applyNativeAttributes() {
        applyLayoutParams()
    }

    private fun applyLayoutParams() {
        ui.esv.layoutParams?.width =
            if (layoutWidth < 0) layoutWidth
            else ResourceUtils.convertDpToPixel(context, layoutWidth.toFloat()).toInt()
        ui.esv.layoutParams?.height =
            if (layoutHeight < 0) layoutHeight
            else ResourceUtils.convertDpToPixel(context, layoutHeight.toFloat()).toInt()
    }

    private fun setupInitialState() {
        when (initialState) {
            State.Loading -> showLoading()
            State.Empty -> showEmpty()
            State.Container -> showContainer()
        }
    }

    val isLoadingState get() = ui.esv.isLoadingState

    fun showLoading() = ui.esv.showLoading()

    fun showEmpty() = ui.esv.showEmpty()

    fun showContainer() = ui.esv.showContainer()

    fun setupTextColor() = setTextColor(stateTextColor)

    fun setTextColor(color: Int) {
        stateTextColor = color
        ui.esv.setTextColor(stateTextColor)
    }

    private fun setupEmptyStateImage() = setEmptyStateImage(emptyStateDrawable)

    fun setEmptyStateImage(drawable: Drawable?) {
        emptyStateDrawable = drawable
        ui.esv.setEmptyStateImage(drawable)
    }

    fun setupEmptyStateTitle() = setEmptyStateTitle(emptyStateTitle)

    fun setEmptyStateTitle(title: String?) {
        emptyStateTitle = title
        ui.esv.setEmptyStateTitle(title)
    }

    fun setupEmptyStateSubTitle() = setEmptyStateSubTitle(emptyStateSubTitle)

    fun setEmptyStateSubTitle(subTitle: String?) {
        emptyStateSubTitle = subTitle
        ui.esv.setEmptyStateSubTitle(emptyStateSubTitle)
    }

    fun setEmptyStateActionButton(actionTitle: String?, clickListener: UiCallback<View?>) {
        actionButtonText = actionTitle
        ui.esv.setEmptyStateActionButton(actionTitle, clickListener)
    }

    fun setupActionButtonText() = setEmptyStateActionButton(actionButtonText)

    fun setEmptyStateActionButton(actionTitle: String?) {
        actionButtonText = actionTitle
        ui.esv.setEmptyStateActionButton(actionTitle)
    }

    fun onActionButtonClick(onClick: UiJustCallback) = ui.esv.onActionButtonClick(onClick)

    private fun setupActionButtonVisibility() =
        setEmptyStateActionButtonVisibility(actionButtonVisible)

    fun hideEmptyStateActionButton() {
        setEmptyStateActionButtonVisibility(false)
    }

    fun showEmptyStateActionButton() {
        setEmptyStateActionButtonVisibility(true)
    }

    fun setEmptyStateActionButtonVisibility(isVisible: Boolean) {
        actionButtonVisible = isVisible
        ui.esv.setEmptyStateActionButtonVisibility(isVisible)
    }

    fun setupLoadingStateTitle() = setLoadingStateTitle(loadingStateTitle)

    fun setLoadingStateTitle(title: String?) {
        loadingStateTitle = title
        ui.esv.setLoadingStateTitle(title)
    }

    fun setupLoadingStateSubTitle() = setLoadingStateSubTitle(loadingStateSubTitle)

    fun setLoadingStateSubTitle(subTitle: String?) {
        loadingStateSubTitle = subTitle
        ui.esv.setLoadingStateSubTitle(subTitle)
    }

    fun show(isEmpty: Boolean) {
        if (isEmpty) showEmpty() else showContainer()
    }

    fun showOrLoading(hasContent: Boolean) {
        if (hasContent) showContainer() else showLoading()
    }

    private fun setupDimTitle() {
        if (Conditions.isNotNullOrEmpty(dimTitle))
            setDimTitle(dimTitle)
    }

    fun setDimTitle(title: String?) {
        dimTitle = title
        ui.esv.setDimTitle(title)
    }

    fun dimContainer(dim: Boolean) = ui.esv.dimContainer(dim)

    private fun initRv() {
        ui.rv.layoutManager = layoutManager
        addLineDividerIfRequired()
    }

    private fun addLineDividerIfRequired() = enableListLineDivider.ifYes {
        lineDivider?.let { ui.rv.addItemDecoration(it) }
    }

    private val lineDivider: RecyclerView.ItemDecoration?
        get() {
            return when (layoutType) {
                LayoutType.LinearVertical -> HorizontalLineDivider(context)
                LayoutType.LinearHorizontal -> VerticalLineDivider(context)
                LayoutType.Grid -> null
            }
        }

    private val layoutManager : RecyclerView.LayoutManager get() {
        return when(layoutType) {
            LayoutType.LinearVertical -> LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            LayoutType.LinearHorizontal -> LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            LayoutType.Grid -> GridLayoutManager(context, gridLayoutColumnsCount)
        }
    }

    var adapter : RecyclerViewListAdapter<*, *>
    get() = ui.rv.adapter as RecyclerViewListAdapter<*, *>
        set(value) {
            value.onDataItemsCountChangedCallback = ::onListDataItemsCountChanged
            ui.rv.adapter = value
            addEmptyListFooterIfRequired(value)
            onListDataItemsCountChanged(adapter.dataItemCount)
        }

    private fun addEmptyListFooterIfRequired(adapter: RecyclerViewListAdapter<*, *>) =
        enableEmptyListFooter.ifYes {
            adapter.setFooterView(RecyclerViewListAdapter.getDefaultListFooter(context))
        }

    private fun onListDataItemsCountChanged(dataItemsCount: Int) {
        ui.esv.show(dataItemsCount < 1)
    }

    fun scrollToPosition(position: Int) = ui.rv.scrollToPosition(position)

}

private const val DefaultTextColor = Color.WHITE
private const val DefaultGridLayoutColumnsType = 2
