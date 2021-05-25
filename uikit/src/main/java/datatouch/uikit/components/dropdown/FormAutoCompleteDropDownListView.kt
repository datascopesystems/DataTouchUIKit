package datatouch.uikit.components.dropdown

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnFocusChangeListener
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import datatouch.uikit.R
import datatouch.uikit.components.dropdown.adapter.IDropDownListAdapterItem
import datatouch.uikit.components.dropdown.adapter.ISelectableDropDownListAdapter
import datatouch.uikit.core.extensions.TypedArrayExtensions.getAppCompatDrawable
import datatouch.uikit.databinding.FormAutoCompleteDropDownListViewBinding

private const val DefaultThreshold = 1

@SuppressLint("NonConstantResourceId")
class FormAutoCompleteDropDownListView : LinearLayout, IFormView {

    private val ui = FormAutoCompleteDropDownListViewBinding
        .inflate(LayoutInflater.from(context), this, true)

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
        initResources(context)
        parseCustomAttributes(attrs)
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

            iconDrawable = typedArray.getAppCompatDrawable(context,
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

    override fun onFinishInflate() {
        super.onFinishInflate()
        originalTypeface = ui.actv.typeface
        ui.actv.threshold = DefaultThreshold
        ui.actv.dropDownVerticalOffset = verticalOffsetPx
        ui.actv.hint = hint
        refreshClearButton()
        ui.actv.setHintTextColor(normalHintTextColor)
        ui.actv.setTypeface(originalTypeface, Typeface.NORMAL)
        ui.ivIcon.setImageDrawable(iconDrawable)
        ui.actv.addTextChangedListener(AfterTextChangedListener { afterTextChanged() })
        ui.actv.onFocusChangeListener = OnFocusChangeListener { _, focus -> onFocusChange(focus) }
        ui.ivClear.setOnClickListener { ui.actv.setText("") }
        ui.ivMandatoryIndicator.isVisible = isMandatoryField
    }

    private fun afterTextChanged() {
        refreshClearButton()

        if (isInputFromUser)
            adapter?.unSelectItem()

        if (isItemSelected()) {
            ui.ivIcon.setColorFilter(selectedColor)
            ui.ivMandatoryIndicator.setColorFilter(selectedColor)
        } else {
            ui.ivIcon.setColorFilter(unselectedNormalColor)
            ui.ivMandatoryIndicator.setColorFilter(unselectedErrorColor)
        }
    }

    private fun refreshClearButton() {
        ui.ivClear.isVisible = ui.actv.text?.isNotEmpty() == true
    }

    private fun isItemSelected() = adapter?.isItemSelected == true

    fun setAdapter(adapter: ISelectableDropDownListAdapter) {
        ui.actv.setAdapter(adapter)
        this.adapter = adapter
        adapter.onViewInvalidateRequiredCallback =
            { it?.let { onItemSelected(it) } }
    }

    private fun onItemClick(selectedText: String) {
        setTextNotFromUser(selectedText)
        ui.ivIcon.setColorFilter(selectedColor)
        ui.ivMandatoryIndicator.setColorFilter(selectedColor)
        ui.actv.dismissDropDown()
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
            ui.ivIcon.setColorFilter(selectedColor)
            ui.ivMandatoryIndicator.setColorFilter(selectedColor)
            ui.actv.hint = hint
            ui.actv.setHintTextColor(normalHintTextColor)
            ui.actv.setTypeface(originalTypeface, Typeface.NORMAL)
        } else {
            setTextNotFromUser("")
            ui.ivIcon.setColorFilter(unselectedNormalColor)
            ui.ivMandatoryIndicator.setColorFilter(unselectedErrorColor)
            if (isMandatoryField) {
                ui.actv.hint = leftUnselectedHint
                ui.actv.setHintTextColor(unselectedErrorColor)
                ui.actv.setTypeface(originalTypeface, Typeface.BOLD)
                ui.ivIcon.setColorFilter(unselectedErrorColor)
                ui.ivMandatoryIndicator.setColorFilter(unselectedErrorColor)
            }
        }
    }

    private fun onFocused() {
        ui.actv.hint = hint
        ui.ivIcon.setColorFilter(unselectedNormalColor)
        ui.ivMandatoryIndicator.setColorFilter(unselectedErrorColor)
        ui.actv.setHintTextColor(normalHintTextColor)
        ui.actv.setTypeface(originalTypeface, Typeface.NORMAL)
    }

    private fun setTextNotFromUser(text: String) {
        isInputFromUser = false
        ui.actv.setText(text)
        isInputFromUser = true
    }

    fun setReleaseAdapterOnDetachedFromWindow(release: Boolean) {
        releaseAdapterOnDetachedFromWindow = release
    }

    fun releaseAdapter() {
        adapter?.onViewInvalidateRequiredCallback = null
        adapter = null
        ui.actv.setAdapter(null)
    }

    override fun onDetachedFromWindow() {
        if (releaseAdapterOnDetachedFromWindow) releaseAdapter()
        super.onDetachedFromWindow()
    }

    override fun showMandatoryFieldErrorIfRequired() = onFocusChange(false)

    override fun setMandatory(isMandatory: Boolean) {
        isMandatoryField = isMandatory
        ui.ivMandatoryIndicator.isVisible = isMandatoryField
    }

    fun setLeftUnselectedHint(leftUnselectedHint: String) {
        this.leftUnselectedHint = leftUnselectedHint
    }

    // It is required for DMS
    fun getLeftUnselectedHint(): String {
        return leftUnselectedHint
    }

    fun setHint(hint: String) {
        this.hint = hint
        onFocusChange(isFocused)
    }
}