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

    private var adapter: SelectableAutoCompleteTextViewListAdapter? = null

    private var isInputFromUser = true

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
        actv?.setHintTextColor(normalHintTextColor)
        actv?.setTypeface(originalTypeface, Typeface.NORMAL)
        ivIcon?.setImageDrawable(iconDrawable)
        actv?.addTextChangedListener(AfterTextChangedListener { afterTextChanged() })
        actv?.onFocusChangeListener = OnFocusChangeListener { _, focus -> onFocusChange(focus) }
    }

    private fun afterTextChanged() {
        if (isInputFromUser)
            adapter?.unSelectItem()

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
        setTextNotFromUser(selectedText)
        ivIcon?.setColorFilter(selectedColor)
        actv?.dismissDropDown()
    }

    private fun onFocusChange(focused: Boolean) {
        if (isUserLeftUnselected(focused)) {
            setTextNotFromUser("")
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

    private fun setTextNotFromUser(text: String) {
        isInputFromUser = false
        actv?.setText(text)
        isInputFromUser = true
    }

    private fun isUserLeftUnselected(focused: Boolean) = !focused && !isItemSelected()

    override fun onDetachedFromWindow() {
        adapter = null
        adapter?.onItemClickCallback = null
        super.onDetachedFromWindow()
    }

    override fun showMandatoryFieldErrorIfRequired() = onFocusChange(false)

}