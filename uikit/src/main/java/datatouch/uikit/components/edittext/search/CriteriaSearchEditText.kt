package datatouch.uikit.components.edittext.search

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnFocusChangeListener
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import datatouch.uikit.R
import datatouch.uikit.components.dropdown.AfterTextChangedListener
import datatouch.uikit.core.callbacks.UiJustCallback
import datatouch.uikit.databinding.CriteriaSearchEditTextBinding

@SuppressLint("NonConstantResourceId")
class CriteriaSearchEditText : RelativeLayout {

    private val ui = CriteriaSearchEditTextBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var verticalOffsetPx = 0
    private var notEmptyColor = 0
    private var emptyNormalColor = 0
    private var emptyErrorColor = 0
    private var normalHintTextColor = 0
    private var defaultIconDrawable: Drawable? = null

    private var adapter: ICriteriaSearchEditTextAdapter? = null
    private var hint = ""

    var onTextChangeCallback: UiJustCallback? = null

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
            context.resources.getDimensionPixelSize(R.dimen.search_drop_down_vertical_offset)
        notEmptyColor = ContextCompat.getColor(context, R.color.accent_start_light)
        emptyNormalColor = ContextCompat.getColor(context, R.color.white)
        emptyErrorColor = ContextCompat.getColor(context, R.color.accent_negative_start_light)
        normalHintTextColor = ContextCompat.getColor(context, R.color.secondary_light)
        defaultIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_search_white)
    }

    private fun parseCustomAttributes(attrs: AttributeSet) {
        val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.CriteriaSearchEditText, 0, 0
            )
        try {
            hint = typedArray.getString(R.styleable.CriteriaSearchEditText_cset_hint).orEmpty()
        } finally {
            typedArray.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        ui.et.hint = hint
        ui.actv.dropDownVerticalOffset = verticalOffsetPx
        ui.et.setHintTextColor(normalHintTextColor)
        ui.flIcon.setOnClickListener { onDropDownIconClick() }
        ui.actv.setOnDismissListener { ui.ivArrowIcon.setImageResource(R.drawable.ic_arrow_down_white) }
        ui.et.addTextChangedListener(AfterTextChangedListener { afterTextChanged() })
        ui.et.onFocusChangeListener = OnFocusChangeListener { _, focus -> onFocusChange(focus) }
        ui.ivClear.setOnClickListener { ui.et.setText("") }
        refreshClearButton()
    }

    private fun onDropDownIconClick() {
        if (ui.actv.isPopupShowing) {
            ui.ivArrowIcon.setImageResource(R.drawable.ic_arrow_down_white)
            ui.actv.dismissDropDown()
        } else {
            ui.ivArrowIcon.setImageResource(R.drawable.ic_arrow_up_white)
            ui.actv.showDropDown()
        }
    }

    private fun afterTextChanged() {
        refreshClearButton()

        if (hasValidInput)
            showAsValidInput()
        else
            showAsNormalInput()

        onTextChangeCallback?.invoke()
    }

    private fun refreshClearButton() {
        ui.ivClear.isVisible = ui.et.text?.isNotEmpty() == true
    }

    private fun onFocusChange(focused: Boolean) {
        if (focused) onFocused()
    }

    private fun onFocused() {
        afterTextChanged()
    }

    private fun showAsValidInput() {
        ui.ivIcon.setColorFilter(notEmptyColor)
        ui.et.hint = hint
        ui.et.setHintTextColor(normalHintTextColor)
    }

    private fun showAsNormalInput() {
        ui.ivIcon.setColorFilter(emptyNormalColor)
        ui.et.hint = hint
        ui.et.setHintTextColor(normalHintTextColor)
    }

    private val hasValidInput get() = ui.et.text?.isNotEmpty() == true

    var text: String
        get() = ui.et.text.toString()
        set(value) {
            ui.et.setText(value)
        }

    fun setAdapter(adapter: ICriteriaSearchEditTextAdapter) {
        ui.actv.setAdapter(adapter)
        this.adapter = adapter
        adapter.onItemSelectionChangeCallback = { onItemClick(it) }
    }

    private fun onItemClick(item: ISearchCriterionDropDownListAdapterItem) {
        ui.ivIcon.setImageResource(item.iconResId)
        ui.actv.dismissDropDown()
        ui.et.setText("")
    }

    override fun onDetachedFromWindow() {
        adapter?.onItemSelectionChangeCallback = null
        adapter = null
        ui.actv.setAdapter(null)
        super.onDetachedFromWindow()
    }
}