package datatouch.uikitapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import datatouch.uikit.components.recyclerview.RecyclerViewListAdapter.Companion.getDefaultListFooter
import datatouch.uikit.components.recyclerview.movable.VerticallyMovableItemTouchHelperCallback
import datatouch.uikitapp.adapters.TestItemHolder
import datatouch.uikitapp.adapters.TestItemMovableRecyclerViewListAdapter
import kotlinx.android.synthetic.main.activity_main.*


class TestActivity : AppCompatActivity() {

    private var itemTouchHelper: ItemTouchHelper? = null

    private val adapter by lazy {
        val a = TestItemMovableRecyclerViewListAdapter { itemTouchHelper?.startDrag(it) }
        a.setFooterView(getDefaultListFooter(this))
        val callback: ItemTouchHelper.Callback = VerticallyMovableItemTouchHelperCallback(a)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper?.attachToRecyclerView(rv)
        a
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)

        rv?.layoutManager = LinearLayoutManager(this)

        adapter.data = listOf(TestItemHolder(), TestItemHolder(), TestItemHolder())

        rv?.adapter = adapter
    }

}