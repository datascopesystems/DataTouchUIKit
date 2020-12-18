package datatouch.uikitapp

import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import datatouch.uikit.components.buttons.ImageButtonAccentOutline
import datatouch.uikit.components.buttons.ToggleButton
import datatouch.uikit.components.camera.utils.CameraUtils
import datatouch.uikit.components.dropdown.FormDropDownListView
import datatouch.uikit.components.edittext.search.CriteriaSearchEditText
import datatouch.uikit.components.edittext.search.CriteriaSearchEditTextAdapter
import datatouch.uikit.components.edittext.search.SearchCriterionItemHolder
import datatouch.uikitapp.adapters.TestAutocompleteDropDownListAdapter
import datatouch.uikitapp.adapters.TestItemHolder
import datatouch.uikitapp.adapters.TestSelectableDropDownListAdapter
import kotlinx.android.synthetic.main.activity_main.*

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val csed = findViewById<CriteriaSearchEditText>(R.id.cset)
        val btnTest = findViewById<ImageButtonAccentOutline>(R.id.btnTest)
        val dropDown = findViewById<FormDropDownListView>(R.id.dropDown)

        fet?.text = "Some Value"


        val adapter = CriteriaSearchEditTextAdapter<C>()

        csed?.setAdapter(adapter)

        adapter.data = mutableListOf(
            SearchCriterionItemHolder("C1", R.drawable.ic_edit_icon_white, C.C1),
            SearchCriterionItemHolder("C2", R.drawable.ic_accepted_white, C.C2),
            SearchCriterionItemHolder("C3", R.drawable.ic_add, C.C3)
        )

        adapter.selectedItem = C.C2

        adapter.onItemClickCallback = { Toast.makeText(this, "Loh", Toast.LENGTH_LONG).show() }

        btnTest.isVisible = true

        btnTest.setOnClickListener {
            CameraUtils.openPhotoCamera(
                this,
                this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.path.orEmpty()
            ) {
                Toast.makeText(this, "LOH" + it.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        val dropDownAdapter = TestSelectableDropDownListAdapter()

        dropDownAdapter.onItemClickCallback = {
            Toast.makeText(this, "Clicked only", Toast.LENGTH_SHORT).show()
        }

        dropDownAdapter.data = mutableListOf(TestItemHolder(), TestItemHolder(), TestItemHolder())

        dropDown.setAdapter(dropDownAdapter)

        dropDownAdapter.selectItem(dropDownAdapter.data[1])

        val autocompleteAdapter = TestAutocompleteDropDownListAdapter()
        autocompleteAdapter.data = mutableListOf(TestItemHolder(), TestItemHolder(), TestItemHolder())

        facddkv?.setAdapter(autocompleteAdapter)

        val gtb = findViewById<ToggleButton>(R.id.gtb)

        gtb.callback = { fetMan?.showMandatoryFieldErrorIfRequired() }

        fetMan?.setMandatory(true)

        tbSmall?.onCheckChangedCallback = {
            Toast.makeText(this, "Check Changed", Toast.LENGTH_SHORT).show()
        }

        tbSmall?.onLockedCallback = {
            tbSmall?.locked = false
        }



    }

    enum class C {
        C1, C2, C3
    }
}