package datatouch.uikitapp.signalingexample

import datatouch.uikit.R
import datatouch.uikit.components.appbackground.AppBackgroundBundle
import datatouch.uikit.components.toast.ToastNotification
import datatouch.uikit.components.windows.base.DefaultFullScreenWindowUiBind
import datatouch.uikit.core.fragmentargs.FragmentArgs
import datatouch.uikit.core.fragmentsignaling.SigFactory
import datatouch.uikit.core.fragmentsignaling.variation.sigfun.SigFun1
import datatouch.uikitapp.databinding.FragmentSignalParentBinding

class FSignalParent : DefaultFullScreenWindowUiBind<FragmentSignalParentBinding>() {

    private val sc by SigFactory.slotContainerFragment()

    private var argFromActivity by FragmentArgs.of("Default value")

    private var valueToActivityCallback by SigFactory.sigFun<String>().of<String>()

    fun withArg(arg: String) = apply {
        // Assign argument from parent fragment
        argFromActivity = arg
    }

    fun withCallback(callback: SigFun1<String, String>) = apply {
        // Assing callback with 1 String parameter and 1 String return value
        valueToActivityCallback = callback
    }

    override fun afterViewCreated() {
        ui?.btnShowArgFromActivity?.setOnClickListener {
            ui?.tvShowArgFromActivity?.setText(argFromActivity)
        }

        ui?.btnCallbackValueToActivity?.setOnClickListener {
            sendValueToActivity()
        }

        ui?.btnShowFragment?.setOnClickListener {
            showChildFragment()
        }

        ui?.btnShowFragmentWithBundle?.setOnClickListener {
            showChildFragmentWithAruments()
        }
    }

    private fun sendValueToActivity() {
        // Callback to parent activity
        valueToActivityCallback.invoke("Preved") {
            // it - return value from activity slot
            ui?.tvValueToActivity?.setText(it)
        }
    }

    private fun showChildFragment() {
        FSignalSender()
            .withArg("Parent arg 123456")
            .withSimpleCallback(onChildCallback)
            .withcalcSumCallback(onCalculateSum)
            .show(childFragmentManager)
    }

    private fun showChildFragmentWithAruments() {
        // Create Bundle with arg and callbacks
        // if fragment instantiated via reflection
        val fragmentArgs = FSignalSender.makeArgs("Parent arg 987654", onChildCallback, onCalculateSum)
        FSignalSender().apply {
            arguments = fragmentArgs
        }.show(childFragmentManager)
    }

    // Slots - callbacks from child fragment
    // Callback with no parameters and no return value
    private val onChildCallback by sc.slot().of {
        ToastNotification.showSuccess(context,"Parent fragment Slot OK")
    }

    // Slot with 2 Int parameters and Float return value
    private val onCalculateSum by sc.slot<Int, Int>().of<Float> { a, b ->
        ToastNotification.showSuccess(context,"Parent fragment Slot onCalculateSum OK")
        return@of (a + b).toFloat()
    }


    override fun inject() {}

    override fun getWindowTitle() = R.string.no_title

    override var appBackgroundBundle: AppBackgroundBundle?
        get() = null
        set(value) {}

}