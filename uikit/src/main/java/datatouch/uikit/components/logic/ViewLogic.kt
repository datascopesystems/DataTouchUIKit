package datatouch.uikit.components.logic

import datatouch.uikit.components.logger.ErrorEvent
import datatouch.uikit.components.logic.ActionFlowExt.showLoadingUi
import datatouch.uikit.components.logic.ActionFlowExt.stopLoadingUi
import io.uniflow.androidx.flow.AndroidDataFlow
import io.uniflow.core.flow.ActionFlow
import io.uniflow.core.flow.data.UIState
import kotlinx.coroutines.delay

open class ViewLogic : AndroidDataFlow() {

    fun showLoadingUiWhileDoingAction(func: suspend ActionFlow.() -> Unit) = action {
        showLoadingUi()
        func.invoke(this)
        /*      this delay is required because sometimes loading progress is not showing
                but action already completed so STOP_LOADING is coming before START_LOADING
                which cause UI infinite loading! possible solution to get rid of this idea of
                wrap into showLoadingUiWhileDoingAction because of async nature of UI show progress func*/
        delay(100)
        stopLoadingUi()
    }

    override suspend fun onError(error: Exception, currentState: UIState, flow: ActionFlow) {
        Events.post(Event(ErrorEvent.DebugViewLogicError, error.message))
        super.onError(error, currentState, flow)
    }
}