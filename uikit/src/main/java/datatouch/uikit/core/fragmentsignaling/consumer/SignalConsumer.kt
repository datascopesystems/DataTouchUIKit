package datatouch.uikit.core.fragmentsignaling.consumer

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import datatouch.uikit.core.fragmentsignaling.viewmodel.SignalSharedViewModel
import datatouch.uikit.core.fragmentsignaling.variation.slotcontainer.SlotCreationContainer
import datatouch.uikit.core.fragmentsignaling.viewmodel.SignalViewModelProvider
import kotlinx.coroutines.launch

internal abstract class SignalConsumer : SlotCreationContainer() {
    private var isObserverStarted = false

    private var isConsumerInitialized = false

    protected abstract fun onConsumerDestroy()

    protected abstract fun onConsumerInit()

    override fun drop() {
        super.drop()
        isObserverStarted = false
        isConsumerInitialized = false
    }

    private fun onLifecycleDestroyed() {
        drop()
        onConsumerDestroy()
    }

    private fun onLifecycleCreated() {
        if (isConsumerInitialized) return
        onConsumerInit()
    }

    protected fun startFlowConsumer(owner: LifecycleOwner, vm: SignalSharedViewModel) {
        if (isConsumerInitialized) return

        isConsumerInitialized = true
        owner.lifecycleScope.launch { vm.collectSignal(::consumeSignal) }
    }

    protected fun startObserver(owner: LifecycleOwner) {
        if (isObserverStarted) return

        setConsumerName(owner::class.simpleName)

        val lifecycle = owner.lifecycle
        if (lifecycle.currentState != Lifecycle.State.DESTROYED) {
            isObserverStarted = true
            lifecycle.addObserver(LifecycleStateChangeObserver(owner))
        }
    }

    protected fun getSharedViewModel(fragment: Fragment): SignalSharedViewModel {
        return SignalViewModelProvider.of(fragment)
    }

    protected fun getSharedViewModel(activity: AppCompatActivity): SignalSharedViewModel {
        return SignalViewModelProvider.of(activity)
    }

    private inner class LifecycleStateChangeObserver(private var lifecycleOwner: LifecycleOwner?) :
        LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            handleState(lifecycleOwner?.lifecycle)
        }

        private fun handleState(lifecycle: Lifecycle?) {
            if (lifecycle == null) return

            when (lifecycle.currentState) {
                Lifecycle.State.CREATED -> {
                    onLifecycleCreated()
                }

                Lifecycle.State.DESTROYED -> {
                    lifecycle.removeObserver(this)
                    onLifecycleDestroyed()
                    dropEventObserver()
                }

                else -> { }
            }
        }

        private fun dropEventObserver() {
            lifecycleOwner = null
        }
    }
}