package datatouch.uikit.core.duration

import datatouch.uikit.core.extensions.GenericExtensions.default

object DurationExtensions {

    fun Duration?.orToday() : Duration = this.default(Duration.createForToday())

}