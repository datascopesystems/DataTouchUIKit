package datatouch.uikit.components.listitems

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import datatouch.uikit.R
import datatouch.uikit.utils.Conditions.isNotNull
import datatouch.uikit.utils.Conditions.isNullOrEmpty
import kotlinx.android.synthetic.main.material_edit_text_view.view.*

class MaterialEditText : RelativeLayout {

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
        inflateView()
        parseAttributes(attrs)
        afterViews()
    }

    protected fun inflateView() {
        View.inflate(context, R.layout.material_edit_text_view, this)
    }

    fun fullyDisable() {
        editText?.isEnabled = false
        editText?.isClickable = false
        editText?.isFocusable = false
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
        etText?.setText("")
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
                typedArray.getDrawable(R.styleable.MaterialEditText_rightActionButtonBackground)
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
        tilText?.error = errorMessage
    }

    fun hideError() {
        tilText?.error = null
    }

    override fun hasFocus(): Boolean {
        return isNotNull(etText) && etText!!.hasFocus()
    }

    fun afterViews() {
        applyAttributes()
    }

    private fun applyAttributes() {
        btnAction?.visibility = if (rightActionButton) View.VISIBLE else View.GONE
        btnAction?.background = rightActionButtonBackground
        tilText?.hint = hint
        etText?.setText(text)
        etText?.background?.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN)
        etText?.isSingleLine = singleLine
        etText?.textSize = textSize
        etText?.filters = arrayOf<InputFilter>(LengthFilter(maxTextLength))
        etText?.setTextColor(textColor)
        setupInputType()
        tilText?.setHintTextAppearance(hintAppearance)
        if (1 == linesCount) etText?.setSingleLine() else if (linesCount > 1) etText?.setLines(
            linesCount
        )
        setDefaultActionButtonCallback()
    }

    private fun setDefaultActionButtonCallback() {
        setOnActionButtonListener(OnClickListener { v: View? -> clearText() })
    }

    fun afterTextChanged(callback: AfterTextChangedCallback?) {
        etText?.addTextChangedListener(object : TextWatcher {
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
                callback?.afterChanged(etText!!.text.toString())
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
            InputType.TEXT -> etText?.inputType = android.text.InputType.TYPE_CLASS_TEXT
            InputType.TEXT_MULTILINE -> etText?.inputType =
                android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
            InputType.NUMBER -> etText?.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            InputType.PASSWORD -> etText?.inputType =
                android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }

    val editText: EditText?
        get() = etText

    fun setTextSize(size: Float) {
        etText?.textSize = size
    }

    fun setTextMoveCaretEnd(text: String?) {
        etText?.setText(text)
        etText?.setSelection(etText!!.text!!.length)
    }

    fun setText(text: String?) {
        etText?.setText(text)
    }

    val trimmedString: String
        get() = etText?.text.toString().trim { it <= ' ' }

    val trimmedText: CharSequence
        get() = etText?.text.toString().trim { it <= ' ' }

    fun getText(): Editable? {
        return etText?.text
    }

    val textStyle: Int
        get() = etText?.typeface?.style ?: 0

    fun getTextColor(): Int {
        return etText?.currentTextColor ?: 0
    }

    fun setSelection(index: Int) {
        etText?.setSelection(index)
    }

    val isEmpty: Boolean
        get() = isNullOrEmpty(trimmedText)

    fun setOnActionButtonListener(onActionButtonListener: OnClickListener?) {
        btnAction?.setOnClickListener(onActionButtonListener)
    }

    fun addTextChangedListener(watcher: TextWatcher?) {
        etText?.addTextChangedListener(watcher)
    }

    fun removeTextChangedListener(watcher: TextWatcher?) {
        etText?.removeTextChangedListener(watcher)
    }

    fun setEditable(editable: Boolean) {
        etText?.isEnabled = editable
    }

    @get:Throws(NumberFormatException::class)
    val currentInputAsInteger: Int
        get() = Integer.valueOf(etText!!.text.toString())

    companion object {
        private const val DEFAULT_TEXT_LENGTH = 60
    }
}