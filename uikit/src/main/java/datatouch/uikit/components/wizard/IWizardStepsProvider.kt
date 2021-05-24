package datatouch.uikit.components.wizard

interface IWizardStepsProvider {
    val containerSteps: WizardStepsContainer
    suspend fun <R> collectResultAsync(onResultCollected: suspend R.() -> Unit)
}