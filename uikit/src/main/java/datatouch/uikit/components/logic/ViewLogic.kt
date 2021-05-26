package datatouch.uikit.components.logic

import datatouch.uikit.components.logger.ErrorEvent
import io.uniflow.android.AndroidDataFlow
import io.uniflow.core.flow.DataFlow
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import kotlinx.coroutines.delay

abstract class ViewLogic : AndroidDataFlow() {

    fun showLoadingUiWhileDoingAction(func: suspend DataFlow.() -> Unit) = action {
        showLoadingUi()
        func.invoke(this)
        /*      this delay is required because sometimes loading progress is not showing
                but action already completed so STOP_LOADING is coming before START_LOADING
                which cause UI infinite loading! possible solution to get rid of this idea of
                wrap into showLoadingUiWhileDoingAction because of async nature of UI show progress func*/
        delay(100)
        stopLoadingUi()
    }

    suspend fun showLoadingUi() = sendEvent(StartLoading)
    suspend fun stopLoadingUi() = sendEvent(StopLoading)

    override suspend fun onError(error: Exception, currentState: UIState) {
        super.onError(error, currentState)
        Events.post(Event(ErrorEvent.DebugViewLogicError, error.message))
        super.onError(error, currentState)
    }

}

typealias StopLoading = UIEvent.Success
typealias StartLoading = UIEvent.Loading