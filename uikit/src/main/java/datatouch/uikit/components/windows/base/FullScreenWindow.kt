package datatouch.uikit.components.windows.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import datatouch.uikit.R
import datatouch.uikit.components.appbackground.AppBackgroundBundle
import datatouch.uikit.core.utils.ScreenUtils


@SuppressLint("InflateParams")
abstract class FullScreenWindow<TToolbar : View> : FragmentWindow() {

    private var llToolbarContainer: LinearLayout? = null

    private var llWindowContentContainer: LinearLayout? = null

    protected var windowToolBar: TToolbar? = null
        private set

    protected var ivBackground: ImageView? = null
        private set

    override val rootView: View by lazy {
        LayoutInflater.from(context)
            .inflate(R.layout.full_screen_window_fragment, null, false)
    }

    protected abstract var appBackgroundBundle: AppBackgroundBundle?

    open fun hasToolbar() = true

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setDimAmount(0f)
        dialog.window?.setWindowAnimations(R.style.FullScreenWindowAnimations)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setContentView(LinearLayout(context))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.setOnDismissListener { onClose() }
        dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, event: KeyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (event.action == KeyEvent.ACTION_UP) {
                    onNavigationBackPress()
                    return@setOnKeyListener true
                }
            }
            false
        }
        return dialog
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        llToolbarContainer = rootView.findViewById(R.id.llToolbarContainer)
        llWindowContentContainer = rootView.findViewById(R.id.llWindowContentContainer)
        ivBackground = rootView.findViewById(R.id.ivBackground)

        setupWindowContent(view)
        setupToolbar()
        setupBackground()

        super.onViewCreated(rootView, savedInstanceState)
    }

    private fun setupWindowContent(view: View) {
        (view.parent as? ViewGroup?)?.removeView(view)
        llWindowContentContainer?.addView(view)
    }

    private fun setupToolbar() {
        if (hasToolbar()) {
            windowToolBar = provideToolbar()
            llToolbarContainer?.addView(windowToolBar)
        }
    }

    protected abstract fun provideToolbar(): TToolbar

    private fun setupBackground() {
        ivBackground?.layoutParams?.height = ScreenUtils.getDisplayMetrics(activity).heightPixels
    }

    override fun getView() = rootView

    override fun onStart() {
        super.onStart()
        injectAppBackgrounds()
    }

    private fun injectAppBackgrounds() = launch {
        appBackgroundBundle?.clear()
        appBackgroundBundle?.let { provideAppBackgroundDecorators(it) }
        appBackgroundBundle?.decorateAsync()
    }

    open fun provideAppBackgroundDecorators(bundle: AppBackgroundBundle) {}

}