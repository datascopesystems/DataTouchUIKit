package datatouch.uikit.components.dropdown

import android.text.Editable
import android.text.TextWatcher
import datatouch.uikit.core.callbacks.UiJustCallback

class AfterTextChangedListener(private val callback : UiJustCallback) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        callback.invoke()
    }

}