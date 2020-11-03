package datatouch.uikit.core.callbacks

typealias UiJustCallback = () -> Unit
typealias UiCallback<K> = (K) -> Unit
typealias UiCallbackList<T> = ArrayList<UiCallback<T>>

typealias UiJustValue<T> = () -> T
typealias UiValueProvider<TParam, TResult> = (TParam) -> TResult

typealias UiCondition<T> = UiValueProvider<T, Boolean>

fun <T> UiCallbackList<T>.executeAll(item: T) {
    this.forEach { it.invoke(item) }
}

object CallbackTypeAliases