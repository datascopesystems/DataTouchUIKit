package datatouch.uikit.components.recyclerview.movable

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import datatouch.uikit.core.callbacks.UiCallback

abstract class MovableItemView(context: Context?) : RelativeLayout(context) {

    @SuppressLint("ClickableViewAccessibility")
    fun setOnMoveCallback(onItemViewMoveStartCallback: UiCallback<RecyclerView.ViewHolder>,
                          viewHolder : RecyclerView.ViewHolder) {
        moveAnchorView?.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN)
                onItemViewMoveStartCallback.invoke(viewHolder)

            false
        }
    }

    abstract val moveAnchorView: View?

}