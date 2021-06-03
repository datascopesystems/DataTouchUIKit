package datatouch.uikit.core.cache

interface CacheProvider {
    public suspend fun setString(key: String, value: String)
    public suspend fun getString(key: String, default: String = ""): String
}