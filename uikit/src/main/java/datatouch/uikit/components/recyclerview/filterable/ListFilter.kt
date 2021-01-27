package datatouch.uikit.components.recyclerview.filterable

class ListFilter<TItem : IListAdapterFilterable<TCriterion>, TCriterion> {

    var allData = mutableListOf<TItem>()

    fun invoke(constraint: String, criterion: TCriterion): MutableList<TItem> {
        return allData
            .filter { it.getSearchString(criterion).contains(constraint, true) }
            .toMutableList()
    }
}