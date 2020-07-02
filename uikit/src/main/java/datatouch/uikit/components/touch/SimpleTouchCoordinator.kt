package datatouch.uikit.components.touch

class SimpleTouchCoordinator :
    ViewTouchCoordinator {
    override fun isLongClickAllowed() = true
    override fun isClickAllowed() = true
}