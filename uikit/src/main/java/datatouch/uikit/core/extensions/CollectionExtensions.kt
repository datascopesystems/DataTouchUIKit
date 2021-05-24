package datatouch.uikit.core.extensions

import datatouch.uikit.core.collections.lists.StringList

object CollectionExtensions {

    inline fun <reified U : T, T> Iterable<T>.partitionByType(): Pair<List<U>, List<T>> {
        val first = ArrayList<U>()
        val second = ArrayList<T>()
        for (element in this) {
            if (element is U) first.add(element)
            else second.add(element)
        }
        return Pair(first, second)
    }

    inline fun <T> MutableList<T>.removeWhen(predicate: (T) -> Boolean) {
        for (i in size - 1 downTo 0) {
            if (predicate(this[i])) {
                removeAt(i)
            }
        }
    }

    fun StringList?.allStringNoneEmpty(): List<String> =
            this?.filter { it?.isNotEmpty() == true }.toFilteredMutable()

    fun <T : Any> ArrayList<T?>?.toFilteredMutable() =
            this.orEmpty().filterNotNull().toMutableList()

    fun <T : Any> List<T?>?.toFilteredMutable() =
            this.orEmpty().filterNotNull().toMutableList()

    fun <T> List<T>?.orEmptyMutable(): MutableList<T> = this?.toMutableList() ?: mutableListOf()
}

