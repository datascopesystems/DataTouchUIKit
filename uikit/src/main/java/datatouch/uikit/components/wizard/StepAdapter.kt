package datatouch.uikit.components.wizard

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class StepAdapter(private val wizardSteps: WizardStepsContainer, parentFragment: Fragment)
    : FragmentStateAdapter(parentFragment) {

    override fun getItemCount() = wizardSteps.count

    override fun createFragment(position: Int) = wizardSteps.getFragment(position)

}