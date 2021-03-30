package datatouch.uikit.core.extensions

import androidx.recyclerview.widget.RecyclerView
import datatouch.uikit.components.recyclerview.HorizontalLineDivider
import datatouch.uikit.components.recyclerview.VerticalLineDivider

object RecyclerViewExtensions {

    fun RecyclerView.addHorizontalLineDivider() = addItemDecoration(HorizontalLineDivider(context))

    fun RecyclerView.addVerticalLineDivider() = addItemDecoration(VerticalLineDivider(context))

}