package datatouch.uikit.components.appbackground.interfaces

import android.view.View

interface IAppBackgroundInjector {

    suspend fun showNormalBackgroundAsync(view: View)

    suspend fun showBlurredBackgroundAsync(view: View)

}