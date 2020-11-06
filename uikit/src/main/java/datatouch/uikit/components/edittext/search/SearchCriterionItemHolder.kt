package datatouch.uikit.components.edittext.search

data class SearchCriterionItemHolder<TCriterion>(
    override val name: String,
    override val iconResId: Int,
    val criterion: TCriterion
) : ISearchCriterionDropDownListAdapterItem {

}