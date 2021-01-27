package datatouch.uikitapp.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import datatouch.uikit.components.recyclerview.ViewHolder
import datatouch.uikit.components.recyclerview.movable.ItemMovableRecyclerViewListAdapter
import datatouch.uikit.core.callbacks.UiCallback

class TestItemMovableRecyclerViewListAdapter(
    onItemViewMoveStartCallback: UiCallback<RecyclerView.ViewHolder>) :
        ItemMovableRecyclerViewListAdapter<TestItemHolder, TestDropDownItemViewUi>(
            onItemViewMoveStartCallback) {

    override fun onCreateDataItemViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(TestDropDownItemViewUi(parent.context))

    override fun onBindDataItemViewHolder(holder: ViewHolder<TestDropDownItemViewUi>, position: Int) {
        super.onBindDataItemViewHolder(holder, position)
        holder.typedView.setItemSelected(true)
    }

}