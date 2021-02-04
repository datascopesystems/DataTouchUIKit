package datatouch.uikit.components.emptystateview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.StyleableRes
import androidx.core.view.isVisible
import datatouch.uikit.R
import datatouch.uikit.core.callbacks.UiCallback
import datatouch.uikit.core.callbacks.UiJustCallback
import datatouch.uikit.core.extensions.ConditionsExtensions.isNotNull
import datatouch.uikit.core.utils.Conditions
import datatouch.uikit.core.utils.ResourceUtils
import kotlinx.android.synthetic.main.empty_state_view.view.*

private const val DefaultTextColor = Int.MIN_VALUE

@SuppressLint("ViewConstructor")
class EmptyStateView : RelativeLayout {

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

    private var initialState = 0

    private var addedContainer: View? = null

    // Array of view ids added as part of this view
    private val addedViewIds = intArrayOf(R.id.rlEsvRoot)

    private var layoutWidth = 0
    private var layoutHeight = 0

    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet,
        defStyleAttr: Int,
        @Suppress("UNUSED_PARAMETER") defStyleRes: Int
    )
            : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        inflateView()
        parseAttributes(attrs)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        afterView()
    }

    private fun inflateView() = View.inflate(context, R.layout.empty_state_view, this)

    private fun parseAttributes(attrs: AttributeSet?) {
        parseNativeAttributes(attrs)
        parseCustomAttributes(attrs)
    }

    private fun parseNativeAttributes(attrs: AttributeSet?) {
        val attrIndexes = intArrayOf(
            android.R.attr.layout_width,
            android.R.attr.layout_height,
            android.R.attr.paddingLeft,
            android.R.attr.paddingTop,
            android.R.attr.paddingRight,
            android.R.attr.paddingBottom
        )
        val typedArray = context.obtainStyledAttributes(attrs, attrIndexes, 0, 0)
        try {
            @StyleableRes val widthIndex = 0
            @StyleableRes val heightIndex = 1
            layoutWidth =
                typedArray.getLayoutDimension(widthIndex, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutHeight =
                typedArray.getLayoutDimension(heightIndex, ViewGroup.LayoutParams.WRAP_CONTENT)
        } finally {
            typedArray.recycle()
        }
    }

    private fun parseCustomAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.EmptyStateView, 0, 0
        )
        try {
            detailedLoadingInfo =
                typedArray.getBoolean(R.styleable.EmptyStateView_detailed_loading_info, true)

            initialState = typedArray.getInt(R.styleable.EmptyStateView_initial_state, 0)

            stateTextColor =
                typedArray.getColor(R.styleable.EmptyStateView_state_text_color, DefaultTextColor)

            dimTitle = typedArray.getString(R.styleable.EmptyStateView_dim_title)

            emptyStateTitle = typedArray.getString(R.styleable.EmptyStateView_empty_state_title)
            emptyStateSubTitle =
                typedArray.getString(R.styleable.EmptyStateView_empty_state_subtitle)

            emptyStateDrawable =
                typedArray.getDrawable(R.styleable.EmptyStateView_empty_state_drawable)

            loadingStateTitle = typedArray.getString(R.styleable.EmptyStateView_loading_state_title)
            loadingStateSubTitle =
                typedArray.getString(R.styleable.EmptyStateView_loading_state_subtitle)

            actionButtonVisible =
                typedArray.getBoolean(R.styleable.EmptyStateView_action_button_visible, false)
            actionButtonText = typedArray.getString(R.styleable.EmptyStateView_action_button_text)
        } finally {
            typedArray.recycle()
        }
    }

    fun afterView() {
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

    private fun applyNativeAttributes() {
        applyLayoutParams()
    }

    private fun applyLayoutParams() {
        rlEsvRoot?.layoutParams?.width =
            if (layoutWidth < 0) layoutWidth
            else ResourceUtils.convertDpToPixel(context, layoutWidth.toFloat()).toInt()
        rlEsvRoot?.layoutParams?.height =
            if (layoutHeight < 0) layoutHeight
            else ResourceUtils.convertDpToPixel(context, layoutHeight.toFloat()).toInt()
    }

    private fun setupDimViewClickListener() = rlDim?.setOnClickListener { }

    private fun setupInitialState() {
        when (State.fromValue(initialState)) {
            State.Loading -> showLoading()
            State.Empty -> showEmpty()
            State.Container -> showContainer()
        }
    }

    val isLoadingState: Boolean
        get() = svLoadingState?.visibility == View.VISIBLE

    fun showLoading() {
        svLoadingState?.visibility = View.VISIBLE
        flContainer?.visibility = View.GONE
        svEmptyState?.visibility = View.GONE
        if (!detailedLoadingInfo) {
            circularProgressBar?.visibility = View.GONE
            tvLoadingStateSubTitle?.visibility = View.GONE
            tvLoadingStateTitle?.visibility = View.GONE
            flProgress?.visibility = View.VISIBLE
        }
    }

    fun showEmpty() {
        svLoadingState?.visibility = View.GONE
        flContainer?.visibility = View.GONE
        svEmptyState?.visibility = View.VISIBLE
    }

    fun showContainer() {
        svLoadingState?.visibility = View.GONE
        flContainer?.visibility = View.VISIBLE
        svEmptyState?.visibility = View.GONE
    }

    fun setupTextColor() {
        if (stateTextColor != DefaultTextColor) setTextColor(stateTextColor)
    }

    fun setTextColor(color: Int) {
        tvEmptyStateSubTitle?.setTextColor(color)
        tvEmptyStateTitle?.setTextColor(color)
        tvLoadingStateSubTitle?.setTextColor(color)
        tvLoadingStateTitle?.setTextColor(color)
    }

    private fun setupEmptyStateImage() = setEmptyStateImage(emptyStateDrawable)

    fun setEmptyStateImage(drawable: Drawable?) {
        ivEmptyStatePicture?.setImageDrawable(drawable)
        ivEmptyStatePicture?.isVisible = Conditions.isNotNull(drawable)
    }

    fun setupEmptyStateTitle() = setEmptyStateTitle(emptyStateTitle)

    fun setEmptyStateTitle(title: String?) {
        tvEmptyStateTitle?.text = title
        tvEmptyStateTitle?.isVisible = Conditions.isNotNull(title)
    }

    fun setupEmptyStateSubTitle() = setEmptyStateSubTitle(emptyStateSubTitle)

    fun setEmptyStateSubTitle(subTitle: String?) {
        tvEmptyStateSubTitle?.text = subTitle
        tvEmptyStateSubTitle?.isVisible = Conditions.isNotNull(subTitle)
    }

    fun setEmptyStateActionButton(actionTitle: String?, clickListener: UiCallback<View?>) {
        btnEmptyStateAction?.setOnClickListener(clickListener)
        btnEmptyStateAction?.setText(actionTitle)
        btnEmptyStateAction?.isVisible = Conditions.isNotNull(actionTitle)
    }

    fun setupActionButtonText() = setEmptyStateActionButton(actionButtonText)

    fun setEmptyStateActionButton(actionTitle: String?) {
        btnEmptyStateAction?.setText(actionTitle)
    }

    fun onActionButtonClick(onClick: UiJustCallback) {
        btnEmptyStateAction?.setOnClickListener { onClick.invoke() }
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
        btnEmptyStateAction?.isVisible = isVisible
    }

    fun setupLoadingStateTitle() = setLoadingStateTitle(loadingStateTitle)

    fun setLoadingStateTitle(title: String?) {
        tvLoadingStateTitle?.text = title
        if (detailedLoadingInfo) {
            tvLoadingStateTitle?.isVisible = title.isNotNull()
        }
    }

    fun setupLoadingStateSubTitle() = setLoadingStateSubTitle(loadingStateSubTitle)

    fun setLoadingStateSubTitle(subTitle: String?) {
        tvLoadingStateSubTitle?.text = subTitle
        if (detailedLoadingInfo) {
            tvLoadingStateSubTitle?.isVisible = subTitle.isNotNull()
        }
    }

    private fun setupContainerView() {
        if (addedContainer != null)
            setContainerView(addedContainer)
    }


    fun setContainerView(containerView: View?) {
        llListContainer?.removeAllViews()
        try { // View may already exist in parent container or at leas view has a reference to the parent (memory leak btw)
//            if (isViewAddedToParent(containerView, llListContainer))
//                tryToRemoveViewFromParent(containerView)

            llListContainer?.addView(
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

    val container: ViewGroup?
        get() = llListContainer

    private fun setupDimTitle() {
        if (Conditions.isNotNullOrEmpty(dimTitle.orEmpty()))
            setDimTitle(dimTitle)
    }

    fun setDimTitle(title: String?) {
        tvDimStateTitle?.text = title
        tvDimStateTitle?.isVisible = Conditions.isNotNull(title)
    }

    fun dimContainer(dim: Boolean) {
        rlDim?.isVisible = dim
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        val id = child?.id ?: 0
        if (addedViewIds.contains(id))
            super.addView(child, index, params)
        else
            addedContainer = child
    }

    private enum class State {
        Loading, Empty, Container;

        companion object {
            @JvmStatic
            fun fromValue(value: Int): State {
                return when (value) {
                    0 -> Loading
                    1 -> Empty
                    2 -> Container
                    else -> Loading
                }
            }
        }
    }
}