package datatouch.uikit.components.emptystateview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import datatouch.uikit.R
import datatouch.uikit.core.callbacks.UiCallback
import datatouch.uikit.core.callbacks.UiJustCallback
import datatouch.uikit.core.extensions.ConditionsExtensions.isNotNull
import datatouch.uikit.core.extensions.TypedArrayExtensions.getAppCompatDrawable
import datatouch.uikit.core.utils.Conditions
import datatouch.uikit.core.utils.ResourceUtils
import datatouch.uikit.core.utils.views.ViewUtils
import datatouch.uikit.databinding.EmptyStateViewBinding

class EmptyStateView : RelativeLayout {

    private val ui = EmptyStateViewBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var detailedLoadingInfo = true
    private var stateTextColor = DefaultTextColor

    private var dimTitle: String = ""

    private var emptyStateTitle: String = ""
    private var emptyStateSubTitle: String = ""

    private var emptyStateDrawable: Drawable? = null

    private var loadingStateTitle: String = ""
    private var loadingStateSubTitle: String = ""

    private var actionButtonVisible = false
    private var actionButtonText: String = ""

    private var initialState = State.Loading
    private var addedContainer: View? = null

    private var layoutWidth = 0
    private var layoutHeight = 0

    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        parseAttributes(attrs)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        applyNativeAttributes()
        setupDimViewClickListener()
        setupInitialState()
        setupTextColor()
        setupEmptyStateTitle()
        setupEmptyStateSubTitle()
        setupEmptyStateImage()
        setupLoadingStateTitle()
        setupLoadingStateSubTitle()
        setupActionButtonVisibility()
        setupActionButtonText()
        setupContainerView()
        setupDimTitle()
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        val parsedNativeAttributes = ViewUtils.parseNativeAttributes(context, attrs)
        layoutHeight = parsedNativeAttributes.layoutHeight
        layoutWidth = parsedNativeAttributes.layoutWidth
        parseCustomAttributes(attrs)
    }

    private fun parseCustomAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.EmptyStateView, 0, 0)
        try {
            detailedLoadingInfo =
                typedArray.getBoolean(R.styleable.EmptyStateView_detailed_loading_info, true)

            initialState = State.fromValue(typedArray.getInt(R.styleable.EmptyStateView_initial_state,
                    State.Loading.value))

            stateTextColor =
                typedArray.getColor(R.styleable.EmptyStateView_state_text_color, DefaultTextColor)

            dimTitle = typedArray.getString(R.styleable.EmptyStateView_dim_title).orEmpty()

            emptyStateTitle = typedArray.getString(R.styleable.EmptyStateView_empty_state_title).orEmpty()
            emptyStateSubTitle =
                typedArray.getString(R.styleable.EmptyStateView_empty_state_subtitle).orEmpty()

            emptyStateDrawable =
                typedArray.getAppCompatDrawable(context, R.styleable.EmptyStateView_empty_state_drawable)

            loadingStateTitle = typedArray.getString(R.styleable.EmptyStateView_loading_state_title).orEmpty()
            loadingStateSubTitle =
                typedArray.getString(R.styleable.EmptyStateView_loading_state_subtitle).orEmpty()

            actionButtonVisible =
                typedArray.getBoolean(R.styleable.EmptyStateView_action_button_visible, false)
            actionButtonText = typedArray.getString(R.styleable.EmptyStateView_action_button_text).orEmpty()
        } finally {
            typedArray.recycle()
        }
    }

    private fun applyNativeAttributes() {
        applyLayoutParams()
    }

    private fun applyLayoutParams() {
        ui.rlEsvRoot.layoutParams?.width =
            if (layoutWidth < 0) layoutWidth
            else ResourceUtils.convertDpToPixel(context, layoutWidth.toFloat()).toInt()
        ui.rlEsvRoot.layoutParams?.height =
            if (layoutHeight < 0) layoutHeight
            else ResourceUtils.convertDpToPixel(context, layoutHeight.toFloat()).toInt()
    }

    private fun setupDimViewClickListener() = ui.rlDim.setOnClickListener { }

    private fun setupInitialState() {
        when (initialState) {
            State.Loading -> showLoading()
            State.Empty -> showEmpty()
            State.Container -> showContainer()
        }
    }

    val isLoadingState get() = ui.svLoadingState.visibility == View.VISIBLE

    fun showLoading() {
        ui.svLoadingState.visibility = View.VISIBLE
        ui.flContainer.visibility = View.GONE
        ui.svEmptyState.visibility = View.GONE
        if (!detailedLoadingInfo) {
            ui.circularProgressBar.visibility = View.GONE
            ui.tvLoadingStateSubTitle.visibility = View.GONE
            ui.tvLoadingStateTitle.visibility = View.GONE
            ui.flProgress.visibility = View.VISIBLE
        }
    }

    fun showEmpty() {
        ui.svLoadingState.visibility = View.GONE
        ui.flContainer.visibility = View.GONE
        ui.svEmptyState.visibility = View.VISIBLE
    }

    fun showContainer() {
        ui.svLoadingState.visibility = View.GONE
        ui.flContainer.visibility = View.VISIBLE
        ui.svEmptyState.visibility = View.GONE
    }

    fun setupTextColor() {
        if (stateTextColor != DefaultTextColor) setTextColor(stateTextColor)
    }

    fun setTextColor(color: Int) {
        ui.tvEmptyStateSubTitle.setTextColor(color)
        ui.tvEmptyStateTitle.setTextColor(color)
        ui.tvLoadingStateSubTitle.setTextColor(color)
        ui.tvLoadingStateTitle.setTextColor(color)
    }

    private fun setupEmptyStateImage() = setEmptyStateImage(emptyStateDrawable)

    fun setEmptyStateImage(drawable: Drawable?) {
        ui.ivEmptyStatePicture.setImageDrawable(drawable)
        ui.ivEmptyStatePicture.isVisible = Conditions.isNotNull(drawable)
    }

    fun setupEmptyStateTitle() = setEmptyStateTitle(emptyStateTitle)

    fun setEmptyStateTitle(title: String?) {
        ui.tvEmptyStateTitle.text = title
        ui.tvEmptyStateTitle.isVisible = Conditions.isNotNull(title)
    }

    fun setupEmptyStateSubTitle() = setEmptyStateSubTitle(emptyStateSubTitle)

    fun setEmptyStateSubTitle(subTitle: String?) {
        ui.tvEmptyStateSubTitle.text = subTitle
        ui.tvEmptyStateSubTitle.isVisible = Conditions.isNotNull(subTitle)
    }

    fun setEmptyStateActionButton(actionTitle: String?, clickListener: UiCallback<View?>) {
        ui.btnEmptyStateAction.setOnClickListener(clickListener)
        ui.btnEmptyStateAction.setText(actionTitle)
        ui.btnEmptyStateAction.isVisible = Conditions.isNotNull(actionTitle)
    }

    fun setupActionButtonText() = setEmptyStateActionButton(actionButtonText)

    fun setEmptyStateActionButton(actionTitle: String?) {
        ui.btnEmptyStateAction.setText(actionTitle)
    }

    fun onActionButtonClick(onClick: UiJustCallback) {
        ui.btnEmptyStateAction.setOnClickListener { onClick.invoke() }
    }

    private fun setupActionButtonVisibility() =
        setEmptyStateActionButtonVisibility(actionButtonVisible)

    fun hideEmptyStateActionButton() {
        setEmptyStateActionButtonVisibility(false)
    }

    fun showEmptyStateActionButton() {
        setEmptyStateActionButtonVisibility(true)
    }

    fun setEmptyStateActionButtonVisibility(isVisible: Boolean) {
        ui.btnEmptyStateAction.isVisible = isVisible
    }

    fun setupLoadingStateTitle() = setLoadingStateTitle(loadingStateTitle)

    fun setLoadingStateTitle(title: String?) {
        ui.tvLoadingStateTitle.text = title
        if (detailedLoadingInfo) {
            ui.tvLoadingStateTitle.isVisible = title.isNotNull()
        }
    }

    fun setupLoadingStateSubTitle() = setLoadingStateSubTitle(loadingStateSubTitle)

    fun setLoadingStateSubTitle(subTitle: String?) {
        ui.tvLoadingStateSubTitle.text = subTitle
        if (detailedLoadingInfo) {
            ui.tvLoadingStateSubTitle.isVisible = subTitle.isNotNull()
        }
    }

    private fun setupContainerView() {
        if (addedContainer != null)
            setContainerView(addedContainer)
    }


    fun setContainerView(containerView: View?) {
        ui.llListContainer.removeAllViews()
        try { // View may already exist in parent container or at leas view has a reference to the parent (memory leak btw)
//            if (isViewAddedToParent(containerView, llListContainer))
//                tryToRemoveViewFromParent(containerView)

            ui.llListContainer.addView(
                containerView, LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun isViewAddedToParent(view: View?, viewGroup: ViewGroup): Boolean {
        val existing = viewGroup.findViewById<View>(view?.id ?: 0)
        if (null == existing) {
            val parent = view?.parent as ViewGroup
            return Conditions.isNotNull(parent)
        }
        return true
    }

    fun show(isEmpty: Boolean) {
        if (isEmpty) showEmpty() else showContainer()
    }

    fun showOrLoading(hasContent: Boolean) {
        if (hasContent) showContainer() else showLoading()
    }

    val container: ViewGroup
        get() = ui.llListContainer

    private fun setupDimTitle() {
        if (Conditions.isNotNullOrEmpty(dimTitle.orEmpty()))
            setDimTitle(dimTitle)
    }

    fun setDimTitle(title: String?) {
        ui.tvDimStateTitle.text = title
        ui.tvDimStateTitle.isVisible = Conditions.isNotNull(title)
    }

    fun dimContainer(dim: Boolean) {
        ui.rlDim.isVisible = dim
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        val id = child?.id ?: 0
        if (R.id.rlEsvRoot == id)
            super.addView(child, index, params)
        else
            addedContainer = child
    }

}

private const val DefaultTextColor = Int.MIN_VALUE
