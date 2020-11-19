package datatouch.uikitapp

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import datatouch.uikit.components.buttons.ImageButtonAccentOutline
import datatouch.uikit.components.camera.activities.CameraPhotoActivity
import datatouch.uikit.components.camera.activities.CameraPhotoActivityParamsKey
import datatouch.uikit.components.camera.activities.params.CameraPhotoActivityParams
import datatouch.uikit.components.edittext.search.CriteriaSearchEditText
import datatouch.uikit.components.edittext.search.CriteriaSearchEditTextAdapter
import datatouch.uikit.components.edittext.search.SearchCriterionItemHolder
import java.io.File

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

        btnTest.isVisible = true

        btnTest.setOnClickListener {
            val intent = Intent(this, CameraPhotoActivity::class.java)
            intent.putExtra(CameraPhotoActivityParamsKey, CameraPhotoActivityParams(
                File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.path.orEmpty())
            ))
            startActivity(intent)
        }

    }

    enum class C {
        C1, C2, C3
    }
}