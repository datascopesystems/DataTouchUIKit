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
import datatouch.uikit.R
import datatouch.uikit.components.dropdown.adapter.SelectableAutoCompleteTextViewListAdapter
import kotlinx.android.synthetic.main.form_drop_down_list_view.view.*

@SuppressLint("NonConstantResourceId")
class FormDropDownListView : LinearLayout, IFormView {
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

    private var adapter: SelectableAutoCompleteTextViewListAdapter? = null

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
        View.inflate(context, R.layout.form_drop_down_list_view, this)
    }

    private fun initResources(context: Context) {
        verticalOffsetPx =
            context.resources.getDimensionPixelSize(R.dimen.autocompletetextview_vertical_offset)
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

            iconDrawable = typedArray.getDrawable(
                R.styleable.FormDropDownListView_ddlv_icon
            )
                ?: defaultIconDrawable

            isMandatoryField =
                typedArray.getBoolean(R.styleable.FormDropDownListView_ddlv_mandatory_field, false)
        } finally {
            typedArray.recycle()
        }
    }

    fun afterViews() {
        originalTypeface = actv?.typeface
        actv?.inputType = android.text.InputType.TYPE_NULL
        actv?.dropDownVerticalOffset = verticalOffsetPx
        actv?.hint = hint
        actv?.setHintTextColor(normalHintTextColor)
        actv?.setTypeface(originalTypeface, Typeface.NORMAL)
        ivIcon?.setImageDrawable(iconDrawable)
        actv?.setOnDismissListener { ivArrowIcon?.setImageResource(R.drawable.ic_arrow_down_white) }
        setOnClickListener()
        actv?.addTextChangedListener(AfterTextChangedListener { afterTextChanged() })
        actv?.onFocusChangeListener = OnFocusChangeListener { _, focus -> onFocusChange(focus) }
    }

    private fun setOnClickListener() {
        llDropDownRoot?.setOnClickListener { onViewClick() }
        actv?.setOnClickListener { onViewClick() }
    }

    private fun onViewClick() {
        if (actv?.isPopupShowing == true) {
            ivArrowIcon?.setImageResource(R.drawable.ic_arrow_down_white)
            actv?.dismissDropDown()
        } else {
            ivArrowIcon?.setImageResource(R.drawable.ic_arrow_up_white)
            actv?.showDropDown()
        }
    }

    private fun afterTextChanged() {
        if (isItemSelected())
            ivIcon?.setColorFilter(selectedColor)
        else
            ivIcon?.setColorFilter(unselectedNormalColor)
    }

    private fun isItemSelected() = adapter?.isItemSelected == true

    fun setAdapter(adapter: SelectableAutoCompleteTextViewListAdapter) {
        actv?.setAdapter(adapter)
        this.adapter = adapter
        adapter.onItemClickCallback = { onItemClick(it) }
    }

    private fun onItemClick(selectedText: String) {
        actv?.setText(selectedText)
        ivIcon?.setColorFilter(selectedColor)
        actv?.dismissDropDown()
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
            actv?.setText("")
            ivIcon?.setColorFilter(unselectedNormalColor)
            if (isMandatoryField) {
                actv?.hint = leftUnselectedHint
                actv?.setHintTextColor(unselectedErrorColor)
                actv?.setTypeface(originalTypeface, Typeface.BOLD)
                ivIcon?.setColorFilter(unselectedErrorColor)
            }
        } else {
            actv?.hint = hint
            actv?.setHintTextColor(normalHintTextColor)
            actv?.setTypeface(originalTypeface, Typeface.NORMAL)
        }
    }

    override fun onDetachedFromWindow() {
        adapter = null
        adapter?.onItemClickCallback = null
        super.onDetachedFromWindow()
    }

    override fun showMandatoryFieldErrorIfRequired() = onFocusChange(false)

}