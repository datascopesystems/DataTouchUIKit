package datatouch.uikit.components.pinpad

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.util.Consumer
import androidx.core.view.isVisible
import datatouch.uikit.R
import datatouch.uikit.components.CCircleCheckBox
import datatouch.uikit.core.utils.Conditions.isNotNull
import datatouch.uikit.core.utils.ResourceUtils.convertDpToPixel
import datatouch.uikit.core.utils.views.ViewUtils.iterateOverMultipleChildViews
import datatouch.uikit.databinding.PinPadBinding
import java.util.*

class PinPadView :
    RelativeLayout {

    private val ui = PinPadBinding
        .inflate(LayoutInflater.from(context), this, true)

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
    }

    constructor(
        context: Context?,
        attrs: AttributeSet
    ) : super(context, attrs) {
        parseAttributes(attrs)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        parseAttributes(attrs)
    }

    fun displayUserInput() {
        ui.llCheckBoxesContainer.visibility = View.GONE
        ui.etAmount.visibility = View.VISIBLE
    }

    fun showUserInput(show: Boolean) {
        ui.etAmount.isVisible = show
    }

    fun setOnClickListeners() {
        ui.btn1.setOnClickListener { onPadButtonClick(it) }
        ui.btn2.setOnClickListener { onPadButtonClick(it) }
        ui.btn3.setOnClickListener { onPadButtonClick(it) }
        ui.btn4.setOnClickListener { onPadButtonClick(it) }
        ui.btn5.setOnClickListener { onPadButtonClick(it) }
        ui.btn6.setOnClickListener { onPadButtonClick(it) }
        ui.btn7.setOnClickListener { onPadButtonClick(it) }
        ui.btn8.setOnClickListener { onPadButtonClick(it) }
        ui.btn9.setOnClickListener { onPadButtonClick(it) }
        ui.btn0.setOnClickListener { onPadButtonClick(it) }
        ui.btnOk.setOnClickListener { btnOk() }
        ui.btnCancel.setOnClickListener { btnCancel() }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setOnClickListeners()
        setupCheckboxes()
        setButtonSize()
    }

    private fun setButtonSize() {
        iterateOverMultipleChildViews(
            this,
            buttonViewClasses,
            Consumer { button ->
                val layoutParams: ViewGroup.LayoutParams? = button?.layoutParams
                layoutParams?.height = buttonSize.toInt()
                layoutParams?.width = buttonSize.toInt()
                button?.layoutParams = layoutParams
            }
        )
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
            ui.llCheckBoxesContainer.addView(cCircleCheckBox)
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
        ui.btnOk.visibility = View.INVISIBLE
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
        ui.etAmount.setText(currentInput)
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
        get() {
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
        ui.etAmount.setText(currentInput)
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
        get() {
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
        ui.btnOk.visibility = View.VISIBLE
        ui.pbLoading.visibility = View.GONE
    }

    fun showLoadingState() {
        isInEditingState = false
        ui.btnOk.visibility = View.INVISIBLE
        ui.pbLoading.visibility = View.VISIBLE
    }

    fun setTitle(title: String?) {
        ui.tvTitle.visibility = View.VISIBLE
        ui.tvTitle.text = title
    }

    fun showTitle(show: Boolean) {
        ui.tvTitle.isVisible = show
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
            ui.etAmount.setText(currentInput)
        } catch (ex: Exception) {
            emptyfy()
        }
    }

    private fun emptyfy() {
        ui.etAmount.setText("")
        currentInput = ""
    }

    fun readOnly() {
        isReadOnly = true
    }

    fun setOkButtonAppearanceDefault() {
        ui.btnOk.setAppearanceDefault()
    }

    fun setOkButtonAppearanceArrowRight() {
        ui.btnOk.setAppearanceArrowRight()
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