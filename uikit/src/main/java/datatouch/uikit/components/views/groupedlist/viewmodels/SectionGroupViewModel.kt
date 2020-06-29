package datatouch.uikit.components.views.groupedlist.viewmodels

class SectionGroupViewModel {
    var id = 0
    var title: String? = null
        private set
    var iconResourceId = 0
    var items: List<SectionGroupItemViewModel>? = null

    constructor() {}
    constructor(id: Int, name: String?, iconResourceId: Int) {
        this.id = id
        title = name
        this.iconResourceId = iconResourceId
    }

    fun setName(name: String?) {
        title = name
    }

}