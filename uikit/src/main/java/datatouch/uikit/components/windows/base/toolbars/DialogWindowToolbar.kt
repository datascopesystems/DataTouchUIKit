package datatouch.uikit.components.windows.base.toolbars

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.view.isInvisible
import datatouch.uikit.R
import datatouch.uikit.databinding.DialogWindowToolbarBinding

class DialogWindowToolbar @JvmOverloads constructor(
    context: Context? = null,
    attrs: AttributeSet? = null, defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle) {

    private val ui by lazy {
        DialogWindowToolbarBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private var titleText: String? = null
    private var iconDrawable: Drawable? = null

    init {
        parseAttributes(attrs)
        setupTitle()
    }

    private fun parseAttributes(attrs: AttributeSet?) = attrs?.apply {
        @SuppressLint("CustomViewStyleable") val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.ToolBar, 0, 0)
        titleText = try {
            typedArray.getString(R.styleable.ToolBar_toolbar_title)
        } finally {
            typedArray.recycle()
        }
    }

    private fun setupTitle() {
        ui.tvTitle.text = titleText.orEmpty()
    }

    fun setTitle(titleText: String?) {
        this.titleText = titleText
        setupTitle()
    }

    fun setIcon(iconDrawable: Drawable?) {
        this.iconDrawable = iconDrawable
        setupIcon()
    }

    private fun setupIcon() {
        ui.ivIcon.setImageDrawable(iconDrawable)
    }

    fun setOnBackButtonListener(clickListener: OnClickListener?) {
        ui.btnClose.setOnClickListener(clickListener)
    }

    fun setCloseButtonVisibility(isVisible: Boolean) {
        ui.btnClose.isInvisible = !isVisible
    }
}