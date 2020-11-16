package datatouch.uikit.components.pinpad

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.util.Consumer
import datatouch.uikit.R
import datatouch.uikit.components.CCircleCheckBox
import datatouch.uikit.core.utils.Conditions.isNotNull
import datatouch.uikit.core.utils.ResourceUtils.convertDpToPixel
import datatouch.uikit.core.utils.views.ViewUtils.iterateOverMultipleChildViews
import kotlinx.android.synthetic.main.pin_pad.view.*
import java.util.*

class PinPadView :
    RelativeLayout {

    var currentInput = ""
    private val buttonViewClasses =
        Arrays.asList<Class<*>?>(
            CPinPadNumberButton::class.java,
            CPinPadOkButton::class.java,
            CPinPadCancelButton::class.java
        )
    private var onActionClickListener: OnOkClickListener? = null
    private var isInEditingState = true
    private var circleCheckBoxesCount = DEFAULT_CHECKBOXES_COUNT_COUNT
    private val circleCheckBoxes: MutableList<CCircleCheckBox> =
        ArrayList()
    private var numberChangedCallback: OnEnteredNumberChangedCallback? = null
    private var isReadOnly = false
    private var buttonSize = 0f
    private fun parseAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.PinPadView, 0, 0
        )
        try {
            circleCheckBoxesCount = typedArray.getInteger(
                R.styleable.PinPadView_checkBoxesCount,
                DEFAULT_CHECKBOXES_COUNT_COUNT
            )
            buttonSize = typedArray.getDimension(
                R.styleable.PinPadView_singleButtonSize,
                DEFAULT_BUTTON_SIZE_DP
            )
        } finally {
            typedArray.recycle()
        }
    }

    constructor(context: Context?) : super(context) {
        inflateView()
        afterView()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet
    ) : super(context, attrs) {
        inflateView()
        parseAttributes(attrs)
        afterView()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        inflateView()
        parseAttributes(attrs)
        afterView()
    }

    fun displayUserInput() {
        llCheckBoxesContainer?.visibility = View.GONE
        etAmount?.visibility = View.VISIBLE
    }

    fun setOnClickListeners() {
        btn1.setOnClickListener { onPadButtonClick(it) }
        btn2.setOnClickListener { onPadButtonClick(it) }
        btn3.setOnClickListener { onPadButtonClick(it) }
        btn4.setOnClickListener { onPadButtonClick(it) }
        btn5.setOnClickListener { onPadButtonClick(it) }
        btn6.setOnClickListener { onPadButtonClick(it) }
        btn7.setOnClickListener { onPadButtonClick(it) }
        btn8.setOnClickListener { onPadButtonClick(it) }
        btn9.setOnClickListener { onPadButtonClick(it) }
        btn0.setOnClickListener { onPadButtonClick(it) }
        btnOk?.setOnClickListener { btnOk() }
        btnCancel?.setOnClickListener { btnCancel() }
    }

    fun afterView() {
        setOnClickListeners()
        setupCheckboxes()
        setButtonSize()
    }

    private fun setButtonSize() {
        iterateOverMultipleChildViews(
            this,
            buttonViewClasses,
            Consumer { button ->
                val layoutParams: ViewGroup.LayoutParams? = button?.getLayoutParams()
                layoutParams?.height = buttonSize.toInt()
                layoutParams?.width = buttonSize.toInt()
                button?.layoutParams = layoutParams
            }
        )
    }

    protected fun inflateView() {
        View.inflate(context, R.layout.pin_pad, this)
    }

    fun resize(newSize: Int) {
        iterateOverMultipleChildViews(
            this,
            buttonViewClasses,
            Consumer { button ->
                val layoutParams: ViewGroup.LayoutParams? = button?.layoutParams
                layoutParams?.height = newSize
                layoutParams?.width = newSize
                button?.layoutParams = layoutParams
                try {
                    (button as IResizeable).setSize(
                        (newSize * DEFAULT_PERCENT_FACTOR)
                    )
                } catch (ex: Exception) {
                }
            }
        )
    }

    private fun setupCheckboxes() {
        require(circleCheckBoxesCount >= 1) { "Circle check boxes count should be greater than 0" }
        for (i in 1..circleCheckBoxesCount) {
            val cCircleCheckBox = CCircleCheckBox(context)
            llCheckBoxesContainer?.addView(cCircleCheckBox)
            circleCheckBoxes.add(cCircleCheckBox)
            if (i != circleCheckBoxesCount) {
                val vlp =
                    cCircleCheckBox.layoutParams as MarginLayoutParams
                if (isNotNull(vlp)) vlp.rightMargin = convertDpToPixel(
                    context,
                    DEFAULT_CHECK_BOXES_MARGIN.toFloat()
                ).toInt()
            }
        }
    }

    fun hideOkButton() {
        btnOk?.visibility = View.INVISIBLE
    }

    fun btnOk() {
        if (!isInEditingState) return
        if (isReadOnly) return
        if (isNotNull(onActionClickListener)) onActionClickListener?.onOkClicked(
            currentInput
        )
    }

    fun clearInput() {
        currentInput = ""
        uncheckAllCircles()
    }

    fun onPadButtonClick(clickedButton: View) {
        if (!isInEditingState) return
        if (isReadOnly) return
        appendNumberToPinCode((clickedButton as CPinPadNumberButton).buttonNumber)
        etAmount?.setText(currentInput)
        appendCheckedCircle()
        notifyNumberChanged()
    }

    private fun notifyNumberChanged() {
        if (isNotNull(numberChangedCallback)) numberChangedCallback?.numberChanged(
            currentInput
        )
    }

    private fun appendNumberToPinCode(number: Int) {
        currentInput = currentInput + number
    }

    private fun appendCheckedCircle() {
        val nextUncheckedCircle = nextUncheckedCircle
        if (isNotNull(nextUncheckedCircle)) {
            nextUncheckedCircle?.setChecked(true)
        } else {
            uncheckAllCircles()
            circleCheckBoxes[0].setChecked(true)
        }
    }

    private val nextUncheckedCircle: CCircleCheckBox?
        private get() {
            for (cb in circleCheckBoxes) if (!cb.isChecked()) return cb
            return null
        }

    private fun uncheckAllCircles() {
        for (cb in circleCheckBoxes) cb.setChecked(false)
    }

    fun btnCancel() {
        if (!isInEditingState) return
        if (isReadOnly) return
        deleteLastInputLastCharacter()
        uncheckLastCircle()
        etAmount?.setText(currentInput)
        notifyNumberChanged()
    }

    private fun uncheckLastCircle() {
        val lastCheckedCircle = lastCheckedCircle
        if (isNotNull(lastCheckedCircle)) {
            lastCheckedCircle?.setChecked(false)
        } else {
            if ("" != currentInput) {
                checkAllCircles()
                circleCheckBoxes[circleCheckBoxesCount - 1].setChecked(false)
            } else {
                if (isNotNull(numberChangedCallback)) {
                    numberChangedCallback?.onEmptyInputAccept()
                }
            }
        }
    }

    private val lastCheckedCircle: CCircleCheckBox?
        private get() {
            for (i in circleCheckBoxesCount - 1 downTo 0) {
                val lastCircle = circleCheckBoxes[i]
                if (lastCircle.isChecked()) return lastCircle
            }
            return null
        }

    private fun checkAllCircles() {
        for (cb in circleCheckBoxes) cb.setChecked(false)
    }

    private fun deleteLastInputLastCharacter() {
        val length = currentInput.length
        if (length > 0) currentInput = currentInput.substring(0, currentInput.length - 1)
    }

    fun setOnOkClickListener(onActionClickListener: OnOkClickListener?) {
        this.onActionClickListener = onActionClickListener
    }

    fun showEditingState() {
        isInEditingState = true
        btnOk?.visibility = View.VISIBLE
        pbLoading?.visibility = View.GONE
    }

    fun showLoadingState() {
        isInEditingState = false
        btnOk?.visibility = View.INVISIBLE
        pbLoading?.visibility = View.VISIBLE
    }

    fun setTitle(title: String?) {
        tvTitle?.visibility = View.VISIBLE
        tvTitle?.text = title
    }

    fun setOnEnteredNumberChangedCallback(numberChangedCallback: OnEnteredNumberChangedCallback?) {
        this.numberChangedCallback = numberChangedCallback
    }

    fun setValue(initValue: Int) {
        try {
            if (initValue == 0) {
                emptyfy()
                return
            }
            currentInput = initValue.toString()
            etAmount?.setText(currentInput)
        } catch (ex: Exception) {
            emptyfy()
        }
    }

    private fun emptyfy() {
        etAmount?.setText("")
        currentInput = ""
    }

    fun readOnly() {
        isReadOnly = true
    }

    interface OnOkClickListener {
        fun onOkClicked(input: String?)
    }

    interface OnEnteredNumberChangedCallback {
        fun numberChanged(number: String?)
        fun onEmptyInputAccept()
    }

    companion object {
        private const val DEFAULT_CHECKBOXES_COUNT_COUNT = 6
        private const val DEFAULT_CHECK_BOXES_MARGIN = 25
        private const val DEFAULT_BUTTON_SIZE_DP = 85f

        // Required for text to be smaller then circle around single button
        private const val DEFAULT_PERCENT_FACTOR = 0.4f
    }

}