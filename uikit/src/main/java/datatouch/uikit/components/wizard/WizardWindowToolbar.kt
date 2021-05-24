package datatouch.uikit.components.wizard

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import datatouch.uikit.databinding.WizardWindowToolbarBinding


@SuppressLint("ViewConstructor")
class WizardWindowToolbar(context: Context?) : RelativeLayout(context) {

    private val ui = WizardWindowToolbarBinding
        .inflate(LayoutInflater.from(context), this, true)

    init {
        setupMarginsAndPaddings()
    }

    fun setAccentColor(color: Int) = ui.ws.setAccentColor(color)

    private fun setupMarginsAndPaddings() {
        ui.rootToolbar.setContentInsetsRelative(0, 0)
        ui.rootToolbar.contentInsetStartWithNavigation = 0
        ui.rootToolbar.setContentInsetsAbsolute(0, 0)
        ui.rootToolbar.contentInsetEndWithActions = 0
        ui.rootToolbar.setPadding(0, 0, 0, 0)
        setPadding(0, 0, 0, 0)
    }

    fun setOnBackButtonClickListener(clickListener: OnClickListener?) {
        ui.btnBack.setOnClickListener(clickListener)
    }

    fun setOnNextButtonClickListener(clickListener: OnClickListener?) {
        ui.btnNext.setOnClickListener(clickListener)
        ui.btnAccept.setOnClickListener(clickListener)
    }

    fun showWizardStepper(wizardSteps: WizardStepsContainer) {
        ui.ws.wizardSteps = wizardSteps
        refreshWizardStepperPosition(0)
    }

    fun refreshWizardStepperPosition(position: Int) {
        ui.ws.currentStepPosition = position
        val isLastStep = ui.ws.isLastStep
        ui.btnNext.isVisible = !isLastStep
        ui.btnAccept.isVisible = isLastStep
    }

    fun setSecondaryText(toolbarSecondaryText: String) {
        ui.ws.setSecondaryTitle(toolbarSecondaryText)
    }
}