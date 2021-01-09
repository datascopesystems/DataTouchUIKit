package datatouch.uikit.components.dropdown

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import datatouch.uikit.R
import datatouch.uikit.components.dropdown.adapter.IDropDownListAdapterItem
import datatouch.uikit.components.dropdown.adapter.ISelectableDropDownListAdapter
import kotlinx.android.synthetic.main.form_auto_complete_drop_down_list_view.view.*

private const val DefaultThreshold = 1

@SuppressLint("NonConstantResourceId")
class FormAutoCompleteDropDownListView : LinearLayout, IFormView {

    private var verticalOffsetPx = 0
    private var selectedColor = 0
    private var unselectedNormalColor = 0
    private var unselectedErrorColor = 0
    private var normalHintTextColor = 0
    private var defaultIconDrawable: Drawable? = null

    private var originalTypeface: Typeface? = null
    private var hint = ""
    private var leftUnselectedHint = ""
    private var iconDrawable: Drawable? = null
    private var isMandatoryField = false

    private var adapter: ISelectableDropDownListAdapter? = null
    private var isInputFromUser = true

    private var releaseAdapterOnDetachedFromWindow = true

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int)
            : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        inflateView()
        initResources(context)
        parseCustomAttributes(attrs)
        afterViews()
    }

    private fun inflateView() {
        View.inflate(context, R.layout.form_auto_complete_drop_down_list_view, this)
    }

    private fun initResources(context: Context) {
        verticalOffsetPx =
            context.resources.getDimensionPixelSize(R.dimen.drop_down_vertical_offset)
        selectedColor = ContextCompat.getColor(context, R.color.accent_start_light)
        unselectedNormalColor = ContextCompat.getColor(context, R.color.white)
        unselectedErrorColor = ContextCompat.getColor(context, R.color.accent_negative_start_light)
        normalHintTextColor = ContextCompat.getColor(context, R.color.secondary_light)
        defaultIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_search_white)
    }

    private fun parseCustomAttributes(attrs: AttributeSet) {
        val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.FormAutoCompleteDropDownListView, 0, 0
            )
        try {
            hint = typedArray.getString(R.styleable.FormAutoCompleteDropDownListView_actv_hint)
                .orEmpty()

            leftUnselectedHint = typedArray.getString(
                R.styleable.FormAutoCompleteDropDownListView_actv_left_unselected_hint
            ).orEmpty()

            iconDrawable = typedArray.getDrawable(
                R.styleable.FormAutoCompleteDropDownListView_actv_icon
            )
                ?: defaultIconDrawable

            isMandatoryField = typedArray.getBoolean(
                R.styleable
                    .FormAutoCompleteDropDownListView_actv_mandatory_field, false
            )
        } finally {
            typedArray.recycle()
        }
    }

    fun afterViews() {
        originalTypeface = actv?.typeface
        actv?.threshold = DefaultThreshold
        actv?.dropDownVerticalOffset = verticalOffsetPx
        actv?.hint = hint
        refreshClearButton()
        actv?.setHintTextColor(normalHintTextColor)
        actv?.setTypeface(originalTypeface, Typeface.NORMAL)
        ivIcon?.setImageDrawable(iconDrawable)
        actv?.addTextChangedListener(AfterTextChangedListener { afterTextChanged() })
        actv?.onFocusChangeListener = OnFocusChangeListener { _, focus -> onFocusChange(focus) }
        ivClear?.setOnClickListener { actv?.setText("") }
        ivMandatoryIndicator?.isVisible = isMandatoryField
    }

    private fun afterTextChanged() {
        refreshClearButton()

        if (isInputFromUser)
            adapter?.unSelectItem()

        if (isItemSelected()) {
            ivIcon?.setColorFilter(selectedColor)
            ivMandatoryIndicator?.setColorFilter(selectedColor)
        } else {
            ivIcon?.setColorFilter(unselectedNormalColor)
            ivMandatoryIndicator?.setColorFilter(unselectedErrorColor)
        }
    }

    private fun refreshClearButton() {
        ivClear?.isVisible = actv?.text?.isNotEmpty() == true
    }

    private fun isItemSelected() = adapter?.isItemSelected == true

    fun setAdapter(adapter: ISelectableDropDownListAdapter) {
        actv?.setAdapter(adapter)
        this.adapter = adapter
        adapter.onViewInvalidateRequiredCallback =
            { it?.let { onItemSelected(it) } }
    }

    private fun onItemClick(selectedText: String) {
        setTextNotFromUser(selectedText)
        ivIcon?.setColorFilter(selectedColor)
        ivMandatoryIndicator?.setColorFilter(selectedColor)
        actv?.dismissDropDown()
    }

    private fun onItemSelected(item: IDropDownListAdapterItem) = onItemClick(item.name)

    private fun onFocusChange(focused: Boolean) {
        if (focused)
            onFocused()
        else
            onUnfocused()
    }

    private fun onUnfocused() {
        if (isItemSelected()) {
            ivIcon?.setColorFilter(selectedColor)
            ivMandatoryIndicator?.setColorFilter(selectedColor)
            actv?.hint = hint
            actv?.setHintTextColor(normalHintTextColor)
            actv?.setTypeface(originalTypeface, Typeface.NORMAL)
        } else {
            setTextNotFromUser("")
            ivIcon?.setColorFilter(unselectedNormalColor)
            ivMandatoryIndicator?.setColorFilter(unselectedErrorColor)
            if (isMandatoryField) {
                actv?.hint = leftUnselectedHint
                actv?.setHintTextColor(unselectedErrorColor)
                actv?.setTypeface(originalTypeface, Typeface.BOLD)
                ivIcon?.setColorFilter(unselectedErrorColor)
                ivMandatoryIndicator?.setColorFilter(unselectedErrorColor)
            }
        }
    }

    private fun onFocused() {
        actv?.hint = hint
        ivIcon?.setColorFilter(unselectedNormalColor)
        ivMandatoryIndicator?.setColorFilter(unselectedErrorColor)
        actv?.setHintTextColor(normalHintTextColor)
        actv?.setTypeface(originalTypeface, Typeface.NORMAL)
    }

    private fun setTextNotFromUser(text: String) {
        isInputFromUser = false
        actv?.setText(text)
        isInputFromUser = true
    }

    fun setReleaseAdapterOnDetachedFromWindow(release: Boolean) {
        releaseAdapterOnDetachedFromWindow = release
    }

    fun releaseAdapter() {
        adapter?.onViewInvalidateRequiredCallback = null
        adapter = null
        actv?.setAdapter(null)
    }

    override fun onDetachedFromWindow() {
        if (releaseAdapterOnDetachedFromWindow) releaseAdapter()
        super.onDetachedFromWindow()
    }

    override fun showMandatoryFieldErrorIfRequired() = onFocusChange(false)

    override fun setMandatory(isMandatory: Boolean) {
        isMandatoryField = isMandatory
        ivMandatoryIndicator?.isVisible = isMandatoryField
    }

    fun setLeftUnselectedHint(leftUnselectedHint: String) {
        this.leftUnselectedHint = leftUnselectedHint
    }

    fun getLeftUnselectedHint(): String {
        return leftUnselectedHint
    }
}