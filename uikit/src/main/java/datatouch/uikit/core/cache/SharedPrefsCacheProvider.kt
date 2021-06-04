package datatouch.uikit.core.cache

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsCacheProvider(
    private val context: Context
) : CacheProvider {
    val sharedPreference: SharedPreferences by lazy {
        context.getSharedPreferences("default", Context.MODE_PRIVATE)
    }

    override fun setString(key: String, value: String) {
        sharedPreference.edit().putString(key, value).apply()
    }

    override fun getString(key: String, default: String): String {
        return sharedPreference.getString(key, default).orEmpty()
    }
}