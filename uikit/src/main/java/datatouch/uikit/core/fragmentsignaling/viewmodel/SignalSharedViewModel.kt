package datatouch.uikit.core.fragmentsignaling.viewmodel


import android.os.ConditionVariable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import datatouch.uikit.core.fragmentsignaling.interfaces.ISignal
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect

private const val BUFFER_SIZE = 16
private const val BLOCKING_TIMEOUT = 1000L

internal class SignalSharedViewModel : ViewModel() {

    private val flow = MutableSharedFlow<ISignal>(extraBufferCapacity = BUFFER_SIZE)

    fun emitSignal(signal: ISignal) {
        this.viewModelScope.launch(Dispatchers.IO) {
            flow.emit(signal)
        }
    }

    fun emitSignalBlocking(signal: ISignal) {
        val coroutineFinishCondition = ConditionVariable() // Blocked state

        this.viewModelScope.launch(Dispatchers.IO) {
            flow.emit(signal)
            coroutineFinishCondition.open() // Remove blocked state
        }

        // Wait for coroutine finish
        coroutineFinishCondition.block(BLOCKING_TIMEOUT)
    }

    suspend fun collectSignal(action: suspend (value: ISignal) -> Unit) {
        flow.collect(action)
    }
}