package test.android.analytics.provider

internal interface Analytics {
    fun log(message: String)
    fun setCustomKey(key: String, value: String)
    fun record(error: Throwable)
    fun report(entries: Map<String, String>)
}
