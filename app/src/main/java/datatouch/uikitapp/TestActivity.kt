package datatouch.uikitapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import datatouch.uikitapp.adapters.TestItemHolder
import datatouch.uikitapp.adapters.TestItemMovableRecyclerViewListAdapter
import kotlinx.android.synthetic.main.activity_main.*


class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val adapter = TestItemMovableRecyclerViewListAdapter {}

        srv.adapter = adapter

        adapter.data = listOf(TestItemHolder(), TestItemHolder(), TestItemHolder(), TestItemHolder())

    }

}