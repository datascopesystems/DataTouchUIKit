package datatouch.uikitapp.signalingexample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import datatouch.uikit.components.toast.ToastNotification
import datatouch.uikit.core.activityargs.ActivityArgs
import datatouch.uikit.core.activityargs.extension.putArg
import datatouch.uikit.core.activityargs.extension.withClass
import datatouch.uikit.core.fragmentsignaling.SigFactory
import datatouch.uikitapp.R

class ASignalTest : AppCompatActivity() {

    private val sc by SigFactory.slotContainerActivity()

    private var arg by ActivityArgs.of("default qwerty")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signal_test)

        val btnShowSignalingExample = findViewById<View>(R.id.btnShowSignalingExample)
        btnShowSignalingExample?.setOnClickListener {
            FSignalParent()
                .withArg("Qwerty arg 123")
                .withCallback(onFragmentValue)
                .show(supportFragmentManager)
        }
    }

    // Slot with 1 String parameter and 1 String return value
    private val onFragmentValue by sc.slot<String>().of<String> {
        ToastNotification.showSuccess(this,"Activity Slot param: $it")
        return@of "Activity Slot OK!!!!!"
    }

    companion object {
        fun intent(context: Context?, arg: String): Intent {
            return Intent().withClass(context, ASignalTest::class)
                .putArg(ASignalTest::arg, arg)
        }
    }
}