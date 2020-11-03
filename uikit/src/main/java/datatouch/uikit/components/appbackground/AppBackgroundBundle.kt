package datatouch.uikit.components.appbackground

import datatouch.uikit.components.appbackground.interfaces.IAppBackgroundInjector

class AppBackgroundBundle(private val injector: IAppBackgroundInjector) {

    private val decorators: MutableList<AppBackgroundViewDecorator>
            by lazy { mutableListOf<AppBackgroundViewDecorator>() }

    fun add(decorator: AppBackgroundViewDecorator) {
        decorators.add(decorator)
    }

    suspend fun decorateAsync() {
        decorators.forEach { it.decorateAsync(injector) }
    }

    fun clear() {
        decorators.clear()
    }
}