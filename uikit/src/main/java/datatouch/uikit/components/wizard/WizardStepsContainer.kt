package datatouch.uikit.components.wizard

import datatouch.uikit.core.extensions.CollectionExtensions.partitionByType
import java.io.Serializable


class WizardStepsContainer(private var steps: MutableList<Step<*>> = mutableListOf()) : IWizardStepsProvider,
    Serializable {

    override val containerSteps = this

    fun applySortLogic(sortLogic: IWizardSortLogic, sortableStepsFirst: Boolean = true) = apply {
        if (requiredSorting()) {
            val (sortableSteps, notSortableSteps) = steps.partitionByType<SortableStepFragment<*>, Step<*>>()
            steps = when (sortableStepsFirst) {
                true -> sortLogic.sortSteps(sortableSteps).plus(notSortableSteps)
                false -> notSortableSteps.plus(sortLogic.sortSteps(sortableSteps))
            }.toMutableList()
        }
    }

    private fun requiredSorting() = steps.size > 1 && steps.any { it is SortableStepFragment }

    override suspend fun <R> collectResultAsync(onResultCollected: suspend R.() -> Unit) {
        @Suppress("UNCHECKED_CAST")
        onResultCollected.invoke(steps.map { it.stepResultAsync() } as R)
    }

    fun plusStep(step: Step<*>) = apply { steps.add(step) }
    fun getFragment(position: Int) = steps[position].fragment
    fun isComplete(position: Int) = steps[position].isComplete
    fun getName(position: Int) = steps[position].name

    val count get() = steps.size
    val any get() = steps.any()

    fun getStep(position: Int) = steps[position]
}