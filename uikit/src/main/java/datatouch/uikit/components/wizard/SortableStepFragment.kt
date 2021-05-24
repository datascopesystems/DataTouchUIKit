package datatouch.uikit.components.wizard

class SortableStepFragment<R>(
    val position: Int,
    name: String,
    fragment: SuperWizardFragment<R>) : StepFragment<R>(fragment, name)