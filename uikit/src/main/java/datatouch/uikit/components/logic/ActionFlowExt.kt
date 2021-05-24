package datatouch.uikit.components.logic

import io.uniflow.core.flow.ActionFlow
import io.uniflow.core.flow.data.UIEvent

typealias StopLoading = UIEvent.Success
typealias StartLoading = UIEvent.Loading

object ActionFlowExt {
    suspend fun ActionFlow.showLoadingUi() = sendEvent(StartLoading)
    suspend fun ActionFlow.stopLoadingUi() = sendEvent(StopLoading)
}