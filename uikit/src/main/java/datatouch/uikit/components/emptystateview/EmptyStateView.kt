package datatouch.uikit.components.emptystateview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import datatouch.uikit.R
import datatouch.uikit.utils.Conditions
import datatouch.uikit.utils.ViewUtils
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
    private val addedViewIds = intArrayOf(R.id.rlRoot)

    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        inflateView()
        parseCustomAttributes(attrs)
        afterView()
    }

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        inflateView()
        parseCustomAttributes(attrs)
        afterView()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet,
        defStyleAttr: Int,
        @Suppress("UNUSED_PARAMETER") defStyleRes: Int
    ) : super(context, attrs, defStyleAttr) {
        inflateView()
        parseCustomAttributes(attrs)
        afterView()
    }

    protected fun inflateView() {
        View.inflate(context, R.layout.empty_state_view, this)
    }

    private fun parseCustomAttributes(attrs: AttributeSet) {
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

    private fun setupDimViewClickListener() = rlDim.setOnClickListener { }

    private fun setupInitialState() {
        when (State.fromValue(initialState)) {
            State.Loading -> showLoading()
            State.Empty -> showEmpty()
            State.Container -> showContainer()
        }
    }

    val isLoadingState: Boolean
        get() = svLoadingState.visibility == View.VISIBLE

    fun showLoading() {
        svLoadingState.visibility = View.VISIBLE
        flContainer.visibility = View.GONE
        svEmptyState.visibility = View.GONE
        if (!detailedLoadingInfo) {
            circularProgressBar.visibility = View.GONE
            tvLoadingStateSubTitle.visibility = View.GONE
            tvLoadingStateTitle.visibility = View.GONE
            flProgress.visibility = View.VISIBLE
        }
    }

    fun showEmpty() {
        svLoadingState.visibility = View.GONE
        flContainer.visibility = View.GONE
        svEmptyState.visibility = View.VISIBLE
    }

    fun showContainer() {
        svLoadingState.visibility = View.GONE
        flContainer.visibility = View.VISIBLE
        svEmptyState.visibility = View.GONE
    }

    fun setupTextColor() {
        if (stateTextColor != DefaultTextColor) setTextColor(stateTextColor)
    }

    fun setTextColor(color: Int) {
        tvEmptyStateSubTitle.setTextColor(color)
        tvEmptyStateTitle.setTextColor(color)
        tvLoadingStateSubTitle.setTextColor(color)
        tvLoadingStateTitle.setTextColor(color)
    }

    private fun setupEmptyStateImage() = setEmptyStateImage(emptyStateDrawable)

    fun setEmptyStateImage(drawable: Drawable?) {
        ivEmptyStatePicture.setImageDrawable(drawable)
        ivEmptyStatePicture.visibility =
            if (Conditions.isNotNull(drawable)) View.VISIBLE else View.GONE
    }

    fun setupEmptyStateTitle() = setEmptyStateTitle(emptyStateTitle)

    fun setEmptyStateTitle(title: String?) {
        tvEmptyStateTitle.text = title
        tvEmptyStateTitle.visibility = if (Conditions.isNotNull(title)) View.VISIBLE else View.GONE
    }

    fun setupEmptyStateSubTitle() = setEmptyStateSubTitle(emptyStateSubTitle)

    fun setEmptyStateSubTitle(subTitle: String?) {
        tvEmptyStateSubTitle.text = subTitle
        tvEmptyStateSubTitle.visibility =
            if (Conditions.isNotNull(subTitle)) View.VISIBLE else View.GONE
    }

    fun setEmptyStateActionButton(actionTitle: String?, clickListener: (View?) -> Unit) {
        btnEmptyStateAction.setOnClickListener(clickListener)
        btnEmptyStateAction.setText(actionTitle)
        btnEmptyStateAction.visibility =
            if (Conditions.isNotNull(actionTitle)) View.VISIBLE else View.GONE
    }

    fun setupActionButtonText() = setEmptyStateActionButton(actionButtonText)

    fun setEmptyStateActionButton(actionTitle: String?) {
        btnEmptyStateAction.setText(actionTitle)
    }

    fun setEmptyStateActionButton(clickListener: OnClickListener?) {
        btnEmptyStateAction.setOnClickListener(clickListener)
    }

    private fun setupActionButtonVisibility() =
        if (actionButtonVisible) showEmptyStateActionButton() else hideEmptyStateActionButton()

    fun hideEmptyStateActionButton() {
        btnEmptyStateAction.visibility = View.GONE
    }

    fun showEmptyStateActionButton() {
        btnEmptyStateAction.visibility = View.VISIBLE
    }

    fun setupLoadingStateTitle() = setLoadingStateTitle(loadingStateTitle)

    fun setLoadingStateTitle(title: String?) {
        tvLoadingStateTitle.text = title
        if (detailedLoadingInfo) {
            tvLoadingStateTitle.visibility =
                if (Conditions.isNotNull(title)) View.VISIBLE else View.GONE
        }
    }

    fun setupLoadingStateSubTitle() = setLoadingStateSubTitle(loadingStateSubTitle)

    fun setLoadingStateSubTitle(subTitle: String?) {
        tvLoadingStateSubTitle.text = subTitle
        if (detailedLoadingInfo) {
            tvLoadingStateSubTitle.visibility =
                if (Conditions.isNotNull(subTitle)) View.VISIBLE else View.GONE
        }
    }

    private fun setupContainerView() {
        if (addedContainer != null)
            setContainerView(addedContainer)
    }


    fun setContainerView(containerView: View?) {
        llListContainer.removeAllViews()
        try { // View may already exist in parent container or at leas view has a reference to the parent (memory leak btw)
            if (ViewUtils.isViewAddedToParent(containerView!!, llListContainer)) {
                ViewUtils.tryToRemoveViewFromParent(containerView)
            }
            llListContainer.addView(
                containerView,
                LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun show(isEmpty: Boolean) {
        if (isEmpty) showEmpty() else showContainer()
    }

    fun showOrLoading(hasContent: Boolean) {
        if (hasContent) showContainer() else showLoading()
    }

    val container: ViewGroup
        get() = llListContainer

    private fun setupDimTitle() {
        if (Conditions.isNotNullOrEmpty(dimTitle.toString())) {
            setDimTitle(dimTitle)
        }
    }

    fun setDimTitle(title: String?) {
        tvDimStateTitle.text = title
        tvDimStateTitle.visibility = if (Conditions.isNotNull(title)) View.VISIBLE else View.GONE
    }

    fun dimContainer(dim: Boolean) {
        rlDim.visibility = if (dim) View.VISIBLE else View.GONE
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