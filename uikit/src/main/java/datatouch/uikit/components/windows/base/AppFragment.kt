package datatouch.uikit.components.windows.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import datatouch.uikit.components.logic.ViewLogic
import datatouch.uikit.core.utils.Conditions
import io.uniflow.androidx.flow.onEvents
import io.uniflow.androidx.flow.onStates
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@Deprecated("Inherit from AppFragmentUiBind instead")
abstract class AppFragment : Fragment() {

    private var scope: CoroutineScope? = null

    protected fun launch(block: suspend CoroutineScope.() -> Unit) =
        scope?.launch(scope!!.coroutineContext, block = block)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        destroyScope()
        scope = MainScope()
    }

    override fun onDetach() {
        super.onDetach()
        destroyScope()
    }

    private fun destroyScope() {
        scope?.cancel()
        scope = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        getLogic()?.apply {
            onEvents(this) { handleBaseUiEvent(it) }
            onStates(this) { handleUiState(it) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        afterViewCreated()
    }

    open fun afterViewCreated() {}

    protected open fun getLogic(): ViewLogic? = null

    protected open fun handleUiEvent(event: UIEvent?) {}
    protected open fun handleUiState(state: UIState?) {}

    private fun handleBaseUiEvent(event: io.uniflow.core.flow.data.Event<*>) =
        event.take()?.let { handleUiEvent(it) }

    protected val windowActivity get() = activity as WindowActivity<*>?

    private var currentFragment: Fragment? = null

    protected fun showFragment(fragmentContainerId: Int, fragment: Fragment) {
        if (fragmentContainerId == 0) return

        val ft = childFragmentManager.beginTransaction()
        if (Conditions.isNotNull(currentFragment))
            ft.replace(fragmentContainerId, fragment)
        else
            ft.add(fragmentContainerId, fragment)
        currentFragment = fragment
        ft.commitNow()
    }

    protected fun removeCurrentFragment() = currentFragment?.let {
        val ft = childFragmentManager.beginTransaction()
        ft.remove(it)
        ft.commit()
        currentFragment = null
    }

    protected abstract fun inject()

}