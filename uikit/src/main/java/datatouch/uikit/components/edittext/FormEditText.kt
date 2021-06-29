package datatouch.uikit.components.edittext

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnFocusChangeListener
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import datatouch.uikit.R
import datatouch.uikit.components.dropdown.AfterTextChangedListener
import datatouch.uikit.components.dropdown.IFormView
import datatouch.uikit.core.callbacks.UiJustCallback
import datatouch.uikit.core.extensions.ImageViewExtensions.setColorFilterRes
import datatouch.uikit.core.extensions.TextViewExtensions.setHintTextColorRes
import datatouch.uikit.core.extensions.TypedArrayExtensions.getAppCompatDrawable
import datatouch.uikit.core.utils.views.ViewUtils
import datatouch.uikit.databinding.FormEditTextBinding


@SuppressLint("NonConstantResourceId")
class FormEditText : LinearLayout, IFormView {

    private val ui = FormEditTextBinding
        .inflate(LayoutInflater.from(context), this, true)

    private val darkThemeBackgroundRes = R.drawable.ellipsized_primary_half_transparent_background
    private val lightThemeBackgroundRes = R.drawable.primary_light_edit_text_rounded_corner

    private val notEmptyColorRes = R.color.accent_start_light
    private val emptyNormalColorRes = R.color.white
    private val emptyErrorColorRes = R.color.accent_negative_start_light
    private val normalHintTextColorRes = R.color.secondary_light
    private var defaultIconDrawable: Drawable? = null

    private var originalTypeface: Typeface? = null
    private var hint = ""
    private var leftUnselectedHint = ""
    private var iconDrawable: Drawable? = null
    private var isMandatoryField = false
    private var inputType = InputType.Text
    private var isEditable = true
    private var maxLines = DefaultMaxLines
    private var maxTextLength = DefaultMaxTextLength
    private var theme = Theme.Dark

    private var enableClickOnFocus = false
    var onTextChangeCallback: UiJustCallback? = null

    private var layoutWidth = 0
    private var layoutHeight = 0

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int)
            : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        initResources(context)
        parseAttributes(attrs)
    }

    private fun initResources(context: Context) {
        defaultIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_search_white)
    }

    private fun parseAttributes(attrs: AttributeSet) {
        val parsedNativeAttributes = ViewUtils.parseNativeAttributes(context, attrs)
        layoutHeight = parsedNativeAttributes.layoutHeight
        layoutWidth = parsedNativeAttributes.layoutWidth
        parseCustomAttributes(attrs)
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

            iconDrawable = typedArray.getAppCompatDrawable(
                context,
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

            maxLines = typedArray.getInteger(R.styleable.FormEditText_et_max_lines, DefaultMaxLines)

            maxTextLength = typedArray.getInt(
                R.styleable.FormEditText_et_max_text_length,
                DefaultMaxTextLength
            )

            val themeInt = typedArray.getInt(R.styleable.FormEditText_et_theme, 0)
            theme = Theme.fromInt(themeInt)

        } finally {
            typedArray.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        applyLayoutParams()
        ui.apply {
            originalTypeface = et.typeface
            et.hint = hint
            et.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxTextLength))
            et.setHintTextColorRes(normalHintTextColorRes)
            et.setTypeface(originalTypeface, Typeface.NORMAL)
            setupInputType()
            et.maxLines = maxLines
            refreshClearButton()
            ivIcon.setImageDrawable(iconDrawable)
            et.addTextChangedListener(AfterTextChangedListener { afterTextChanged() })
            et.onFocusChangeListener = OnFocusChangeListener { _, focus -> onFocusChange(focus) }
            ivClear.setOnClickListener { et.setText("") }
            ivMandatoryIndicator.isVisible = isMandatoryField
            setupTheme()
        }
    }

    private fun applyLayoutParams() {
        ui.llFormEditTextRoot.layoutParams?.width = layoutWidth
        ui.llFormEditTextRoot.layoutParams?.height = layoutHeight
    }

    private fun setupInputType() = ui.apply {
        if (!isEditable) {
            et.inputType = android.text.InputType.TYPE_NULL
            et.keyListener = null
            return@apply
        }

        when (inputType) {
            InputType.Text -> et.inputType = android.text.InputType.TYPE_CLASS_TEXT

            InputType.Number -> et.inputType = android.text.InputType.TYPE_CLASS_NUMBER

            InputType.TextMultiline -> et.inputType =
                android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
        }
    }

    private fun afterTextChanged() {
        refreshViewsAfterTextChange()
        onTextChangeCallback?.invoke()
    }

    private fun refreshViewsAfterTextChange() {
        refreshClearButton()

        if (hasValidInput)
            showAsValidInput()
        else
            showAsNormalInput()
    }

    private fun refreshClearButton() = ui.apply {
        ivClear.isVisible = et.text?.isNotEmpty() == true && isEditable
    }

    private fun onFocusChange(focused: Boolean) {
        if (focused) onFocused()
        else onUnfocused()
    }

    private fun onFocused() {
        afterTextChanged()

        if (enableClickOnFocus)
            ui.llFormEditTextRoot.performClick()
    }

    fun showAsValidInput() = ui.apply {
        ivIcon.setColorFilterRes(notEmptyColorRes)
        ivMandatoryIndicator.setColorFilter(notEmptyColorRes)
        et.hint = hint
        et.setHintTextColorRes(normalHintTextColorRes)
        et.setTypeface(originalTypeface, Typeface.NORMAL)
    }

    fun showAsNormalInput() = ui.apply {
        ivIcon.setColorFilterRes(emptyNormalColorRes)
        ivMandatoryIndicator.setColorFilterRes(emptyErrorColorRes)
        et.hint = hint
        et.setHintTextColorRes(normalHintTextColorRes)
        et.setTypeface(originalTypeface, Typeface.NORMAL)
    }

    private fun onUnfocused() {
        if (isMandatoryField && !hasValidInput) showAsErrorInput()
    }

    fun showAsErrorInput() = ui.apply {
        ivIcon.setColorFilterRes(emptyErrorColorRes)
        et.hint = leftUnselectedHint
        et.setHintTextColorRes(emptyErrorColorRes)
        et.setTypeface(originalTypeface, Typeface.BOLD)
    }

    val hasValidInput get() = ui.et.text?.trim()?.isNotEmpty() == true

    val hasValidInputWhenMandatory
        get() = run {
            if (isMandatoryField) hasValidInput
            else true
        }

    var text: String
        get() = ui.et.text.toString()
        set(value) {
            ui.et.setText(value)
            refreshViewsAfterTextChange()
        }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        enableClickOnFocus = true
        ui.et.isFocusableInTouchMode = false
        ui.llFormEditTextRoot.setOnClickListener(l)
        ui.et.setOnClickListener(l)
        ui.ivIcon.setOnClickListener(l)
    }

    fun setHint(hint: String) {
        this.hint = hint
        ui.et.hint = hint
    }

    fun setLeftUnselectedHint(leftUnselectedHint: String) {
        this.leftUnselectedHint = leftUnselectedHint
        refreshIfUnfocused()
    }

    fun getLeftUnselectedHint(): String {
        return leftUnselectedHint
    }

    private fun refreshIfUnfocused() {
        if (!isFocused)
            onUnfocused()
    }

    fun setIconDrawable(iconDrawable: Drawable?) {
        this.iconDrawable = iconDrawable
        ui.ivIcon.setImageDrawable(iconDrawable)
    }

    override fun showMandatoryFieldErrorIfRequired() {
        if (!isMandatoryField) return

        setMandatory(true)
        onUnfocused()
    }

    override fun setMandatory(isMandatory: Boolean) {
        this.isMandatoryField = isMandatory
        ui.ivMandatoryIndicator.isVisible = isMandatoryField
        refreshViewsAfterTextChange()
    }

    fun setInputType(type: InputType) {
        inputType = type
        setupInputType()
    }

    fun setIsEditable(isEditable: Boolean) {
        this.isEditable = isEditable
        setupInputType()
    }

    fun setTheme(theme: Theme) {
        this.theme = theme
        setupTheme()
    }

    private fun setupTheme() {
        // Save and restore padding
        val paddingBottom = ui.llFormEditTextRoot.paddingBottom
        val paddingTop = ui.llFormEditTextRoot.paddingTop
        val paddingStart = ui.llFormEditTextRoot.paddingStart
        val paddingEnd = ui.llFormEditTextRoot.paddingEnd

        when (theme) {
            Theme.Dark -> ui.llFormEditTextRoot.setBackgroundResource(darkThemeBackgroundRes)
            Theme.Light -> ui.llFormEditTextRoot.setBackgroundResource(lightThemeBackgroundRes)
        }

        ui.llFormEditTextRoot.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom)
    }


}

const val DefaultMaxTextLength = 60
const val DefaultMaxLines = 1