package datatouch.uikit.components.wizard

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import datatouch.uikit.databinding.WizardStepperViewBinding


class WizardStepperView : LinearLayout {

    private val ui = WizardStepperViewBinding
        .inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes)

    var wizardSteps: WizardStepsContainer = WizardStepsContainer(mutableListOf())
        set(value) {
            field = value
            ui.tvTotalStepsNumber.text = wizardSteps.count.toString()
            ui.tvCurrentStepNumber.text = 0.toString()
            ui.cpb.setProgress(0f)
            ui.tvStepTitle.text = ""
        }

    var currentStepPosition = 0
        set(value) {
            field = value
            val stepNumber = (value + 1)
            ui.tvCurrentStepNumber.text = stepNumber.toString()
            val progress = (stepNumber.toDouble() / wizardSteps.count.toDouble()) * 100
            ui.cpb.setProgressWithAnimation(progress.toFloat())
            ui.tvStepTitle.text = wizardSteps.getName(currentStepPosition)
            checkIfRequiredFontReduction()
        }

    fun setAccentColor(color: Int) {
        ui.cpb.setColor(color)
        ui.tvCurrentStepNumber.setTextColor(color)
        ui.tvStepSubTitle.setTextColor(color)
    }

    fun setSecondaryTitle(secondaryTitle: String) {
        ui.tvStepSubTitle.text = secondaryTitle
    }

    private fun checkIfRequiredFontReduction() {
        ui.tvStepTitle.post {
            ui.tvStepTitle.layout?.let { l ->
                if (l.getEllipsisCount(l.lineCount - 1) > 0)
                    ui.tvStepTitle.textSize = 14F
            }
        }
    }

    val isLastStep get() = currentStepPosition == wizardSteps.count - 1

}