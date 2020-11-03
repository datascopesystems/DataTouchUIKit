package datatouch.uikit.core.extensions

import datatouch.uikit.core.collections.lists.StringList

object CollectionExtensions {

    fun StringList?.allStringNoneEmpty(): List<String> =
            this?.filter { it?.isNotEmpty() == true }.toFilteredMutable()

    fun <T : Any> ArrayList<T?>?.toFilteredMutable() =
            this.orEmpty().filterNotNull().toMutableList()

    fun <T : Any> List<T?>?.toFilteredMutable() =
            this.orEmpty().filterNotNull().toMutableList()

    fun <T> List<T>?.orEmptyMutable(): MutableList<T> = this?.toMutableList() ?: mutableListOf()
}

