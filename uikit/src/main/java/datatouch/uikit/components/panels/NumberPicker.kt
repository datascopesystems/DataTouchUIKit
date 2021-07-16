package datatouch.uikit.components.panels


import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import datatouch.uikit.R
import datatouch.uikit.components.panels.NumberPicker.InputMode.*
import datatouch.uikit.components.pinpad.PinPadView
import datatouch.uikit.core.callbacks.UiCallback
import datatouch.uikit.databinding.NumberPickerBinding
import java.lang.NumberFormatException
import kotlin.math.absoluteValue


private const val MAX_INT_PART_INPUT_VALUE = 999999
private const val MAX_FLOAT_PART_VALUE = 999

class NumberPicker : PopUpPanel {

    private var ui: NumberPickerBinding? = null
    override val uiBindingProperty = ::ui

    private val colorSelected by lazy { getColor(R.color.accent_negative_start_light) }
    private val colorNotSelected by lazy { getColor(R.color.white) }

    private var isChangeSignAllowed = false

    private var mode = INPUT_INT_PART

    private var currentValue = ""

    private var valueIntPart: Int = 0
    private var valueFloatPart: Int = 0

    private var numberParts: NumberParts

    private var onNumberIntSelected: UiCallback<Int>? = null
    private var onNumberFloatSelected: UiCallback<Float>? = null

    constructor(initialValue: Int) : super() {
        numberParts = NumberParts(initialValue)
        mode = INPUT_ONLY_INT
    }

    constructor(initialValue: Float) : super() {
        numberParts = NumberParts(initialValue)
    }

    fun withOnIntSelected(callback: UiCallback<Int>) = apply {
        onNumberIntSelected = callback
    }

    fun withOnFloatSelected(callback: UiCallback<Float>) = apply {
        onNumberFloatSelected = callback
    }

    fun withChangeSignAllowed(allowed: Boolean) = apply {
        isChangeSignAllowed = allowed
    }

    override fun onCreationComplete() {
        setupPinPad()
        setupInitialValues(mode)
        setupIntFloatPartsClicks()
        setupSignChangeToolButton()
        setupToolsClicks()
    }

    private fun setupIntFloatPartsClicks() {
        if (mode != INPUT_ONLY_INT) {
            ui?.tvIntPart?.setOnClickListener { switchInputMode(INPUT_INT_PART) }
            ui?.tvFloatPart?.setOnClickListener { switchInputMode(INPUT_FLOAT_PART) }
        }
    }

    private fun setupSignChangeToolButton() {
        ui?.tvChangeSign?.isVisible = isChangeSignAllowed
    }

    private fun setupToolsClicks() {
        ui?.ivIncrement?.setOnClickListener { increment(1) }
        ui?.ivDecrement?.setOnClickListener { increment(-1) }
        ui?.ivBackspace?.setOnClickListener { backspace() }

        if (isChangeSignAllowed) {
            ui?.tvChangeSign?.setOnClickListener { changeSign() }
        }
    }

    private fun increment(value: Int) {
        when (mode) {
            INPUT_ONLY_INT -> numberParts.addIntPart(value)
            INPUT_INT_PART -> numberParts.addIntPart(value)
            INPUT_FLOAT_PART -> numberParts.addFloatPart(value)
        }
        setupInitialValues(mode)
    }

    private fun backspace() {
        when (mode) {
            INPUT_ONLY_INT -> numberParts.backspaceIntPart()
            INPUT_INT_PART -> numberParts.backspaceIntPart()
            INPUT_FLOAT_PART -> numberParts.backspaceFloatPart()
        }
        setupInitialValues(mode)
    }

    private fun changeSign() {
        numberParts.changeSign()
        setupInitialValues(mode)
    }

    private fun setupPinPad() {
        ui?.pinPad?.displayUserInput()
        ui?.pinPad?.showTitle(false)
        ui?.pinPad?.showUserInput(false)
        ui?.pinPad?.setOkButtonAppearanceDefault()
        ui?.pinPad?.setOnEnteredNumberChangedCallback(object : PinPadView.OnEnteredNumberChangedCallback {
            override fun numberChanged(number: String?) {
                number?.let { onNumberChanged(it) }
            }

            override fun onEmptyInputAccept() {
            }
        })

        ui?.pinPad?.setOnOkClickListener(object : PinPadView.OnOkClickListener {
            override fun onOkClicked(input: String?) {
                onOkClick()
            }
        })
    }

    private fun updateSelection() {
        when (mode) {
            INPUT_ONLY_INT -> {
                ui?.tvIntPart?.setTextColor(colorSelected)
                ui?.tvFloatPart?.setTextColor(colorNotSelected)
                ui?.tvFloatPart?.isVisible = false
                ui?.intPartUnderline?.isInvisible = true
                ui?.floatPartUnderline?.isInvisible = true
                ui?.tvDot?.isVisible = false
            }

            INPUT_INT_PART -> {
                ui?.tvIntPart?.setTextColor(colorSelected)
                ui?.tvFloatPart?.setTextColor(colorNotSelected)
                ui?.tvFloatPart?.isVisible = true

                ui?.intPartUnderline?.isInvisible = false
                ui?.intPartUnderline?.setBackgroundColor(colorSelected)
                ui?.floatPartUnderline?.isInvisible = true

                ui?.tvDot?.isVisible = true
            }

            INPUT_FLOAT_PART -> {
                ui?.tvIntPart?.setTextColor(colorNotSelected)
                ui?.tvFloatPart?.setTextColor(colorSelected)
                ui?.tvFloatPart?.isVisible = true

                ui?.floatPartUnderline?.isInvisible = false
                ui?.floatPartUnderline?.setBackgroundColor(colorSelected)
                ui?.intPartUnderline?.isInvisible = true

                ui?.tvDot?.isVisible = true
            }
        }
    }

    private fun setPinPadValue(number: Int) {
        currentValue = if (number == 0) "" else number.toString()
        ui?.pinPad?.clearInput()
        ui?.pinPad?.setValue(number)
    }

    private fun setupInitialValues(inputMode: InputMode) {
        valueIntPart = numberParts.intPart
        valueFloatPart = numberParts.floatPart
        setNumberText(numberParts)

        switchInputMode(inputMode)
    }

    private fun switchInputMode(newMode: InputMode) {
        mode = newMode
        when (mode) {
            INPUT_ONLY_INT -> setPinPadValue(valueIntPart)
            INPUT_INT_PART -> setPinPadValue(valueIntPart)
            INPUT_FLOAT_PART -> setPinPadValue(valueFloatPart)
        }
        updateSelection()
    }

    private fun onNumberChanged(number: String) {
        if (number.length < currentValue.length) {
            clearInput()
            return
        }

        if (number.length > currentValue.length) {
            validateInput(currentValue, number)
        }
    }

    private fun getMaxInputValue(): Int = when (mode) {
        INPUT_ONLY_INT -> MAX_INT_PART_INPUT_VALUE
        INPUT_INT_PART -> MAX_INT_PART_INPUT_VALUE
        INPUT_FLOAT_PART -> MAX_FLOAT_PART_VALUE
    }

    private fun parseInput(oldInput: String, newInput: String): Int {
        return newInput.runCatching {
            toInt().takeIf { it <= getMaxInputValue() } ?: throw NumberFormatException()
        }.recoverCatching { oldInput.toInt() }.getOrDefault(0)
    }

    private fun validateInput(oldInput: String, newInput: String) {
        when (mode) {
            INPUT_ONLY_INT -> valueIntPart = parseInput(oldInput, newInput)
            INPUT_INT_PART -> valueIntPart = parseInput(oldInput, newInput)
            INPUT_FLOAT_PART -> valueFloatPart = parseInput(oldInput, newInput)
        }
        updateNumber()
        updatePinpadValue()
    }

    private fun updatePinpadValue() = when (mode) {
        INPUT_ONLY_INT -> setPinPadValue(valueIntPart)
        INPUT_INT_PART -> setPinPadValue(valueIntPart)
        INPUT_FLOAT_PART -> setPinPadValue(valueFloatPart)
    }

    private fun onOkClick() {
        onNumberIntSelected?.invoke(numberParts.getIntValue())
        onNumberFloatSelected?.invoke(numberParts.getFloatValue())
        dismiss()
    }

    private fun clearInput() {
        when (mode) {
            INPUT_ONLY_INT -> valueIntPart = 0
            INPUT_INT_PART -> valueIntPart = 0
            INPUT_FLOAT_PART -> valueFloatPart = 0
        }

        setPinPadValue(0)
        updateNumber()
    }

    private fun updateNumber() {
        numberParts.set(valueIntPart, valueFloatPart)
        setNumberText(numberParts)
    }

    private fun setNumberText(np: NumberParts) {
        valueIntPart = np.intPart
        valueFloatPart = np.floatPart

        val intPartText = if (np.isSignNegative()) "-$valueIntPart" else valueIntPart.toString()

        ui?.tvIntPart?.text =  intPartText
            .padStart(1, '0')

        ui?.tvFloatPart?.text = valueFloatPart.toString()
            .padStart(1, '0')
    }

    enum class InputMode {INPUT_ONLY_INT, INPUT_INT_PART, INPUT_FLOAT_PART}

    private class NumberParts {
        var intPart: Int = 0
            private set

        var floatPart: Int = 0
            private set

        private var sign: Int = 1

        constructor(initialValue: Float) {
            setupInitialValue(initialValue)
            sign = if (initialValue < 0) -1 else 1
        }

        constructor(initialValue: Int) {
            intPart = initialValue.absoluteValue
            sign = if (initialValue < 0) -1 else 1
        }

        private fun setupInitialValue(initialValue: Float) {
            val tokens = initialValue.toString().split(".")

            if (tokens.size >= 2) {
                intPart = tokens[0].toInt().absoluteValue
                floatPart = tokens[1].toInt().absoluteValue
                return
            }

            if (tokens.isNotEmpty()) {
                intPart = tokens[0].toInt().absoluteValue
                return
            }
        }

        fun set(intPart: Int, floatPart: Int) {
            this.intPart = intPart
            this.floatPart = floatPart
        }

        fun getIntValue(): Int = intPart * sign

        fun getFloatValue(): Float {
            val s = "$intPart.$floatPart"
            return s.toFloat() * sign
        }

        fun addIntPart(value: Int) {
            val tmp = intPart + value
            intPart = when {
                tmp >= MAX_INT_PART_INPUT_VALUE -> 0
                tmp <= 0 -> 0
                else -> tmp
            }
        }

        fun addFloatPart(value: Int) {
            val tmp = floatPart + value
            floatPart = when {
                tmp >= MAX_FLOAT_PART_VALUE -> 0
                tmp <= 0 -> 0
                else -> tmp
            }
        }

        fun backspaceIntPart() {
            val s = intPart.toString().dropLast(1)
            intPart = when (s.isEmpty()) {
                true -> 0
                else -> s.toInt()
            }
        }

        fun backspaceFloatPart() {
            val s = floatPart.toString().dropLast(1)
            floatPart = when (s.isEmpty()) {
                true -> 0
                else -> s.toInt()
            }
        }

        fun changeSign() {
            sign *= -1
        }

        fun isSignNegative(): Boolean = sign < 0

        override fun toString(): String {
            return when (sign >= 0) {
                true -> "$intPart.$floatPart"
                else -> "-$intPart.$floatPart"
            }
        }
    }
}