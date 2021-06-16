package datatouch.uikit.core.fragmentsignaling.consumer

import androidx.appcompat.app.AppCompatActivity

internal class ActivitySignalConsumer : SignalConsumer() {
    private var activity: AppCompatActivity? = null

    fun configure(activity: AppCompatActivity) {
        if (this.activity == null) {
            this.activity = activity
            startObserver(activity)
        }
    }

    override fun onConsumerInit() {
        activity?.let {
            startFlowConsumer(it, getSharedViewModel(it))
        }
    }

    override fun onConsumerDestroy() {
        activity = null
    }
}