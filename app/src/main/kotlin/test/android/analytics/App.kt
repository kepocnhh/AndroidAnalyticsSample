package test.android.analytics

import android.app.Application
import android.os.Build
import androidx.lifecycle.ProcessLifecycleOwner
import test.android.analytics.module.app.Injection
import test.android.analytics.provider.Analytics
import test.android.analytics.provider.FinalAnalytics
import test.android.analytics.provider.FinalLocals
import test.android.analytics.provider.Locals

internal class App : Application() {
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
        val analytics: Analytics = FinalAnalytics(this)
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
