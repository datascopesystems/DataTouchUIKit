package datatouch.uikit.core.cache

interface CacheProvider {
    public fun setString(key: String, value: String)
    public fun getString(key: String, default: String = ""): String
}