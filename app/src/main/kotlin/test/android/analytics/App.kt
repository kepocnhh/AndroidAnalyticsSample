package test.android.analytics

import android.app.Application
import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.eventFlow
import kotlinx.coroutines.launch
import test.android.analytics.module.app.Injection
import test.android.analytics.provider.Analytics
import test.android.analytics.provider.FinalLocals
import test.android.analytics.provider.Locals
import java.util.Date
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal class App : Application() {
    private class MockAnalytics : Analytics {
        private val map = mutableMapOf<String, String>()
        private val messages = mutableMapOf<Duration, String>()

        override fun log(message: String) {
            val now = System.currentTimeMillis().milliseconds
            messages[now] = message
        }

        override fun setCustomKey(key: String, value: String) {
            map[key] = value
        }

        override fun record(error: Throwable) {
            TODO("Not yet implemented: record")
        }

        override fun report(entries: Map<String, String>) {
            val id = UUID.randomUUID()
            val message = """
Event: $id
 entries:${(map + entries).entries.joinToString(prefix = "\n\t", separator = "\n\t")}
 messages:${messages.entries.sortedBy { (k, _) -> k }.joinToString(prefix = "\n\t", separator = "\n\t") { (duration, message) -> "${Date(duration.inWholeMilliseconds)}] $message"}}
            """.trimIndent()
            println(message)
            // todo event
        }
    }

    override fun onCreate() {
        super.onCreate()
        val lifecycle = ProcessLifecycleOwner.get().lifecycle
        /*
        lifecycle.coroutineScope.launch {
            lifecycle.currentStateFlow.collect { state ->
                when (state) {
                    Lifecycle.State.DESTROYED -> TODO()
                    Lifecycle.State.INITIALIZED -> TODO()
                    Lifecycle.State.CREATED -> TODO()
                    Lifecycle.State.STARTED -> TODO()
                    Lifecycle.State.RESUMED -> TODO()
                }
            }
        }
        lifecycle.coroutineScope.launch {
            lifecycle.eventFlow.collect { event ->
                when (event) {
                    Lifecycle.Event.ON_CREATE -> TODO()
                    Lifecycle.Event.ON_START -> TODO()
                    Lifecycle.Event.ON_RESUME -> TODO()
                    Lifecycle.Event.ON_PAUSE -> TODO()
                    Lifecycle.Event.ON_STOP -> TODO()
                    Lifecycle.Event.ON_DESTROY -> TODO()
                    Lifecycle.Event.ON_ANY -> TODO()
                }
            }
        }
        */
        val locals: Locals = FinalLocals(this)
        val analytics: Analytics = MockAnalytics()
        analytics.setCustomKey("brand", Build.BRAND)
        analytics.setCustomKey("model", Build.MODEL)
        analytics.setCustomKey("android", Build.VERSION.SDK_INT.toString())
        analytics.setCustomKey("app:id", BuildConfig.APPLICATION_ID)
        analytics.setCustomKey("build:type", BuildConfig.BUILD_TYPE)
        analytics.setCustomKey("version:name", BuildConfig.VERSION_NAME)
        analytics.setCustomKey("version:code", BuildConfig.VERSION_CODE.toString())
        analytics.setCustomKey("device:id", locals.id.toString())
        _injection = Injection(
            locals = locals,
            analytics = analytics,
        )
    }

    companion object {
        private var _injection: Injection? = null
        val injection: Injection get() = checkNotNull(_injection) { "No injection!" }
    }
}
