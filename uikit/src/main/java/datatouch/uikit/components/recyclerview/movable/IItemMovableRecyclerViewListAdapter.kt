package datatouch.uikit.components.recyclerview.movable

interface IItemMovableRecyclerViewListAdapter {

    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean

    fun onItemsMoved()

}