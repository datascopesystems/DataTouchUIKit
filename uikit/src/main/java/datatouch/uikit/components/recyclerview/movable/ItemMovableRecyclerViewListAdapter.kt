package datatouch.uikit.components.recyclerview.movable

import androidx.recyclerview.widget.RecyclerView
import datatouch.uikit.components.recyclerview.RecyclerViewListAdapter
import datatouch.uikit.components.recyclerview.ViewHolder
import datatouch.uikit.core.callbacks.UiCallback
import java.util.*

abstract class ItemMovableRecyclerViewListAdapter<TData : IMovableData, TView : MovableItemView>(
    private val onItemViewMoveStartCallback: UiCallback<RecyclerView.ViewHolder>
) : RecyclerViewListAdapter<TData, TView>(), IItemMovableRecyclerViewListAdapter {

    override fun onBindDataItemViewHolder(holder: ViewHolder<TView>, position: Int) {
        holder.typedView.setOnMoveCallback(onItemViewMoveStartCallback, holder)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition)
            for (i in fromPosition until toPosition)
                Collections.swap(data, i, i + 1)
        else
            for (i in fromPosition downTo toPosition + 1)
                Collections.swap(data, i, i - 1)


        notifyMyItemMoved(fromPosition, toPosition)

        return true
    }

    override fun onItemsMoved() = data.forEach { it.order = data.indexOf(it) }

}