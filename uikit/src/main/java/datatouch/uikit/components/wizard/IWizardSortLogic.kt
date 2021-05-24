package datatouch.uikit.components.wizard

interface IWizardSortLogic {
    fun sortSteps(steps: List<SortableStepFragment<*>>) : MutableList<SortableStepFragment<*>>
}