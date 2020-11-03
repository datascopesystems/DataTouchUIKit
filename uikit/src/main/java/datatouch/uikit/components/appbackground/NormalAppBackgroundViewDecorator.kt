package datatouch.uikit.components.appbackground

import android.view.View
import datatouch.uikit.components.appbackground.interfaces.IAppBackgroundInjector

class NormalAppBackgroundViewDecorator(view: View?) : AppBackgroundViewDecorator(view) {

    override suspend fun decorateAsync(injector: IAppBackgroundInjector) {
        view?.apply { injector.showNormalBackgroundAsync(this) }
    }
}