package datatouch.uikitapp.adapters

import datatouch.uikit.components.dropdown.adapter.IDropDownListAdapterItem
import java.util.*

class TestItemHolder : IDropDownListAdapterItem {

    override val name: String get() = UUID.randomUUID().toString()

}