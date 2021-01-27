package datatouch.uikitapp.adapters

import datatouch.uikit.components.dropdown.adapter.IDropDownListAdapterItem
import datatouch.uikit.components.recyclerview.movable.IMovableData
import java.util.*

class TestItemHolder : IDropDownListAdapterItem, IMovableData {

    override val name: String get() = UUID.randomUUID().toString()

    override var order = 0

}