package datatouch.activities.custom.components.listitems

interface ItemOnClickListener<T> {
    fun click(src: T)
}