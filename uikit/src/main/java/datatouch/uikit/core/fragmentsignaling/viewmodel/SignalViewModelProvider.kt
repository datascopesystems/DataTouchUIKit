package datatouch.uikit.core.fragmentsignaling.viewmodel

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

internal object SignalViewModelProvider {

    fun of(fragment: Fragment): SignalSharedViewModel {
        return ViewModelProvider(fragment.requireActivity()).get(SignalSharedViewModel::class.java)
    }

    fun of(activity: AppCompatActivity): SignalSharedViewModel {
        return ViewModelProvider(activity).get(SignalSharedViewModel::class.java)
    }
}