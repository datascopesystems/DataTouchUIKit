package datatouch.uikit.components.wizard

import androidx.viewpager2.widget.ViewPager2
import datatouch.uikit.components.appbackground.AppBackgroundBundle
import datatouch.uikit.components.appbackground.BlurredAppBackgroundViewDecorator
import datatouch.uikit.components.buttons.DefaultButtonPanel
import datatouch.uikit.components.windows.base.FQuestion
import datatouch.uikit.components.windows.base.FullScreenWindowUiBind
import datatouch.uikit.core.callbacks.UiJustCallback
import datatouch.uikit.core.extensions.BooleanExtensions.ifYes
import datatouch.uikit.core.extensions.IntExtensions.orZero
import datatouch.uikit.core.extensions.ViewPagerExtensions.nextPage
import datatouch.uikit.core.extensions.ViewPagerExtensions.prevPage
import datatouch.uikit.databinding.FragmentWizardBinding

abstract class FWizard : FullScreenWindowUiBind<FragmentWizardBinding, WizardWindowToolbar>() {

    var accentColor = 0
    var toolbarSecondaryText = ""

    var wizardSteps: WizardStepsContainer? = null

    val panel: DefaultButtonPanel by lazy {
        DefaultButtonPanel(context).also {
            it.btnCallback = ::onBtnNextOrAcceptClick
            it.setAcceptButtonVisibility(true)
        }
    }

    var onAllStepsCompleteCallback: UiJustCallback? = null
    var onCloseCallback: UiJustCallback? = null

    override fun afterViewCreated() {
        setCustomOnBackPressCallback()

        wizardSteps?.apply {
            if (any) setupSteps(this)
            else notifyAllStepsCompleteAndDismiss()
        } ?: run {
            notifyAllStepsCompleteAndDismiss()
        }

        windowToolBar?.setSecondaryText(toolbarSecondaryText)
        windowToolBar?.setAccentColor(accentColor)
    }

    private fun setCustomOnBackPressCallback() {
        windowToolBar?.setOnBackButtonClickListener { onBtnBackClick() }
        windowToolBar?.setOnNextButtonClickListener { onBtnNextOrAcceptClick() }
    }

    private fun setupSteps(wizardSteps: WizardStepsContainer) {
        windowToolBar?.showWizardStepper(wizardSteps)
        ui?.vp?.apply {
            isUserInputEnabled = false
            adapter = StepAdapter(wizardSteps, this@FWizard)
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    windowToolBar?.refreshWizardStepperPosition(position)
                }
            })
        }
    }

    private fun hasNextStep(): Boolean {
        val stepPos = ui?.vp?.currentItem
        val stepsCount = wizardSteps?.count
        if (stepPos != null && stepsCount != null)
            return stepPos < stepsCount - 1
        return false
    }

    private fun hasPrevStep(): Boolean {
        val stepPos = ui?.vp?.currentItem
        if (stepPos != null)
            return stepPos > 0
        return false
    }

    private fun isCurrentStepComplete(): Boolean {
        return when (val stepPos = ui?.vp?.currentItem) {
            null -> false
            else -> wizardSteps?.isComplete(stepPos) == true
        }
    }

    private fun onBtnNextOrAcceptClick() {
        hideKeyboard()
        isCurrentStepComplete().ifYes {
            when (hasNextStep()) {
                true -> nextStep()
                false -> showAcceptDataConfirmationPopUp()
            }
        }
    }

    private fun notifyAllStepsCompleteAndDismiss() {
        dismiss()
        onAllStepsCompleteCallback?.invoke()
    }

    private fun nextStep() {
        notifyNextStepChangedIfRequired()
        ui?.vp?.nextPage()
        ui?.vp?.currentItem?.let { windowToolBar?.refreshWizardStepperPosition(it) }
    }

    private fun notifyNextStepChangedIfRequired() {
        val nextStepPosition = ui?.vp?.currentItem.orZero() + 1
        val nextStep = wizardSteps?.getStep(nextStepPosition)

        if (nextStep is RoutableStep<*> && !nextStep.isStepRemainingSame)
            ui?.vp?.adapter?.notifyItemChanged(nextStepPosition)
    }

    private fun onBtnBackClick() {
        when (hasPrevStep()) {
            true -> prevStep()
            false -> showQuitWizardConfirmationPopUp()
        }
    }

    private fun showQuitWizardConfirmationPopUp() {
        FQuitWizardConfirmation().also {
            it.onQuitConfirmedCallback = ::dismiss
        }.show(childFragmentManager)
    }

    private fun showAcceptDataConfirmationPopUp() {
        FQuestion().also {
            it.onAcceptConfirmedCallback = ::notifyAllStepsCompleteAndDismiss
        }.show(childFragmentManager)
    }

    private fun prevStep() {
        ui?.vp?.prevPage()
        ui?.vp?.currentItem?.let { windowToolBar?.refreshWizardStepperPosition(it) }
    }

    override fun provideAppBackgroundDecorators(bundle: AppBackgroundBundle) {
        super.provideAppBackgroundDecorators(bundle)
        bundle.add(BlurredAppBackgroundViewDecorator(ivBackground))
    }

    override fun provideToolbar() = WizardWindowToolbar(context)

    override fun onNavigationBackPress() {
        onBtnBackClick()
    }

    override fun dismiss() {
        onCloseCallback?.invoke()
        super.dismiss()
    }

    override fun onClose() {
        onCloseCallback?.invoke()
    }

}