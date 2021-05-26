package datatouch.uikit.components.windows.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import datatouch.uikit.components.appbackground.AppBackgroundBundle
import datatouch.uikit.components.logic.*
import datatouch.uikit.components.windows.error.FQaErrorNotification
import datatouch.uikit.components.windows.progress.FBlockingProgress
import datatouch.uikit.core.extensions.ViewBindingExtensions.getViewBindingClass
import datatouch.uikit.core.utils.views.ViewBindingUtil
import io.uniflow.android.livedata.onEvents
import io.uniflow.android.livedata.onStates
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class WindowActivity<TActivityLayout : ViewBinding> : AppCompatActivity(), CoroutineScope {

    protected abstract var appBackground: AppBackgroundBundle?

    val ui: TActivityLayout? by lazy {
        ViewBindingUtil.inflate(
            layoutInflater,
            getViewBindingClass(javaClass)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui?.root)

        getLogic()?.apply {
            onEvents(this) { handleBaseEvents(it) }
            onStates(this) { handleUiState(it) }
        }

        renderUi()
    }

    abstract fun renderUi()

    private var job: Job = Job()
    override val coroutineContext get() = Dispatchers.Main + job

    private val progressDialog by lazy { createProgressDialog() }

    private fun handleBaseEvents(event: UIEvent?) {
        when (event) {
            is StartLoading -> showProgress()
            is StopLoading -> hideProgress()
        }

        handleUiEvent(event)
    }

    abstract fun handleUiEvent(event: UIEvent?)
    abstract fun handleUiState(uiState: UIState)
    abstract fun getLogic(): ViewLogic?

    protected fun displayError(event: Event) {
        FQaErrorNotification().also { it.errorMessage = event.payload?.toString().orEmpty() }
            .show(supportFragmentManager)
    }

    override fun onPause() {
        Events.unregister(this)
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
        injectAppBackgrounds()
        Events.register(this)
    }

    private fun injectAppBackgrounds() = appBackground?.let {
        launch {
            it.clear()
            addDecorators(it)
            it.decorateAsync()
        }
    }

    private fun createProgressDialog() = FBlockingProgress().also {
        it.isDialogCancelable = true
        it.onBackClickedCallback = ::onBackButtonClickedClicked
    }

    fun showProgress() = progressDialog.show(supportFragmentManager, progressDialog::javaClass.name)
    fun hideProgress() = progressDialog.dismiss()

    protected open fun onBackButtonClickedClicked() {}
    open fun addDecorators(bundle: AppBackgroundBundle) {}


    fun showProgressDialogWithMessage(resId: Int) {
        showProgressDialogWithMessage(getString(resId))
    }

    fun showProgressDialogWithMessage(s: String) {
        progressDialog.showSubMessage(s)
        progressDialog.show(supportFragmentManager, progressDialog::javaClass.name)
    }


}