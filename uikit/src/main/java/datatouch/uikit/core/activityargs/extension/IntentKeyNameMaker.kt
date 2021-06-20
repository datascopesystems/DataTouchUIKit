package datatouch.uikit.core.activityargs.extension


private const val KEY_PREFIX = "uikit.extra"

// Key names for intent extras should starts with package name
internal fun makeIntentKeyName(key: String): String {
    return "$KEY_PREFIX.$key"
}