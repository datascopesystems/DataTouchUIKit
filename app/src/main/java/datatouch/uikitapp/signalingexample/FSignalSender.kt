package datatouch.uikitapp.signalingexample

import android.os.Bundle
import datatouch.uikit.R
import datatouch.uikit.components.appbackground.AppBackgroundBundle
import datatouch.uikit.components.windows.base.DefaultFullScreenWindowUiBind
import datatouch.uikit.core.extensions.GenericExtensions.default
import datatouch.uikit.core.fragmentargs.extension.putArg
import datatouch.uikit.core.fragmentsignaling.SigFactory
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall0
import datatouch.uikit.core.fragmentsignaling.variation.call.SigCall2
import datatouch.uikit.core.fragmentsignaling.variation.extension.putSignal
import datatouch.uikitapp.databinding.FragmentSignalSenderBinding
import datatouch.utils.fragmentargs.FragmentArgs

class FSignalSender : DefaultFullScreenWindowUiBind<FragmentSignalSenderBinding>() {

    private var simpleCallback by SigFactory.sigCall<Unit>()
    private var calcSumCallback by SigFactory.sigCall<Int, Int, Float>()

    private var argFromParent by FragmentArgs.of("Default value 1")


    fun withArg(arg: String) = apply {
        // Assign argument from parent fragment
        argFromParent = arg
    }

    fun withSimpleCallback(callback: SigCall0<Unit>) = apply {
        // Assing callback with no parameters and no return value
        simpleCallback = callback
    }

    fun withcalcSumCallback(callback: SigCall2<Int, Int, Float>) = apply {
        // Assing callback with 2 Int parameters and 1 Float return value
        calcSumCallback = callback
    }

    override fun afterViewCreated() {
        ui?.btnShowArgFromParentFragment?.setOnClickListener {
            ui?.tvArgFromParentFragment?.setText(argFromParent)
        }

        ui?.btnCallbackCalculateSum?.setOnClickListener {
            val a = ui?.etA?.text?.toString().default("0").toInt()
            val b = ui?.etB?.text?.toString().default("0").toInt()
            execSumCallback(a, b)
        }

        ui?.btnCllbackToParent?.setOnClickListener {
            // Callback to parent fragment
            simpleCallback.invoke()
        }
    }

    private fun execSumCallback(a: Int, b: Int) {
        // Callback to parent fragment
        calcSumCallback.invoke(a, b) {
            // it - return value from parent fragment slot
            ui?.tvSum?.setText(it.toString())
        }
    }

    companion object {
        // Create Bundle with arg and callbacks
        // if fragment instantiated via reflection
        fun makeArgs(arg: String, simpleCallback: SigCall0<Unit>,
                     calcSumCallback: SigCall2<Int, Int, Float>) = Bundle()
            .putArg(FSignalSender::argFromParent, arg)
            .putSignal(FSignalSender::simpleCallback, simpleCallback)
            .putSignal(FSignalSender::calcSumCallback, calcSumCallback)
    }


    override fun inject() {}

    override fun getWindowTitle() = R.string.no_title

    override var appBackgroundBundle: AppBackgroundBundle?
        get() = null
        set(value) {}

}