package datatouch.uikit.components.panels

import androidx.core.view.isInvisible
import datatouch.uikit.R
import datatouch.uikit.components.panels.DurationPicker.InputMode.*
import datatouch.uikit.components.pinpad.PinPadView
import datatouch.uikit.core.callbacks.UiCallback
import datatouch.uikit.core.duration.DurationTime
import datatouch.uikit.databinding.DurationPickerBinding
import java.lang.NumberFormatException

private const val MAX_HOURS_INPUT_VALUE = 9999
private const val MAX_MINUTES_INPUT_VALUE = 99

class DurationPicker(initialDuration: DurationTime) : PopUpPanel() {

    private var ui: DurationPickerBinding? = null
    override val uiBindingProperty = ::ui

    private val colorSelected by lazy { getColor(R.color.accent_negative_start_light) }
    private val colorNotSelected by lazy { getColor(R.color.white) }

    private var mode = INPUT_HOURS

    private var currentValue = ""

    private var valueHours: Int = 0
    private var valueMinutes: Int = 0

    private var duration = DurationTime(initialDuration)

    private var onDurationSelected: UiCallback<DurationTime>? = null

    fun withOnDurationSelected(callback: UiCallback<DurationTime>) = apply {
        onDurationSelected = callback
    }

    override fun onCreationComplete() {
        setupPinPad()
        setupInitialValues()
        setupHoursMinutesClicks()
        setupQuickDurationClicks()
    }

    private fun setupHoursMinutesClicks() {
        ui?.tvHours?.setOnClickListener { switchInputMode(INPUT_HOURS) }
        ui?.tvMinutes?.setOnClickListener { switchInputMode(INPUT_MINUTES) }
    }

    private fun setupQuickDurationClicks() {
        ui?.tv5m?.setOnClickListener { setInitialMinutes(5) }
        ui?.tv15m?.setOnClickListener { setInitialMinutes(15) }
        ui?.tv30m?.setOnClickListener { setInitialMinutes(30) }
        ui?.tv1h?.setOnClickListener { setInitialMinutes(60) }
    }

    private fun setInitialMinutes(minutes: Int) {
        duration = DurationTime(minutes)
        setupInitialValues(INPUT_MINUTES)
    }

    private fun setupPinPad() {
        ui?.pinPad?.displayUserInput()
        ui?.pinPad?.showTitle(false)
        ui?.pinPad?.showUserInput(false)
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
            INPUT_HOURS -> {
                ui?.tvHours?.setTextColor(colorSelected)
                ui?.tvMinutes?.setTextColor(colorNotSelected)

                ui?.hoursUnderline?.isInvisible = false
                ui?.hoursUnderline?.setBackgroundColor(colorSelected)
                ui?.minutesUnderline?.isInvisible = true
                ui?.pinPad?.setOkButtonAppearanceArrowRight()
            }
            INPUT_MINUTES -> {
                ui?.tvHours?.setTextColor(colorNotSelected)
                ui?.tvMinutes?.setTextColor(colorSelected)

                ui?.minutesUnderline?.isInvisible = false
                ui?.minutesUnderline?.setBackgroundColor(colorSelected)
                ui?.hoursUnderline?.isInvisible = true
                ui?.pinPad?.setOkButtonAppearanceDefault()
            }
        }
    }

    private fun setPinPadValue(number: Int) {
        currentValue = if (number == 0) "" else number.toString()
        ui?.pinPad?.clearInput()
        ui?.pinPad?.setValue(number)
    }

    private fun setupInitialValues(inputMode: InputMode = INPUT_HOURS) {
        valueHours = duration.getHours()
        valueMinutes = duration.getMinutes()
        setDurationText(duration)

        switchInputMode(inputMode)
    }

    private fun switchInputMode(newMode: InputMode) {
        mode = newMode
        when (mode) {
            INPUT_HOURS -> setPinPadValue(valueHours)
            INPUT_MINUTES -> setPinPadValue(valueMinutes)
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
        INPUT_HOURS -> MAX_HOURS_INPUT_VALUE
        INPUT_MINUTES -> MAX_MINUTES_INPUT_VALUE
    }

    private fun parseInput(oldInput: String, newInput: String): Int {
        return newInput.runCatching {
            toInt().takeIf { it <= getMaxInputValue() } ?: throw NumberFormatException()
        }.recoverCatching { oldInput.toInt() }.getOrDefault(0)
    }

    private fun validateInput(oldInput: String, newInput: String) {
        when (mode) {
            INPUT_HOURS -> valueHours = parseInput(oldInput, newInput)
            INPUT_MINUTES -> valueMinutes = parseInput(oldInput, newInput)
        }
        updateDuration()
        updatePinpadValue()
    }

    private fun updatePinpadValue() = when (mode) {
        INPUT_HOURS -> setPinPadValue(valueHours)
        INPUT_MINUTES -> setPinPadValue(valueMinutes)
    }

    private fun onOkClick() {
        when (mode) {
            INPUT_HOURS -> {
                switchInputMode(INPUT_MINUTES)
                return
            }
            INPUT_MINUTES -> {
                onDurationSelected?.invoke(duration)
                dismiss()
                return
            }
        }
    }

    private fun clearInput() {
        when (mode) {
            INPUT_HOURS -> valueHours = 0
            INPUT_MINUTES -> valueMinutes = 0
        }

        setPinPadValue(0)
        updateDuration()
    }

    private fun updateDuration() {
        duration.set(valueHours, valueMinutes)
        setDurationText(duration)
    }

    private fun setDurationText(duration: DurationTime) {
        valueHours = duration.getHours()
        valueMinutes = duration.getMinutes()

        ui?.tvHours?.text = valueHours.toString()
                .padStart(2, '0')

        ui?.tvMinutes?.text = valueMinutes.toString()
                .padStart(2, '0')
    }

    enum class InputMode {INPUT_HOURS, INPUT_MINUTES}
}