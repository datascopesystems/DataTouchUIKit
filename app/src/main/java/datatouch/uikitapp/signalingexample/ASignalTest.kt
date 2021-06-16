package datatouch.uikitapp.signalingexample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import datatouch.uikit.components.toast.ToastNotification
import datatouch.uikit.core.fragmentsignaling.SigFactory
import datatouch.uikitapp.R

class ASignalTest : AppCompatActivity() {

    private val sc by SigFactory.slotContainerActivity()

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

    // Callback with 1 String parameter and 1 String return value
    private val onFragmentValue = sc.slot<String, String> {
        ToastNotification.showSuccess(this,"Activity Slot param: $it")
        return@slot "Activity slot OK"
    }
}