package datatouch.uikitapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import datatouch.uikit.components.buttons.ImageButtonAccentOutline
import datatouch.uikit.components.edittext.search.CriteriaSearchEditText
import datatouch.uikit.components.edittext.search.CriteriaSearchEditTextAdapter
import datatouch.uikit.components.edittext.search.SearchCriterionItemHolder
import datatouch.uikit.core.utils.views.ScreenshotUtils

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val llRoot = findViewById<View>(R.id.llRoot)

        val csed = findViewById<CriteriaSearchEditText>(R.id.cset)
        val btnTest = findViewById<ImageButtonAccentOutline>(R.id.btnTest)

        val adapter = CriteriaSearchEditTextAdapter<C>()

        csed?.setAdapter(adapter)

        adapter.data = mutableListOf(SearchCriterionItemHolder("C1", R.drawable.ic_edit_icon_white, C.C1),
            SearchCriterionItemHolder("C2", R.drawable.ic_accepted_white, C.C2),
            SearchCriterionItemHolder("C3", R.drawable.ic_add, C.C3))

        adapter.selectedItem = C.C2

        adapter.onItemClickCallback = { Toast.makeText(this, "Loh", Toast.LENGTH_LONG).show()}

        btnTest.setOnClickListener {
            val screenShot = ScreenshotUtils.makeScreenshot(llRoot)
            this.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)?.mkdirs()
            screenShot.saveToFile("${this.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)?.path.orEmpty()}/TEST.jpg")
        }

    }

    enum class C {
        C1, C2, C3
    }
}