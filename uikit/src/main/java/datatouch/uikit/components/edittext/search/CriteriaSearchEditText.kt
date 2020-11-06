package datatouch.uikit.components.edittext.search

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import datatouch.uikit.R
import datatouch.uikit.components.dropdown.AfterTextChangedListener
import datatouch.uikit.core.callbacks.UiJustCallback
import kotlinx.android.synthetic.main.criteria_search_edit_text.view.*

@SuppressLint("NonConstantResourceId")
class CriteriaSearchEditText : RelativeLayout {

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
        inflateView()
        initResources(context)
        parseCustomAttributes(attrs)
        afterViews()
    }

    private fun inflateView() {
        View.inflate(context, R.layout.criteria_search_edit_text, this)
    }

    private fun initResources(context: Context) {
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

    private fun afterViews() {
        et?.hint = hint
        et?.setHintTextColor(normalHintTextColor)
        flIcon?.setOnClickListener { onDropDownIconClick() }
        et?.addTextChangedListener(AfterTextChangedListener { afterTextChanged() })
        et?.onFocusChangeListener = OnFocusChangeListener { _, focus -> onFocusChange(focus) }
        ivClear?.setOnClickListener { et?.setText("") }
    }

    private fun onDropDownIconClick() {
        if (actv?.isPopupShowing == true) {
            ivArrowIcon?.setImageResource(R.drawable.ic_arrow_down_white)
            actv?.dismissDropDown()
        } else {
            ivArrowIcon?.setImageResource(R.drawable.ic_arrow_up_white)
            actv?.showDropDown()
        }
    }

    private fun afterTextChanged() {
        if (hasValidInput)
            showAsValidInput()
        else
            showAsNormalInput()

        onTextChangeCallback?.invoke()
    }

    private fun onFocusChange(focused: Boolean) {
        if (focused) onFocused()
    }

    private fun onFocused() {
        afterTextChanged()
    }

    private fun showAsValidInput() {
        ivIcon?.setColorFilter(notEmptyColor)
        et?.hint = hint
        et?.setHintTextColor(normalHintTextColor)
    }

    private fun showAsNormalInput() {
        ivIcon?.setColorFilter(emptyNormalColor)
        et?.hint = hint
        et?.setHintTextColor(normalHintTextColor)
    }

    private val hasValidInput get() = et?.text?.isNotEmpty() == true

    var text: String
        get() = et?.text.toString()
        set(value) {
            et?.setText(value)
        }

    fun setAdapter(adapter: ICriteriaSearchEditTextAdapter) {
        actv?.setAdapter(adapter)
        this.adapter = adapter
        adapter.onItemClickCallback = { onItemClick(it) }
    }

    private fun onItemClick(item: ISearchCriterionDropDownListAdapterItem) {
        ivIcon?.setImageResource(item.iconResId)
        actv?.dismissDropDown()
        et?.setText("")
    }

    override fun onDetachedFromWindow() {
        adapter?.onItemClickCallback = null
        adapter = null
        actv?.setAdapter(null)
        super.onDetachedFromWindow()
    }
}