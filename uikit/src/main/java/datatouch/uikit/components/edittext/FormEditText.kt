package datatouch.uikit.components.edittext

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
import datatouch.uikit.components.dropdown.AfterTextChangedListener
import datatouch.uikit.components.dropdown.IFormView
import datatouch.uikit.core.callbacks.UiJustCallback
import kotlinx.android.synthetic.main.form_edit_text.view.*

@SuppressLint("NonConstantResourceId")
class FormEditText : LinearLayout, IFormView {

    private var notEmptyColor = 0
    private var emptyNormalColor = 0
    private var emptyErrorColor = 0
    private var normalHintTextColor = 0
    private var defaultIconDrawable: Drawable? = null

    private var originalTypeface: Typeface? = null
    private var hint = ""
    private var leftUnselectedHint = ""
    private var iconDrawable: Drawable? = null
    private var isMandatoryField = false
    private var inputType = InputType.Text
    private var isEditable = true

    private var enableClickOnFocus = false
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
        View.inflate(context, R.layout.form_edit_text, this)
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
                R.styleable.FormEditText, 0, 0
            )
        try {
            hint = typedArray.getString(R.styleable.FormEditText_et_hint).orEmpty()

            leftUnselectedHint = typedArray.getString(
                R.styleable.FormEditText_et_left_unselected_hint
            ).orEmpty()

            iconDrawable = typedArray.getDrawable(
                R.styleable.FormEditText_et_icon
            )
                ?: defaultIconDrawable

            isMandatoryField = typedArray.getBoolean(
                R.styleable
                    .FormEditText_et_mandatory_field, false
            )

            val inputTypeInt = typedArray.getInt(R.styleable.FormEditText_et_inputType, 0)
            inputType = InputType.fromInt(inputTypeInt)

            isEditable = typedArray.getBoolean(R.styleable.FormEditText_et_editable, true)
        } finally {
            typedArray.recycle()
        }
    }

    fun afterViews() {
        originalTypeface = et?.typeface
        et?.hint = hint
        et?.setHintTextColor(normalHintTextColor)
        et?.setTypeface(originalTypeface, Typeface.NORMAL)
        setupInputType()
        ivIcon?.setImageDrawable(iconDrawable)
        et?.addTextChangedListener(AfterTextChangedListener { afterTextChanged() })
        et?.onFocusChangeListener = OnFocusChangeListener { _, focus -> onFocusChange(focus) }
    }

    private fun setupInputType() {
        if (!isEditable) {
            et.inputType = android.text.InputType.TYPE_NULL
            return
        }

        when (inputType) {
            InputType.Text -> et.inputType = android.text.InputType.TYPE_CLASS_TEXT

            InputType.Number -> et.inputType = android.text.InputType.TYPE_CLASS_NUMBER
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
        else onUnfocused()
    }

    private fun onFocused() {
        afterTextChanged()

        if (enableClickOnFocus)
            llFormEditTextRoot.performClick()
    }

    private fun showAsValidInput() {
        ivIcon?.setColorFilter(notEmptyColor)
        et?.hint = hint
        et?.setHintTextColor(normalHintTextColor)
        et?.setTypeface(originalTypeface, Typeface.NORMAL)
    }

    private fun showAsNormalInput() {
        ivIcon?.setColorFilter(emptyNormalColor)
        et?.hint = hint
        et?.setHintTextColor(normalHintTextColor)
        et?.setTypeface(originalTypeface, Typeface.NORMAL)
    }

    private fun onUnfocused() {
        if (isMandatoryField && !hasValidInput) showAsErrorInput()
    }

    private fun showAsErrorInput() {
        ivIcon?.setColorFilter(emptyErrorColor)
        et?.hint = leftUnselectedHint
        et?.setHintTextColor(emptyErrorColor)
        et?.setTypeface(originalTypeface, Typeface.BOLD)
    }

    val hasValidInput get() = et?.text?.isNotEmpty() == true

    var text: String
        get() = et?.text.toString()
        set(value) {
            et?.setText(value)
        }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        enableClickOnFocus = true
        et?.isFocusableInTouchMode = false
        llFormEditTextRoot?.setOnClickListener(l)
        et?.setOnClickListener(l)
        ivIcon?.setOnClickListener(l)
    }

    override fun showMandatoryFieldErrorIfRequired() = onUnfocused()

}