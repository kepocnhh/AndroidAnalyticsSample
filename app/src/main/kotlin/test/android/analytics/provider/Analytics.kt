package test.android.analytics.provider

import java.util.UUID

internal interface Analytics {
    data class Event(
        val id: UUID,
    )

    fun log(message: String)
    fun setCustomKey(key: String, value: String)
    fun record(error: Throwable)
    fun report(event: Event)
}
