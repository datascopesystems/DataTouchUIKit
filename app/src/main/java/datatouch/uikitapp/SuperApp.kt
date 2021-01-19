package datatouch.uikitapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class SuperApp : Application() {

    override fun onCreate() {
        // Needed to test on API 19 and below
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        super.onCreate()
    }

}