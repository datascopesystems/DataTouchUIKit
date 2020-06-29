package datatouch.uikit.components.views.groupedlist.viewmodels

class SectionGroupItemViewModel {
    var id = 0
    var name: String? = null
    var description: String? = null
    var iconResourceId = 0

    constructor() {}
    constructor(id: Int, name: String?, description: String?, iconResourceId: Int) {
        this.id = id
        this.name = name
        this.description = description
        this.iconResourceId = iconResourceId
    }

}