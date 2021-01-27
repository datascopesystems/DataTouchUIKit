package datatouch.uikit.components.recyclerview.filterable

interface IListAdapterFilterable<TCriterion> {

    fun getSearchString(criterion: TCriterion): String

}