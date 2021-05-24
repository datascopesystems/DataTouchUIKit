package datatouch.uikit.components.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import datatouch.uikit.components.windows.base.UiBindingInjector
import datatouch.uikit.components.windows.base.UiBindingProperty
import io.uniflow.androidx.flow.onEvents
import kotlinx.coroutines.*


abstract class WizardCompletableFragment<TStepResult> : SuperWizardFragment<TStepResult>(),
    CoroutineScope {

    private var job: Job = Job()
    override val coroutineContext get() = Dispatchers.Main + job
    override val fragment: SuperWizardFragment<TStepResult> get() = this

    private val uiBindingInjector = UiBindingInjector()
    protected abstract val uiBindingProperty: UiBindingProperty

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val ui = inflateInject(inflater, container)
        return provideRootView(ui)
    }

    override fun onDestroyView() {
        uiBindingInjector.releaseInjected()
        super.onDestroyView()
    }

    fun launchIO(block: suspend CoroutineScope.() -> Unit) =
        launch(Dispatchers.IO + Job(), CoroutineStart.DEFAULT, block)

    override val isComplete get() = getStepResult() != StepFragmentResult.Incomplete

    abstract fun getStepResult(): StepFragmentResult

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getLogic()?.apply {
            onEvents(this) { handleBaseUiEvent(it) }
        }
    }

    private fun handleBaseUiEvent(event: io.uniflow.core.flow.data.Event<*>) = event.take()?.let {
        handleUiEvent(it)
    }

    protected open fun provideRootView(viewBinding: ViewBinding?): View? {
        return viewBinding?.root
    }

    protected open fun inflateInject(inflater: LayoutInflater, container: ViewGroup?): ViewBinding? {
        return uiBindingInjector.inflateInject(uiBindingProperty, inflater, container, false)
    }
}