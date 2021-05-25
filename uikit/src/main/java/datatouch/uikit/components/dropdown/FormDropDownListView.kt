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
import datatouch.uikit.databinding.FormDropDownListViewBinding

@SuppressLint("NonConstantResourceId")
class FormDropDownListView : LinearLayout, IFormView {

    private val ui = FormDropDownListViewBinding
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

    private var releaseAdapterOnDetachedFromWindow = true

    private var adapter: ISelectableDropDownListAdapter? = null

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
                R.styleable.FormDropDownListView, 0, 0
            )
        try {
            hint = typedArray.getString(R.styleable.FormDropDownListView_ddlv_hint).orEmpty()

            leftUnselectedHint = typedArray.getString(
                R.styleable.FormDropDownListView_ddlv_left_unselected_hint
            ).orEmpty()

            iconDrawable = typedArray.getAppCompatDrawable(context,
                R.styleable.FormDropDownListView_ddlv_icon
            )
                ?: defaultIconDrawable

            isMandatoryField =
                typedArray.getBoolean(R.styleable.FormDropDownListView_ddlv_mandatory_field, false)
        } finally {
            typedArray.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        originalTypeface = ui.actv.typeface
        ui.actv.inputType = android.text.InputType.TYPE_NULL
        ui.actv.keyListener = null
        ui.actv.dropDownVerticalOffset = verticalOffsetPx
        ui.actv.hint = hint
        ui.actv.setHintTextColor(normalHintTextColor)
        ui.actv.setTypeface(originalTypeface, Typeface.NORMAL)
        ui.ivIcon.setImageDrawable(iconDrawable)
        ui.actv.setOnDismissListener { ui.ivArrowIcon?.setImageResource(R.drawable.ic_arrow_down_white) }
        setOnClickListener()
        ui.actv.addTextChangedListener(AfterTextChangedListener { afterTextChanged() })
        ui.actv.onFocusChangeListener = OnFocusChangeListener { _, focus -> onFocusChange(focus) }
        ui.ivMandatoryIndicator.isVisible = isMandatoryField
    }

    private fun setOnClickListener() {
        ui.llDropDownRoot.setOnClickListener { onViewClick() }
        ui.actv.setOnClickListener { onViewClick() }
    }

    private fun onViewClick() {
        if (ui.actv.isPopupShowing) {
            ui.ivArrowIcon.setImageResource(R.drawable.ic_arrow_down_white)
            ui.actv.dismissDropDown()
        } else {
            ui.ivArrowIcon.setImageResource(R.drawable.ic_arrow_up_white)
            ui.actv.showDropDown()
        }
    }

    private fun afterTextChanged() {
        if (isItemSelected()) {
            ui.ivIcon.setColorFilter(selectedColor)
            ui.ivMandatoryIndicator.setColorFilter(selectedColor)
        }
        else {
            ui.ivIcon.setColorFilter(unselectedNormalColor)
            ui.ivMandatoryIndicator.setColorFilter(unselectedErrorColor)
        }
    }

    private fun isItemSelected() = adapter?.isItemSelected == true

    fun setAdapter(adapter: ISelectableDropDownListAdapter) {
        ui.actv.setAdapter(adapter)
        this.adapter = adapter
        adapter.onViewInvalidateRequiredCallback =
            { it?.let { onItemSelected(it) } ?: onItemUnSelected() }
    }

    private fun onItemClick(selectedText: String) {
        ui.actv.setText(selectedText)
        ui.ivIcon.setColorFilter(selectedColor)
        ui.ivMandatoryIndicator.setColorFilter(selectedColor)
        ui.actv.dismissDropDown()
    }

    private fun onItemSelected(item: IDropDownListAdapterItem) = onItemClick(item.name)

    private fun onItemUnSelected() {
        ui.actv.setText("")
        ui.ivIcon.setColorFilter(unselectedNormalColor)
        ui.ivMandatoryIndicator.setColorFilter(unselectedErrorColor)
        ui.actv.dismissDropDown()
    }

    private fun onFocusChange(focused: Boolean) {
        if (focused)
            onFocused()
        else
            onUnFocused()
    }

    // Prevents double click to show drop down when clicked on unfocused view
    private fun onFocused() {
        onViewClick()
    }

    private fun onUnFocused() {
        if (!isItemSelected()) {
            ui.actv.setText("")
            ui.ivIcon.setColorFilter(unselectedNormalColor)
            ui.ivMandatoryIndicator.setColorFilter(unselectedErrorColor)
            if (isMandatoryField) {
                ui.actv.hint = leftUnselectedHint
                ui.actv.setHintTextColor(unselectedErrorColor)
                ui.actv.setTypeface(originalTypeface, Typeface.BOLD)
                ui.ivIcon.setColorFilter(unselectedErrorColor)
                ui.ivMandatoryIndicator.setColorFilter(unselectedErrorColor)
            }
        } else {
            ui.actv.hint = hint
            ui.actv.setHintTextColor(normalHintTextColor)
            ui.actv.setTypeface(originalTypeface, Typeface.NORMAL)
            ui.ivIcon.setColorFilter(selectedColor)
            ui.ivMandatoryIndicator.setColorFilter(selectedColor)
        }
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

    fun getLeftUnselectedHint(): String {
        return leftUnselectedHint
    }
}