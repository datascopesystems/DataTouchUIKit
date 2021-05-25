package datatouch.uikit.components.listitems

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import datatouch.uikit.R
import datatouch.uikit.core.extensions.TypedArrayExtensions.getAppCompatDrawable
import datatouch.uikit.core.utils.Conditions.isNotNull
import datatouch.uikit.core.utils.Conditions.isNullOrEmpty
import datatouch.uikit.databinding.MaterialEditTextViewBinding

class MaterialEditText : RelativeLayout {

    private val ui = MaterialEditTextViewBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var rightActionButton = false
    private var rightActionButtonBackground: Drawable? = null
    private var hint: String? = null
    private var text: String? = null
    private var accentColor = 0
    private var singleLine = false
    private var textSize = 0f
    private var textColor = 0
    private var hintAppearance = 0
    private var linesCount = -1
    private var maxTextLength = 0
    private var inputType = 0

    constructor(
        context: Context?,
        attrs: AttributeSet
    ) : super(context, attrs) {
        parseAttributes(attrs)
    }

    fun fullyDisable() {
        editText.isEnabled = false
        editText.isClickable = false
        editText.isFocusable = false
    }

    private enum class InputType(val value: Int) {
        TEXT(0), NUMBER(1), PASSWORD(2), TEXT_MULTILINE(3);

        companion object {
            fun fromValue(value: Int): InputType {
                return when (value) {
                    0 -> TEXT
                    1 -> NUMBER
                    2 -> PASSWORD
                    3 -> TEXT_MULTILINE
                    else -> TEXT
                }
            }
        }

    }

    fun clearText() {
        ui.etText.setText("")
    }

    private fun parseAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.MaterialEditText, 0, 0
        )
        try {
            rightActionButton =
                typedArray.getBoolean(R.styleable.MaterialEditText_rightActionButton, false)
            rightActionButtonBackground =
                typedArray.getAppCompatDrawable(context, R.styleable.MaterialEditText_rightActionButtonBackground)
            hint = typedArray.getString(R.styleable.MaterialEditText_hint)
            text = typedArray.getString(R.styleable.MaterialEditText_text)
            maxTextLength = typedArray.getInteger(
                R.styleable.MaterialEditText_maxTextLength,
                DEFAULT_TEXT_LENGTH
            )
            accentColor = typedArray.getColor(
                R.styleable.MaterialEditText_accentColor,
                ContextCompat.getColor(context, R.color.accent)
            )
            singleLine = typedArray.getBoolean(R.styleable.MaterialEditText_singleLine, false)
            textSize = typedArray.getDimension(R.styleable.MaterialEditText_textSize, 17f)
            textColor = typedArray.getColor(
                R.styleable.MaterialEditText_textColor,
                ContextCompat.getColor(context, R.color.primary_text)
            )
            linesCount = typedArray.getInteger(R.styleable.MaterialEditText_lines, -1)
            hintAppearance = typedArray.getResourceId(
                R.styleable.MaterialEditText_hintColor,
                R.style.BaseHintAppearance_SecondaryText
            )
            inputType = typedArray.getInteger(
                R.styleable.MaterialEditText_inputType,
                InputType.TEXT.value
            )
        } finally {
            typedArray.recycle()
        }
    }

    constructor(
        context: Context?,
        attrs: AttributeSet,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        parseAttributes(attrs)
    }

    constructor(context: Context?) : super(context) {}

    fun setError(errorMessage: String?) {
        ui.tilText.error = errorMessage
    }

    fun hideError() {
        ui.tilText.error = null
    }

    override fun hasFocus(): Boolean {
        return isNotNull(ui.etText) && ui.etText.hasFocus()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        applyAttributes()
    }

    private fun applyAttributes() {
        ui.btnAction.visibility = if (rightActionButton) View.VISIBLE else View.GONE
        ui.btnAction.background = rightActionButtonBackground
        ui.tilText.hint = hint
        ui.etText.setText(text)
        ui.etText.background?.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN)
        ui.etText.isSingleLine = singleLine
        ui.etText.textSize = textSize
        ui.etText.filters = arrayOf<InputFilter>(LengthFilter(maxTextLength))
        ui.etText.setTextColor(textColor)
        setupInputType()
        ui.tilText.setHintTextAppearance(hintAppearance)
        if (1 == linesCount) ui.etText.setSingleLine() else if (linesCount > 1) ui.etText.setLines(
            linesCount
        )
        setDefaultActionButtonCallback()
    }

    private fun setDefaultActionButtonCallback() {
        setOnActionButtonListener { clearText() }
    }

    fun afterTextChanged(callback: AfterTextChangedCallback?) {
        ui.etText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable) {
                callback?.afterChanged(ui.etText.text.toString())
            }
        })
    }

    interface AfterTextChangedCallback {
        fun afterChanged(text: String?)
    }

    private fun setupInputType() {
        when (InputType.fromValue(
            inputType
        )) {
            InputType.TEXT -> ui.etText.inputType = android.text.InputType.TYPE_CLASS_TEXT
            InputType.TEXT_MULTILINE -> ui.etText.inputType =
                android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
            InputType.NUMBER -> ui.etText.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            InputType.PASSWORD -> ui.etText.inputType =
                android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }

    val editText: EditText
        get() = ui.etText

    fun setTextSize(size: Float) {
        ui.etText.textSize = size
    }

    fun setTextMoveCaretEnd(text: String?) {
        ui.etText.setText(text)
        ui.etText.setSelection(ui.etText.text!!.length)
    }

    fun setText(text: String?) {
        ui.etText.setText(text)
    }

    val trimmedString: String
        get() = ui.etText.text.toString().trim { it <= ' ' }

    val trimmedText: CharSequence
        get() = ui.etText.text.toString().trim { it <= ' ' }

    fun getText(): Editable? {
        return ui.etText.text
    }

    val textStyle: Int
        get() = ui.etText.typeface?.style ?: 0

    fun getTextColor(): Int {
        return ui.etText.currentTextColor
    }

    fun setSelection(index: Int) {
        ui.etText.setSelection(index)
    }

    val isEmpty: Boolean
        get() = isNullOrEmpty(trimmedText)

    fun setOnActionButtonListener(onActionButtonListener: OnClickListener?) {
        ui.btnAction.setOnClickListener(onActionButtonListener)
    }

    fun addTextChangedListener(watcher: TextWatcher?) {
        ui.etText.addTextChangedListener(watcher)
    }

    fun removeTextChangedListener(watcher: TextWatcher?) {
        ui.etText.removeTextChangedListener(watcher)
    }

    fun setEditable(editable: Boolean) {
        ui.etText.isEnabled = editable
    }

    @get:Throws(NumberFormatException::class)
    val currentInputAsInteger: Int
        get() = Integer.valueOf(ui.etText.text.toString())

    companion object {
        private const val DEFAULT_TEXT_LENGTH = 60
    }
}