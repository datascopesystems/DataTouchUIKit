package datatouch.uikit.components.recyclerview

interface IListAdapterFilterable<TCriterion> {

    fun getSearchString(criterion: TCriterion): String

}