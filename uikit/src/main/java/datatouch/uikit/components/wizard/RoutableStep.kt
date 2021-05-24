package datatouch.uikit.components.wizard


import kotlinx.coroutines.flow.Flow

class RoutableStep<R>(
    private val fragments: List<StepFragment<R>>,
    private val routeFragmentSelector: (List<StepFragment<R>>) -> StepFragment<R>,
) : Step<R>("") {

    override val isComplete: Boolean get() = selectFragment().isComplete
    override val result: Flow<R> get() = selectFragment().result
    override val name get() = selectFragment().name

    override val fragment: SuperWizardFragment<R>
        get() {
            val selectedFragment = selectFragment().fragment
            previousFragment = selectedFragment
            return selectedFragment
        }

    private fun selectFragment() = routeFragmentSelector(fragments)

    private var previousFragment: SuperWizardFragment<R>? = null

    val isStepRemainingSame: Boolean
        get() {
            val newFragment = selectFragment().fragment
            val previousFragmentClass = previousFragment?.javaClass
            val newFragmentClass = newFragment.javaClass
            return newFragmentClass == previousFragmentClass
        }

}