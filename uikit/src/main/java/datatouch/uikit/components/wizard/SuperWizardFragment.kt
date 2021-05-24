package datatouch.uikit.components.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import datatouch.uikit.components.windows.base.AppFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

abstract class SuperWizardFragment<TStepResult> : AppFragment(), IWizardStep<TStepResult> {

    override var result: Flow<TStepResult> = emptyFlow()

    protected fun setStepResultTo(result: TStepResult) { this.result = flowOf(result) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}