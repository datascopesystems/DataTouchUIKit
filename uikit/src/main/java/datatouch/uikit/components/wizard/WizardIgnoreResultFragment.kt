package datatouch.uikit.components.wizard

abstract class WizardIgnoreResultFragment : WizardCompletableFragment<Any>() {

    override fun getStepResult(): StepFragmentResult {
        setStepResultTo(Any())
        return StepFragmentResult.Complete
    }

}