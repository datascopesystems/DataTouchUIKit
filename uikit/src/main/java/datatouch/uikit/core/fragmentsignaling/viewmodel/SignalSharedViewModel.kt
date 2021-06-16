package datatouch.uikit.core.fragmentsignaling.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import datatouch.uikit.core.fragmentsignaling.interfaces.ISignal
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class SignalSharedViewModel : ViewModel() {
    private val flow = MutableSharedFlow<ISignal>()

    fun emitSignal(signal: ISignal) {
        this.viewModelScope.launch {
            flow.emit(signal)
        }
    }

    suspend fun collectSignal(action: suspend (value: ISignal) -> Unit) {
        flow.collect(action)
    }
}