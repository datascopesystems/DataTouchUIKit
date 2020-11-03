package datatouch.uikit.components.appbackground

import android.view.View
import datatouch.uikit.components.appbackground.interfaces.IAppBackgroundInjector

abstract class AppBackgroundViewDecorator(protected val view: View?) {
    abstract suspend fun decorateAsync(injector: IAppBackgroundInjector)
}