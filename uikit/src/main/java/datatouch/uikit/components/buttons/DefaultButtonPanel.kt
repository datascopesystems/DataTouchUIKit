package datatouch.uikit.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import datatouch.uikit.R
import datatouch.uikit.core.callbacks.UiJustCallback
import datatouch.uikit.databinding.DefaultButtonPanelBinding


@SuppressLint("ViewConstructor")
class DefaultButtonPanel(context: Context?, style: ButtonStyle = ButtonStyle.Accept) : RelativeLayout(context) {

    private val ui = DefaultButtonPanelBinding
        .inflate(LayoutInflater.from(context), this, true)

    var btnCallback: UiJustCallback? = null

    init {
        ui.btn.setOnClickListener { btnCallback?.invoke() }
        setupStyle(style)
    }

    fun setupStyle(style: ButtonStyle) {
        ui.btn.setIcon(getButtonIcon(style))
        ui.btn.setButtonType(getButtonType(style))
    }

    private fun getButtonIcon(style: ButtonStyle): Drawable? {
        return when (style) {
            ButtonStyle.Accept -> ContextCompat.getDrawable(context, R.drawable.ic_check_white)
            ButtonStyle.Add -> ContextCompat.getDrawable(context, R.drawable.ic_add_white_2)
            ButtonStyle.Reset -> ContextCompat.getDrawable(context, R.drawable.ic_reset_white)
        }
    }

    private fun getButtonType(style: ButtonStyle): ButtonType {
        return when (style) {
            ButtonStyle.Accept -> ButtonType.Positive
            ButtonStyle.Add -> ButtonType.Positive
            ButtonStyle.Reset -> ButtonType.Negative
        }
    }

    fun setAcceptButtonVisibility(isVisible: Boolean) {
        ui.btn.isVisible = isVisible
    }

    enum class ButtonStyle {
        Accept, Add, Reset
    }

}