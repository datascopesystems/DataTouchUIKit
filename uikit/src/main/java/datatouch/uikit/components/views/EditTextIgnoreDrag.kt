package datatouch.uikit.components.views

import android.content.Context
import android.util.AttributeSet
import android.view.DragEvent
import androidx.appcompat.widget.AppCompatEditText

class EditTextIgnoreDrag : AppCompatEditText {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDragEvent(event: DragEvent) = false
}