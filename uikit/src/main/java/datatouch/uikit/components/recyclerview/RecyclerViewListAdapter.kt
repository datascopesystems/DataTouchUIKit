package datatouch.uikit.components.recyclerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import datatouch.uikit.core.callbacks.UiCallback
import datatouch.uikit.core.extensions.ConditionsExtensions.isNotNull

abstract class RecyclerViewListAdapter<TData, TView : View>
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    internal var onDataItemsCountChangedCallback: UiCallback<Int>? = null

    private var headerViewHolder: RecyclerView.ViewHolder? = null
    private var footerViewHolder: RecyclerView.ViewHolder? = null

    var clickable = true
    open val dataItemCount get() = data.size

    var headerView: View?
        get() = headerViewHolder?.itemView
        set(header) {
            if (header == null) return

            if (headerViewHolder == null || header !== headerViewHolder!!.itemView) {
                headerViewHolder = HFViewHolder(header)
                notifyDataSetChanged()
            }
        }

    open var data: List<TData> = listOf()
        set(data) {
            field = data
            notifyDataSetChanged()
            notifyDataItemsCountChanged()
        }

    open val isEmpty get() = dataItemCount == 0

    var selectedItem: TData? = null
        set(value) {
            val index = data.indexOf(field)
            field = null
            notifyMyItemChanged(index)

            field = value
            notifyMyItemChanged(data.indexOf(value))
        }

    val hasSelectedItem get() = selectedItem.isNotNull()

    val selectedItemPosition: Int get() = data.indexOf(selectedItem)

    internal inner class HFViewHolder(v: View) : RecyclerView.ViewHolder(v)

    fun useDefaultFooter(context: Context?) {
        setFooterView(getDefaultListFooter(context))
    }

    fun setFooterView(foot: View) {
        if (footerViewHolder == null || foot !== footerViewHolder!!.itemView) {
            footerViewHolder = HFViewHolder(foot)
            notifyDataSetChanged()
        }
    }

    fun removeHeader() {
        if (headerViewHolder != null) {
            headerViewHolder = null
            notifyDataSetChanged()
        }
    }

    fun removeFooter() {
        if (footerViewHolder != null) {
            footerViewHolder = null
            notifyDataSetChanged()
        }
    }

    private fun isHeader(position: Int): Boolean {
        return hasHeader() && position == 0
    }

    private fun isFooter(position: Int): Boolean {
        return hasFooter() && position == dataItemCount + if (hasHeader()) 1 else 0
    }

    private fun itemPositionInData(rvPosition: Int): Int {
        return rvPosition - if (hasHeader()) 1 else 0
    }

    private fun itemPositionInRV(dataPosition: Int): Int {
        return dataPosition + if (hasHeader()) 1 else 0
    }


    open fun addData(position: Int, item: TData) {
        if (position <= data.size) {
            val mutableData = data.toMutableList()
            mutableData.add(position, item)
            replaceListDataOnly(mutableData)
            notifyMyItemInserted(position)
        }
    }

    protected open fun replaceListDataOnly(newData: List<TData>) {
        this.data = newData
    }

    fun appendData(item: TData) = addData(dataItemCount, item)

    open fun updateData(item: TData) {
        data.firstOrNull { it?.equals(item) == true }?.apply {
            val mutableData = data.toMutableList()
            val i = mutableData.indexOf(this)
            kotlin.runCatching {
                mutableData[i] = item
                replaceListDataOnly(mutableData)
                notifyMyItemChanged(i)
            }
        }
    }

    open fun removeData(position: Int) {
        if (position < data.size) {
            val mutableData = data.toMutableList()
            mutableData.removeAt(position)
            replaceListDataOnly(mutableData)
            notifyMyItemRemoved(position)
        }
    }

    fun removeData(item: TData) {
        val index = data.indexOf(item)
        if (index != -1) {
            removeData(index)
        }
    }

    open fun clearData() {
        data = listOf()
        notifyDataSetChanged()
        notifyDataItemsCountChanged()
    }

    protected open fun notifyMyItemInserted(position: Int) {
        notifyItemInserted(position)
        notifyDataItemsCountChanged()
    }

    private fun notifyDataItemsCountChanged() = onDataItemsCountChangedCallback?.invoke(dataItemCount)

    protected open fun notifyMyItemRemoved(position: Int) {
        notifyItemRemoved(position)
        notifyDataItemsCountChanged()
    }

    protected open fun notifyMyItemRangeRemoved(from: Int, to: Int) {
        notifyItemRangeRemoved(from, to)
        notifyDataItemsCountChanged()
    }

    protected open fun notifyMyItemChanged(position: Int) {
        notifyItemChanged(position)
    }

    fun notifyMyItemChanged(item: TData) {
        val index = data.indexOf(item)

        if (-1 != index) {
            notifyMyItemChanged(index)
        }
    }

    protected open fun notifyMyItemMoved(from: Int, to: Int) {
        notifyItemMoved(itemPositionInRV(from), itemPositionInRV(to))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_HEADER) {
            return this.headerViewHolder!!
        } else if (viewType == TYPE_FOOTER) {
            return footerViewHolder!!
        }
        return onCreateDataItemViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        @Suppress("UNCHECKED_CAST")
        if (!isHeader(position) && !isFooter(position))
            onBindDataItemViewHolder(holder as ViewHolder<TView>, itemPositionInData(position))

        if (isHeader(position)) {
            onBindHeaderItemViewHolder(holder, position)
        }

        if (isFooter(position)) {
            footerOnVisibleItem()
        }
    }

    open fun footerOnVisibleItem() {}

    override fun getItemCount(): Int {
        var itemCount = dataItemCount
        if (hasHeader()) {
            itemCount += 1
        }
        if (hasFooter()) {
            itemCount += 1
        }
        return itemCount
    }

    override fun getItemViewType(position: Int): Int {
        if (isHeader(position)) {
            return TYPE_HEADER
        }
        if (isFooter(position)) {
            return TYPE_FOOTER
        }
        val dataItemType = getDataItemType(itemPositionInData(position))
        if (dataItemType > ITEM_MAX_TYPE) {
            throw IllegalStateException("getDataItemType() must be less than $ITEM_MAX_TYPE.")
        }
        return dataItemType
    }

    private fun getDataItemType(position: Int) = 0

    private fun hasHeader() = headerViewHolder != null

    private fun hasFooter() = footerViewHolder != null

    abstract fun onCreateDataItemViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<TView>

    abstract fun onBindDataItemViewHolder(holder: ViewHolder<TView>, position: Int)

    private fun onBindHeaderItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    open fun hasAnyItems() = dataItemCount > 0

    companion object {

        private const val DEFAULT_LIST_FOOTER_HEIGHT = 150

        private const val TYPE_HEADER = Integer.MAX_VALUE
        private const val TYPE_FOOTER = Integer.MAX_VALUE - 1
        private const val ITEM_MAX_TYPE = Integer.MAX_VALUE - 2

        @JvmStatic
        fun getDefaultListFooter(context: Context?): View {
            val view = View(context)
            view.minimumHeight = DEFAULT_LIST_FOOTER_HEIGHT
            return view
        }
    }
}