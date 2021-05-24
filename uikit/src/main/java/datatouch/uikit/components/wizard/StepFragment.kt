package datatouch.uikit.components.wizard

open class StepFragment<R>(fragment: SuperWizardFragment<R>, name: String)
    : Step<R>(name), IWizardStep<R> by fragment