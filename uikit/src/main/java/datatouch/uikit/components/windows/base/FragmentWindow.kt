package datatouch.uikit.components.windows.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import datatouch.uikit.components.logic.ViewLogic
import datatouch.uikit.core.utils.Conditions
import io.uniflow.android.livedata.onEvents
import io.uniflow.android.livedata.onStates
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import kotlinx.coroutines.*
import java.util.*

abstract class FragmentWindow : DialogFragment() {

    private var scope: CoroutineScope? = null

    protected abstract val rootView: View?

    protected var isAttached = false
        private set

    protected fun runDelayed(delayMs: Long, block: () -> Unit) = launch {
        delay(delayMs)
        if (isFragmentOk())
            block.invoke()
    }

    protected fun launch(block: suspend CoroutineScope.() -> Unit) =
        scope?.launch(scope!!.coroutineContext, block = block)

    fun launchIO(block: suspend CoroutineScope.() -> Unit) =
        scope?.launch(Dispatchers.IO + Job(), CoroutineStart.DEFAULT, block)

    protected fun <T> async(block: suspend CoroutineScope.() -> T): Deferred<T>? =
        scope?.async(scope!!.coroutineContext, block = block)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        getLogic()?.apply {
            onEvents(this) { handleUiEvent(it) }
            onStates(this) { handleUiState(it) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        afterViewCreated()
    }

    abstract fun afterViewCreated()

    protected open fun getLogic(): ViewLogic? = null

    protected open fun handleUiEvent(event: UIEvent?) {}
    protected open fun handleUiState(state: UIState?) {}

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAttached = true
        destroyScope()
        scope = MainScope()
    }

    override fun onDetach() {
        super.onDetach()
        isAttached = false
        destroyScope()
    }

    private fun destroyScope() {
        scope?.cancel()
        scope = null
    }

    override fun dismiss() {
        if (!isAdded || !isAttached) return
        hideSoftKeyboardIfPossible()
        super.dismiss()
    }

    private fun hideSoftKeyboardIfPossible() = rootView?.apply {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun isFragmentOk() = isAdded && isAttached
    fun isViewOk(view: View?) = isFragmentOk() && Conditions.isNotNull(view)

    fun showNoBackStack(manager: FragmentManager?, tag: String) = manager?.apply {
        try {
            beginTransaction()
                .add(this@FragmentWindow, tag)
                .commitNowAllowingStateLoss()
        } catch (ex: Exception) {
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (Conditions.isNull(manager)) return
        if (manager.findFragmentByTag(tag) != null) {
            return
        }

        try {
            val ft = manager.beginTransaction()
            ft.addToBackStack(javaClass.name)
            super.show(ft, tag)
        } catch (ignored: Exception) {
        }

    }

    fun show(manager: FragmentManager?) = manager?.apply {
        show(this, this@FragmentWindow.javaClass.name)
    }

    protected open fun onNavigationBackPress() {
        onClose()
        dismiss()
    }

    protected open fun onClose() {}

    protected val windowActivity: WindowActivity<*>? get() = activity as WindowActivity<*>?

    open fun hideKeyboard() {
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.rootView?.windowToken, 0)
    }

    protected abstract fun inject()

}